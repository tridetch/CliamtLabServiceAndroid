package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class ClientResponseModel(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("name1") val surname: String?,
    @SerializedName("name2") val middleName: String?,
    @SerializedName("sex") val gender: Gender?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("contact") val reserveContact: String?,
    @SerializedName("passport") val passport: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("comment") val comment: String?,
    @SerializedName("where") val where: String?,
    @SerializedName("dogovor_number") val contractNumber: String?,
    @SerializedName("dogovor_date") val contractDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("date_registr") val registrationDate: String?,
    @SerializedName("date_update") val lastUpdateDate: String?,
    @SerializedName("author") val author: String?
){
    fun fullName() = "$surname $name $middleName"
}

enum class Gender {
@SerializedName("1")Male, @SerializedName("2")Female
}
