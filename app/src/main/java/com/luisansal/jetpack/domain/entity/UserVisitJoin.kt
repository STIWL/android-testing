package com.luisansal.jetpack.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.luisansal.jetpack.core.domain.entity.UserEntity

@Entity(tableName = "tbluser_tblvisit_join",
        primaryKeys = arrayOf("userId","visitId"),
        foreignKeys = arrayOf(
                ForeignKey(entity = UserEntity::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("userId")),
                ForeignKey(entity = VisitEntity::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("visitId"))
        )
)
data class UserVisitJoin(val userId: String,
                         val visitId: Long)
