package com.luisansal.jetpack.core.domain.firebasedao

class UserDao(
    var dni: String = "",
    var names: String = "",
    var lastNames: String = "",
    var fcbkId: String? = null,
    var address: String = "",
    var email: String = "",
    var emailConfirmation: Boolean = false,
    var emailVerifiedAt: Long = 0,
    var filePath: String? = null,
    var createdAt: Long = 0,
    var updatedAt: Long? = null,
    val password: String = ""
)
