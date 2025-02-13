package com.luisansal.jetpack.data.mappers

import com.luisansal.jetpack.data.network.response.LoginResponse
import com.luisansal.jetpack.core.domain.entity.UserEntity

class UserResponseMapper {
    fun map(userRresponse : LoginResponse.UserResponse) = UserEntity(
            id = userRresponse.id,
            names = userRresponse.names,
            lastNames = userRresponse.lastNames,
            dni = userRresponse.dni,
            email = userRresponse.email,
            fcbkId = userRresponse.fcbkId,
            filePath = userRresponse.filePath,
            address = userRresponse.address,
            emailConfirmation = userRresponse.emailConfirmation,
            emailVerifiedAt = userRresponse.emailVerifiedAt,
            createdAt = userRresponse.createdAt,
            updatedAt = userRresponse.updatedAt
    )
}