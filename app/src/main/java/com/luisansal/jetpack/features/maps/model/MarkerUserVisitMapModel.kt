package com.luisansal.jetpack.features.maps.model

import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.entity.VisitEntity

class MarkerUserVisitMapModel (val visitEntities: List<VisitEntity>,
                               val userEntity : UserEntity?
)