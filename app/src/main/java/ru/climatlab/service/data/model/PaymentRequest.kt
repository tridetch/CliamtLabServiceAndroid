package ru.climatlab.service.data.model

data class PaymentRequest(
    val amount: Double,
    val login: String,
    val password: String,
    val description: String = "",
    val receiptEmail: String = "",
    val receiptPhone: String = ""
)