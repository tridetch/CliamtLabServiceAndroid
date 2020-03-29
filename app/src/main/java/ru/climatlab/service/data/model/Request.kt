package ru.climatlab.service.data.model

/**
 * Created by tridetch on 14.04.2019. CliamtLabService
 */
data class Request(
    val id: String,
    val date: Long,
    val equipmentId: String,
    val type: RequestType,
    val office: String,
    val address: String,
    val addressDetails: String,
    val description: String,
    val comment: String,
    val status: RequestStatus,
    val latlng: String?,
    val clientInfo: ClientResponseModel?
){
    fun getCoordinates(): Coordinates {
        var mapCoordinates = Coordinates(44.055200,  42.982851)
        try {
            val coordinates = latlng?.split(regex = Regex(","), limit = 0)
            val lat = coordinates?.get(0)?.toDouble()
            val lng = coordinates?.get(1)?.toDouble()
            if (lat != null && lng != null) {
                mapCoordinates = Coordinates(lat, lng)
            }
        } catch (ignore: Exception) {}
        return mapCoordinates
    }

}
