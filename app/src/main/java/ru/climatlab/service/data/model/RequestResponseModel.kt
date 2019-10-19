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
    @SerializedName("comment")
    val comment: String?,
    val status: RequestStatus?,
    @SerializedName("latlng") val latlng: String?,
    @SerializedName("client_info") val clientInfo: ClientResponseModel?
) {
    val date: Long get() = _date * 1000
}