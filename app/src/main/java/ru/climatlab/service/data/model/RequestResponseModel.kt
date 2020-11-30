package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
data class RequestResponseModel(
    @SerializedName("id") val id: String,
    @SerializedName("client") val clientId: String?,
    @SerializedName("equipment") val equipment: String?,
    @SerializedName("boilerBrand") val boilerBrand: String?,
    @SerializedName("boilerModel") val boilerModel: String?,
    @SerializedName("date_start") private val _date: Long,
    @SerializedName("type") val type: RequestType?,
    @SerializedName("office") val office: String?,
    @SerializedName("adress") val address: String?,
    @SerializedName("adress2") val addressDetails: String?,
    @SerializedName("description")val description: String?,
    @SerializedName("comment") val comment: String?,
    @SerializedName("status") val status: RequestStatus?,
    @SerializedName("latlng") val latlng: String?,
    @SerializedName("client_info") val clientInfo: ClientResponseModel?
) {
    val date: Long get() = _date * 1000
}