package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
data class RequestResponseModel(
    @SerializedName("id") val id: String,
    @SerializedName("client") val clientId: String?,
    @SerializedName("equipment") val equipmentId: String?,
    @SerializedName("date_start") private val _date: Long,
    val type: RequestType?,
    val office: String?,
    @SerializedName("adress") val address: String?,
    @SerializedName("adress2") val addressDetails: String?,
    val description: String?,
    val comment: String?,
    val status: RequestStatus?,
    @SerializedName("latlng") val latlng: String?
) {
    val date: Long get() = _date * 1000
    fun getCoordinates(): MapCoordinates {
        var mapCoordinates = MapCoordinates(44.055200,  42.982851)
        try {
            val coordinates = latlng?.split(regex = Regex(","), limit = 0)
            val lat = coordinates?.get(0)?.toDouble()
            val lng = coordinates?.get(1)?.toDouble()
            if (lat != null && lng != null) {
                mapCoordinates = MapCoordinates(lat, lng)
            }
        } catch (ignore: Exception) {}
        return mapCoordinates
    }
}