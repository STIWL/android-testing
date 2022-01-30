package com.luisansal.jetpack.features.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisansal.jetpack.core.base.BaseViewModel
import com.luisansal.jetpack.core.data.Result
import com.luisansal.jetpack.core.data.preferences.AuthSharedPreferences
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.domain.exceptions.NotFoundException
import com.luisansal.jetpack.core.domain.exceptions.TransactionCanceledException
import com.luisansal.jetpack.core.utils.EMPTY
import com.luisansal.jetpack.domain.entity.VisitEntity
import com.luisansal.jetpack.domain.model.maps.LatLng
import com.luisansal.jetpack.domain.usecases.MapsUseCase
import com.luisansal.jetpack.domain.usecases.UserUseCase
import com.luisansal.jetpack.domain.usecases.VisitUseCase
import com.luisansal.jetpack.features.maps.model.MarkerUserVisitMapModel
import kotlinx.coroutines.launch

class MapsViewModel(
    private val userUseCase: UserUseCase, private val visitUseCase: VisitUseCase, private val mapsUseCase: MapsUseCase,
    private val authSharedPreferences: AuthSharedPreferences
) : BaseViewModel() {

    val mapViewState = MutableLiveData<MapsViewState>()
    var email = authSharedPreferences.email ?: EMPTY

    fun getVisits() {
        mapViewState.postValue(MapsViewState.LoadingState())

        uiScope.launch {
            try {
                val user = userUseCase.getUser(email)
                val mapsViewModel = visitUseCase.getByUser(email)?.let { user?.let { it1 -> MarkerUserVisitMapModel(it, it1) } }
                mapViewState.postValue(MapsViewState.SuccessVisistsState(mapsViewModel))
            } catch (exception: Exception) {
                mapViewState.postValue(MapsViewState.ErrorState(exception))
            }

        }
    }

    fun saveOneVisitForUser(visitEntity: VisitEntity, userId: String) {

        mapViewState.postValue(MapsViewState.LoadingState())

        uiScope.launch {
            try {
                mapViewState.postValue(MapsViewState.SuccessUserSavedState(visitUseCase.saveOneVisitForUser(visitEntity, userId)))
            } catch (exception: Exception) {
                mapViewState.postValue(MapsViewState.ErrorState(exception))
            }
        }
    }

    fun sendPosition(message: String, latiude: Double, longitude: Double) {
        mapViewState.postValue(MapsViewState.LoadingState())

        uiScope.launch {
            when (val result = mapsUseCase.sendPosition(message, latiude, longitude)) {
                is Result.Success -> {
                    mapViewState.postValue(MapsViewState.SuccessSendPosition(result.data ?: false))
                }
            }
        }
    }


    fun sendPositionFirebase(latiude: Double, longitude: Double) {
        mapViewState.postValue(MapsViewState.LoadingState())

        uiScope.launch {
            val message = authSharedPreferences.names
            val userEntity = UserEntity(
                email = authSharedPreferences.email ?: EMPTY,
                names = authSharedPreferences.names ?: EMPTY,
                lastNames = authSharedPreferences.lastnames ?: EMPTY,
            )
            mapsUseCase.sendPositionFirebase(userEntity, "hola soy $message y estoy enviandote un mensaje", latiude, longitude)
                .addOnCompleteListener {
                    mapViewState.postValue(MapsViewState.SuccessSendPosition(true))
                }.addOnCanceledListener {
                    mapViewState.postValue(MapsViewState.ErrorState(TransactionCanceledException()))
                }
        }
    }

    fun getPositionsFirebase() {
        uiScope.launch {
            mapsUseCase.getPositions({
                mapViewState.postValue(MapsViewState.SuccessVisistsState(MarkerUserVisitMapModel(it.mapIndexed { index, value ->
                    VisitEntity(
                        id = index.toLong(),
                        location = value?.location ?: LatLng(0.0, 0.0),
                        message = value?.message ?: EMPTY
                    )
                }, null)))
            }) {
                errorDialog.postValue(it)
            }
        }
    }
}

