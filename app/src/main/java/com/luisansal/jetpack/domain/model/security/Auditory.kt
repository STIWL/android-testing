package com.luisansal.jetpack.domain.model.security

import com.luisansal.jetpack.core.domain.entity.UserEntity
import java.util.Date

abstract class Auditory {

    var createdBy: UserEntity? = null

    var modifiedBy: UserEntity? = null

    var dateCreated = Date()

    var dateModified: Date? = null

    var status: Boolean? = true
}
