package com.luisansal.jetpack.features.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.luisansal.jetpack.R
import com.luisansal.jetpack.core.base.BaseBindingFragment
import com.luisansal.jetpack.core.data.preferences.AuthSharedPreferences
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.domain.exceptions.RequestPlacesDeniedException
import com.luisansal.jetpack.core.utils.bitmapDescriptorFromVector
import com.luisansal.jetpack.core.utils.hideKeyboardFrom
import com.luisansal.jetpack.core.utils.injectFragment
import com.luisansal.jetpack.data.preferences.UserSharedPreferences
import com.luisansal.jetpack.databinding.FragmentMapsBinding
import com.luisansal.jetpack.domain.entity.Place
import com.luisansal.jetpack.domain.entity.VisitEntity
import com.luisansal.jetpack.domain.network.ApiService
import com.luisansal.jetpack.domain.network.ApiService.Companion.PUSHER_API_KEY
import com.luisansal.jetpack.features.TempData
import com.luisansal.jetpack.features.manageusers.viewmodel.UserViewModel
import com.luisansal.jetpack.features.maps.viewmodels.MapsSearchViewModel
import com.luisansal.jetpack.features.maps.viewmodels.MapsSearchViewState
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFirebaseFragment : BaseBindingFragment(), OnMapReadyCallback {

    companion object {
        private val TAG = MapsFirebaseFragment::class.java.name
        private const val DEFAULT_ZOOM = 18f
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        const val MARKER_ON_MAP_ID = "-1"
        const val MARKER_ON_MAP_NO_RESULTS_ID = "-2"
        const val LOCATION_ORIGIN = "LOCATION_ORIGIN"
        const val LOCATION_DESTINATION = "LOCATION_DESTINATION"
    }

    private val binding by lazy {
        FragmentMapsBinding.inflate(layoutInflater).apply { lifecycleOwner = this@MapsFirebaseFragment }
    }

    private val mViewModel: MapsViewModel by injectFragment()
    private val viewModelSearch: MapsSearchViewModel by injectFragment()

    private var mGoogleMap: GoogleMap? = null

    private var mLocationPermissionGranted: Boolean = false
    private var mLastKnownLocation: Location? = null

    // Construct a FusedLocationProviderClient.
    private val mFusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }

    // Construct a PlaceDetectionClient.
    private val mPlaceDetectionClient by lazy { Places.getPlaceDetectionClient(requireActivity(), null) }

    // Construct a GeoDataClient.
    private val locationManager by lazy { requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager }
    private val userSharedPreferences by inject<UserSharedPreferences>()
    private var lastUserMarker: Marker? = null
    private var lastOtherMarker: Marker? = null
    private var polyline: Polyline? = null
    private val mapsViewModel by viewModel<MapsViewModel>()
    private val markerOptions by lazy {
        MarkerOptions().icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_jetpack_marker).bitmapDescriptorFromVector())
    }
    private val markerOptionsCocheEcologico by lazy {
        MarkerOptions().icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_coche_ecologico).bitmapDescriptorFromVector())
    }

    private val autoCompleteAdapter1 by lazy { PlaceAdapter(requireContext()) }
    private val autoCompleteAdapter2 by lazy { PlaceAdapter(requireContext()) }
    override fun getViewResource() = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mViewModel
        val mapView = binding.wrapMap as MapView?
        mapView?.onCreate(savedInstanceState)
        mapView?.onResume()

        mapView?.getMapAsync(this)

        binding.acBuscarLugares?.setAdapter(autoCompleteAdapter1)
        binding.acBuscarLugares2?.setAdapter(autoCompleteAdapter2)
        mViewModel.mapViewState.observe(::getLifecycle, ::mapsObserve)
        viewModelSearch.viewState.observe(::getLifecycle, ::searchMapObserve)
        initBroadcast()
    }

    private val placeToMap = Place(MARKER_ON_MAP_ID, "", LatLng(0.0, 0.0), "", "", "Seleccione en mapa")
    private fun searchMapObserve(viewState: MapsSearchViewState) {
        when (viewState) {
            is MapsSearchViewState.SuccessState -> {
                val newData = ArrayList(viewState.data)
                newData.add(placeToMap)
                if (viewState.data.isEmpty()) {
                    newData.add(Place(MARKER_ON_MAP_NO_RESULTS_ID, "", LatLng(0.0, 0.0), "", "", "No hay resultados"))
                }
                if (viewModelSearch.currectLocation == LOCATION_ORIGIN) {
                    autoCompleteAdapter1.items = newData
                }
                if (viewModelSearch.currectLocation == LOCATION_DESTINATION) {
                    autoCompleteAdapter2.items = newData
                }
            }
            is MapsSearchViewState.ErrorState -> {
                when (viewState.e) {
                    is RequestPlacesDeniedException -> {
                        val data = mutableListOf<Place>()
                        data.add(placeToMap)
                        if (viewModelSearch.currectLocation == LOCATION_ORIGIN) {
                            autoCompleteAdapter1.items = data
                        }
                        if (viewModelSearch.currectLocation == LOCATION_DESTINATION) {
                            autoCompleteAdapter2.items = data
                        }
                    }
                }
                Toast.makeText(context, viewState.e.message, Toast.LENGTH_SHORT).show()
            }
            is MapsSearchViewState.SuccessDirectionsState -> {
                val list = ArrayList(viewState.data)
                TempData.addressLocationDestination?.let {
                    list.add(it)
                }
                polyline = mGoogleMap?.addPolyline(
                    PolylineOptions()
                        .clickable(true)
                        .addAll(list)
                )
            }
            else -> Unit
        }
    }

    private fun initBroadcast() {

        try {
            mViewModel.getPositionsFirebase()
        } catch (e: java.lang.Exception) {
            Log.i("subscribed", "$e")
        }
    }

    fun sendRealTracking(location: Location?) {
        mapsViewModel.sendPositionFirebase(
            location?.latitude ?: 0.0, location?.longitude ?: 0.0
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney, Australia, and move the camera.
        mGoogleMap = googleMap

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        onClickBtnMostrarRuta()
        onACBuscarVisitasTextChanged()
        onACBuscarLugaresTextChanged()
        onACBuscarLugares2TextChanged()
        onClickBtnCurrentLocation()
        //onClickMap()
        initTracking()
    }

    private fun initTracking() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    this@MapsFirebaseFragment.onLocationChanged(location)
                    sendRealTracking(location)
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit
                override fun onProviderEnabled(p0: String) = Unit
                override fun onProviderDisabled(p0: String) = Unit
            })
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    this@MapsFirebaseFragment.onLocationChanged(location)
                    sendRealTracking(location)
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit
                override fun onProviderEnabled(p0: String) = Unit
                override fun onProviderDisabled(p0: String) = Unit
            })
        }
    }

    fun onLocationChanged(location: Location?) {
        val user = UserViewModel.userEntity
        location?.let { location ->
            lastUserMarker?.remove()
            lastUserMarker =
                mGoogleMap?.addMarker(markerOptions.position(LatLng(location.latitude, location.longitude)).title("User: " + user?.names))
        }
    }

    private fun onClickBtnCurrentLocation() {
        binding.imvCurrentLocation.setOnClickListener {
            getDeviceLocation()
        }
    }

    private fun onClickBtnMostrarRuta() {
        binding.btnMostrarRutas?.setOnClickListener {
            mGoogleMap?.clear()
            TempData.addressLocation?.let {
                mGoogleMap?.addMarker(markerOptions.position(it))
                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM))
            }
            TempData.addressLocationDestination?.let {
                mGoogleMap?.addMarker(markerOptionsCocheEcologico.position(it))
            }
            polyline?.remove()
            val origin = "${TempData.addressLocation?.latitude},${TempData.addressLocation?.longitude}"
            val destination = "${TempData.addressLocationDestination?.latitude},${TempData.addressLocationDestination?.longitude}"
            viewModelSearch.getDirections(origin, destination)
        }
    }

    private fun onClickMap() {
        mGoogleMap?.setOnMapClickListener { location ->
            mGoogleMap?.clear()
            lastUserMarker = mGoogleMap?.addMarker(markerOptions.position(location).title("User: " + UserViewModel.userEntity?.names))

            val visit = VisitEntity(location = com.luisansal.jetpack.domain.model.maps.LatLng(location.latitude, location.longitude))
            UserViewModel.userEntity?.id?.let { mViewModel.saveOneVisitForUser(visit, it) }
        }
    }

    private fun mapsObserve(mapsViewState: MapsViewState) {
        when (mapsViewState) {
            is MapsViewState.ErrorState -> {
                Toast.makeText(context, mapsViewState.error?.message, Toast.LENGTH_SHORT).show()
            }
            is MapsViewState.LoadingState -> {
                Log.d("Loading", "Loading")
            }
            is MapsViewState.SuccessUserSavedState -> {
                val response = mapsViewState.data
                if (response)
                    Toast.makeText(context, "Posición guardada", Toast.LENGTH_LONG).show()
            }
            is MapsViewState.SuccessVisistsState -> {
                mGoogleMap?.clear()
                afterClickShowVisits()

                val response = mapsViewState.data
                if (response != null)
                    for (visit in response.visitEntities) {
                        mGoogleMap?.addMarker(
                            markerOptionsCocheEcologico.position(LatLng(visit.location.latitude, visit.location.longitude))
                                .title("User: " + response.userEntity?.names)
                        )
//                        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(visit.location.latitude, visit.location.longitude), 5f))
                    }
            }
            else -> Unit
        }
    }

    private fun afterClickShowVisits() {
        binding.acBuscarVisitas.hideKeyboardFrom(requireContext())
        binding.acBuscarVisitas.clearFocus()
    }

    private fun onACBuscarLugaresTextChanged() {
        binding.acBuscarLugares?.addTextChangedListener {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModelSearch.getPlaces(it.toString())
                viewModelSearch.currectLocation = LOCATION_ORIGIN
            }, 700)
        }
        binding.acBuscarLugares?.setOnItemClickListener { adapterView, _, position, _ ->
            val adapter = (adapterView.adapter as PlaceAdapter)
            val place = adapter.getItem(position)
            autoCompleteAdapter1.selected = place
            if (place.id != MARKER_ON_MAP_ID) {
                pickOnPlace(place)
            } else {
                binding.acBuscarLugares.setText("")
                startActivity(MapsActivity.newInstance(requireContext(), LOCATION_ORIGIN))
            }
        }
    }

    private fun onACBuscarLugares2TextChanged() {
        binding.acBuscarLugares2?.addTextChangedListener {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModelSearch.getPlaces(it.toString())
                viewModelSearch.currectLocation = LOCATION_DESTINATION
            }, 700)
        }
        binding.acBuscarLugares2?.setOnItemClickListener { adapterView, _, position, _ ->
            val adapter = (adapterView.adapter as PlaceAdapter)
            val place = adapter.getItem(position)
            autoCompleteAdapter2.selected = place
            if (place.id != MARKER_ON_MAP_ID) {
                pickOnPlace(place)
            } else {
                binding.acBuscarLugares2.setText("")
                startActivity(MapsActivity.newInstance(requireContext(), LOCATION_DESTINATION))
            }
        }
    }

    private fun pickOnPlace(place: Place) {
        if (viewModelSearch.currectLocation == LOCATION_ORIGIN) {
            TempData.addressLocation = place.latLng
            lastUserMarker?.remove()
            mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, DEFAULT_ZOOM))
            lastUserMarker = mGoogleMap?.addMarker(markerOptions.position(place.latLng).title(TempData.user.names))
        }
        if (viewModelSearch.currectLocation == LOCATION_DESTINATION) {
            TempData.addressLocationDestination = place.latLng
            lastOtherMarker?.remove()
            mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, DEFAULT_ZOOM))
            lastOtherMarker = mGoogleMap?.addMarker(markerOptionsCocheEcologico.position(place.latLng).title(TempData.user.names))
        }

        hideKeyboard(binding.root)
    }

    private fun onACBuscarVisitasTextChanged() {
        binding.acBuscarVisitas?.addTextChangedListener {
            //TODO()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                mLocationPermissionGranted = true
                updateLocationUI()
                getDeviceLocation()
            }
        }
    }

    private fun updateLocationUI() {
        checkLocationPermission()
        if (mGoogleMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap?.isMyLocationEnabled = true
                mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mGoogleMap?.isMyLocationEnabled = false
                mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
                requestPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message ?: "")
        }
    }

    private fun getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                requestLocation()
                mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    mLastKnownLocation = location
                    mLastKnownLocation?.latitude?.let {
                        mLastKnownLocation?.longitude?.let { it1 ->
                            mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it, it1), DEFAULT_ZOOM))
                            lastUserMarker?.remove()
                            lastUserMarker = mGoogleMap?.addMarker(
                                markerOptions.position(LatLng(it, it1)).title(UserViewModel.userEntity?.names)
                            )
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message ?: "")
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.numUpdates = 1
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) = Unit
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    override fun onResume() {
        super.onResume()
        binding.acBuscarLugares.setText(TempData.address)
        binding.acBuscarLugares2.setText(TempData.addressDestination)
        if (LOCATION_ORIGIN == TempData.lastLocation) {
            val location = TempData.addressLocation
            location?.let {
                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM))
                lastUserMarker?.remove()
                lastUserMarker = mGoogleMap?.addMarker(markerOptions.position(it).title(TempData.user.names))
            }
        }

        if (LOCATION_DESTINATION == TempData.lastLocation) {
            val location2 = TempData.addressLocationDestination
            location2?.let {
                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM))
                lastOtherMarker?.remove()
                lastOtherMarker = mGoogleMap?.addMarker(markerOptionsCocheEcologico.position(it).title(TempData.user.names))
            }
        }
    }

    //    private fun initAutoComplete() {
//        // Initialize Places.
//        if (!Places.isInitialized()) {
//            Places.initialize(requireContext(), getString(R.string.google_maps_key))
//        }
//
//        // Create a new Places client instance.
//        val placesClient: PlacesClient = Places.createClient(requireContext())
//
//        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
//
//        // Specify the types of place data to return.
//
//        // Specify the types of place data to return.
//        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
//
//        // Set up a PlaceSelectionListener to handle the response.
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                Log.i(TAG, "Place: " + place.name.toString() + ", " + place.id)
//                lastUserMarker?.remove()
//                lastUserMarker = mGoogleMap?.addMarker(
//                    place.latLng?.let { markerOptions.position(it).title("User: " + MemberShipAfiliation.user.names) }
//                )
//                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, DEFAULT_ZOOM))
//            }
//
//            override fun onError(@NotNull status: Status) {
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })
//    }
}
