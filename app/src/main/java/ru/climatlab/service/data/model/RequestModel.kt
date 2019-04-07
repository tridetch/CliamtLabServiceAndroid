package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
data class RequestModel(
    @SerializedName("id") val id: String,
    val coordinates: MapCoordinates,
    @SerializedName("client")val clientId: String,
    @SerializedName("equipment")val equipmentId: String,
    val type: RequestType,
    val office: String,
    val address: String,
    val description: String,
    val status: RequestStatus
)