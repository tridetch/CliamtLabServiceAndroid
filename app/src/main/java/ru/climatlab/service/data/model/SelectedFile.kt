package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class SelectedFile(
    @SerializedName("id_request")val request_id: String,
    @SerializedName("file_name")val file_name: String,
    @SerializedName("file")val content: String
)
