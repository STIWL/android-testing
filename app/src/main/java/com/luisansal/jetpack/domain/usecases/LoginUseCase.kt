package com.luisansal.jetpack.domain.usecases

import com.luisansal.jetpack.data.datastore.AuthCloudStore
import com.luisansal.jetpack.core.data.Result
import com.luisansal.jetpack.core.domain.entity.UserEntity

class LoginUseCase(val authCloudStore: AuthCloudStore) {

    // in-memory cache of the loggedInUser object
    var userEntity: UserEntity? = null
        private set

    val isLoggedIn: Boolean
        get() = userEntity != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        userEntity = null
    }

    suspend fun logout() {
        userEntity = null
        authCloudStore.logout()
    }

    suspend fun login(username: String, password: String): Result<UserEntity> {
        // handle login
        val result = authCloudStore.login(username, password)
        if (result is Result.Success) {
            result.data?.let { setLoggedInUser(it) }
        }

        return result
    }

    private fun setLoggedInUser(userEntity: UserEntity) {
        this.userEntity = userEntity
    }
}