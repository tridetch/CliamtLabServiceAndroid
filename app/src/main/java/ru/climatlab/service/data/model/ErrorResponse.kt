package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(@SerializedName("error_code") val code: Int, @SerializedName("error_text") val message: String)