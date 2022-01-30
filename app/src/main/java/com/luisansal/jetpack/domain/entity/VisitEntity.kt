package com.luisansal.jetpack.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luisansal.jetpack.domain.model.maps.LatLng

// Para firebase me fuerza a que todos mis campos esten llenos o si es null = null
@Entity(tableName = "tblvisit")
data class VisitEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0,
        val location: LatLng = LatLng(0.0,0.0),
        val message: String = ""
)