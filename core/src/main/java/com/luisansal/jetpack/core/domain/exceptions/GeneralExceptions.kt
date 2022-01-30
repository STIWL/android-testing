package com.luisansal.jetpack.core.domain.exceptions

import com.luisansal.jetpack.core.domain.entity.UserEntity

class UserExistException(val userEntity: UserEntity) : Exception()
open class AnalyticsException(mensaje: String = "No se pudo crear tag") :
    Exception(mensaje)

class AnalyticsEventException(mensaje: String = "No se pudo crear tag de evento") :
    AnalyticsException(mensaje)

class CreateUserValidationException : Exception()

class DniValidationException : Exception()

class RequestDirectionsDeniedException(message: String) : Exception(message)
class RequestPlacesDeniedException(message: String) : Exception(message)