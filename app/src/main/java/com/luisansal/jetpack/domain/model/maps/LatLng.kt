package com.luisansal.jetpack.domain.model.maps

data class LatLng(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)

fun LatLng.toGLatLng() = com.google.android.gms.maps.model.LatLng(this.latitude, this.longitude)

fun com.google.android.gms.maps.model.LatLng.toLatLng() = LatLng(this.latitude,this.longitude)