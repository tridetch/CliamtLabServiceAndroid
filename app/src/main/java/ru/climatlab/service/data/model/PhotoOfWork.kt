package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class PhotoOfWork(
    @SerializedName("file_name")val file_name: String,
    @SerializedName("file_string")val content: String
)
