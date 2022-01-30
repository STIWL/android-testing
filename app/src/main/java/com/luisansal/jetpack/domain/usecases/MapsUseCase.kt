package com.luisansal.jetpack.domain.usecases

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.luisansal.jetpack.core.data.Result
import com.luisansal.jetpack.core.data.mappers.toDao
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.domain.firebasedao.UserDao
import com.luisansal.jetpack.data.datastore.MapsCloudStore
import com.luisansal.jetpack.data.repository.MapsRepository
import com.luisansal.jetpack.domain.entity.Place
import com.luisansal.jetpack.domain.firebasedao.VisitDao
import com.luisansal.jetpack.domain.model.maps.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class MapsUseCase(
    private val mapsRespository: MapsRepository, private val mapsCloudStore: MapsCloudStore,
    private val markersRef: DatabaseReference
) {
    suspend fun sendPosition(message: String, latiude: Double, longitude: Double): Result<Boolean> {
        return mapsRespository.sendPosition(message, latiude, longitude)
    }

    suspend fun sendPositionFirebase(userEntity: UserEntity, message: String, latitude: Double, longitude: Double): Task<Void> =
        withContext(Dispatchers.IO) {
            val visitDao = VisitDao(
                message = message,
                location = LatLng(latitude, longitude),
                user = userEntity.toDao()
            )
            markersRef.child(userEntity.id).setValue(visitDao)
        }

    suspend fun getPositions(positions: (List<VisitDao?>) -> Unit, exception: (Exception) -> Unit) = withContext(Dispatchers.IO) {
        markersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                positions(snapshot.children.map {
                    val visit = it.getValue(VisitDao::class.java)
                    visit
                })
            }

            override fun onCancelled(error: DatabaseError) {
                exception(error.toException())
            }
        })
    }

    suspend fun getPlaces(query: String): Result<List<Place>> {
        return mapsCloudStore.getPlaces(query)
    }

    suspend fun getDirections(origin: String, destination: String): Result<List<com.google.android.gms.maps.model.LatLng>> {
        return mapsCloudStore.getDirections(origin, destination)
    }
}
