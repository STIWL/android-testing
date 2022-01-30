package com.luisansal.jetpack.core.data.mappers

import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.domain.firebasedao.UserDao

fun UserEntity.toDao() = UserDao(
    dni = this.dni,
    names = this.names,
    lastNames = this.lastNames,
    email = this.email
)