package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class MapCoordinates(@SerializedName ("lat")val latitude: Double = 0.0, @SerializedName ("lng")val longitude: Double = 0.0)
