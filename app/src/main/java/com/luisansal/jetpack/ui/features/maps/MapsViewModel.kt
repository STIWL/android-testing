package com.luisansal.jetpack.ui.features.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisansal.jetpack.domain.entity.Visit
import com.luisansal.jetpack.domain.usecases.UserUseCase
import com.luisansal.jetpack.domain.usecases.VisitUseCase
import com.luisansal.jetpack.ui.features.maps.model.MarkerUserVisitMapModel
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsViewModel(private val userUseCase: UserUseCase,private val visitUseCase: VisitUseCase) : ViewModel() {

    val mapViewState = MutableLiveData<MapsViewState>()
    val saveVisitUserViewState = MutableLiveData<MapsViewState>()

    fun getVisits(dni: String) {
        mapViewState.postValue(MapsViewState.LoadingState())

        viewModelScope.launch {
            try {
                val user = userUseCase.getUser(dni);
                val mapsViewModel = visitUseCase.getByUser(dni)?.let { user?.let { it1 -> MarkerUserVisitMapModel(it, it1) } }
                mapViewState.postValue(MapsViewState.SuccessVisistsState(mapsViewModel))
            } catch (exception: Exception) {
                mapViewState.postValue(MapsViewState.ErrorState(exception))
            }

        }
    }

    fun saveOneVisitForUser(visit: Visit, userId : Long){

        saveVisitUserViewState.postValue(MapsViewState.LoadingState())

        viewModelScope.launch {
            try{
                saveVisitUserViewState.postValue(MapsViewState.SuccessUserSavedState(visitUseCase.saveOneVisitForUser(visit,userId)))
            } catch (exception : Exception){
                saveVisitUserViewState.postValue(MapsViewState.ErrorState(exception))
            }
        }

    }
}

