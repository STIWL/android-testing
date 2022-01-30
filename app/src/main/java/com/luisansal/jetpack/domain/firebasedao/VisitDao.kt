package com.luisansal.jetpack.domain.firebasedao

import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.domain.firebasedao.UserDao
import com.luisansal.jetpack.domain.model.maps.LatLng

data class VisitDao(
    val location: LatLng = LatLng(0.0, 0.0),
    val message: String = "",
    val user: UserDao? = null
)