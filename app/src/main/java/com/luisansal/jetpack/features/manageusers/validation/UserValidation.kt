package com.luisansal.jetpack.features.manageusers.validation

import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.usecases.UserUseCase

class UserValidation(private val userUserUseCase: UserUseCase) {
    companion object {
        fun validateUserToCreate(userEntity: UserEntity): Boolean {
            return userEntity.dni.length == 8 && !userEntity.names.equals("") && !userEntity.lastNames.equals("")
        }
        fun validateDni(dni: String): Boolean {
            return dni.length == 8
        }
    }
}