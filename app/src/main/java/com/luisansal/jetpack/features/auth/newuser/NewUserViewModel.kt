package com.luisansal.jetpack.features.auth.newuser

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.luisansal.jetpack.core.base.BaseViewModel
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.utils.EMPTY
import com.luisansal.jetpack.domain.usecases.UserUseCase
import kotlinx.coroutines.launch
import java.util.*

class NewUserViewModel(private val activity: Activity, private val userUseCase: UserUseCase) : BaseViewModel() {

    var email = ""
    var names = ""
    var lastNames = ""
    var password = ""
    var userSaved = MutableLiveData(false)

    fun onClickSave() {
        showLoading.value = true
        uiScope.launch {
            val id = UUID.randomUUID().toString()
            val user = UserEntity(
                id = id,
                email = email,
                names = names,
                lastNames = lastNames,
                password = password
            )
            userUseCase.newAuthUser(activity, user, {
                userSaved.postValue(true)
                showLoading.postValue(false)
            }) {
                errorDialog.postValue(it)
                showLoading.postValue(false)
            }
        }
    }
}