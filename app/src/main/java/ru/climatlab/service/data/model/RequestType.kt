package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
enum class RequestType {
    @SerializedName("0")
    Mounting,
    @SerializedName("1")
    Service,
    @SerializedName("2")
    OrderEquipment
}