package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class CurrentUserResponse(
    @SerializedName("token") val token: String,
    @SerializedName("login") val login: String,
    @SerializedName("name") val name: String,
    @SerializedName("name1") val surname: String,
    @SerializedName("name2") val middleName: String,
    @SerializedName("city") val city: String,
    @SerializedName("phone") val phone: String
) {
    fun getFullName(): String {
        return "$surname $name $middleName"
    }
}
