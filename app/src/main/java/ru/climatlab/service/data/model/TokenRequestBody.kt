package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class TokenRequestBody(@SerializedName("device_token")val deviceToken: String)
