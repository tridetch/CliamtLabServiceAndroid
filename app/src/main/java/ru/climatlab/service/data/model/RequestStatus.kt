package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
enum class RequestStatus {
    @SerializedName("0")
    NewRequest,
    @SerializedName("1")
    InWork,
    @SerializedName("3")
    Cancelled
}