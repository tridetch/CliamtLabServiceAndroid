package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class RequestReport(
    @SerializedName("model") val model: String = "",
    @SerializedName("brand") val brand: String = "",
    @SerializedName("serialNumber") val serialNumber: String = "",
    @SerializedName("presenceOfPickup") val presenceOfPickup: String = "",
    @SerializedName("voltage") val voltage: String = "",
    @SerializedName("grounding") val grounding: String = "",
    @SerializedName("stabilizer") val stabilizer: String = "",
    @SerializedName("dielectricCoupling") val dielectricCoupling: String = "",
    @SerializedName("inletGasPressure") val inletGasPressure: String = "",
    @SerializedName("minimumGasOnTheBoiler") val minimumGasOnTheBoiler: String = "",
    @SerializedName("maximumGasOnTheBoiler") val maximumGasOnTheBoiler: String = "",
    @SerializedName("co") val co: String = "",
    @SerializedName("co2") val co2: String = "",
    @SerializedName("recommendations") val recommendations: String = "",
    @SerializedName("amountToPay") val amountToPay: String = "",
    @SerializedName("amountForTheRoad") val amountForTheRoad: String = "",
    @SerializedName("amountOfPart") val amountOfPart: String = "",
    @SerializedName("boilerPhoto") val boilerPhoto: String = "",
    @SerializedName("resultPhoto") val resultPhoto: String = ""
)
