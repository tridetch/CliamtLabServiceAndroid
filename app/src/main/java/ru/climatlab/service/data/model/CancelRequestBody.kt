package ru.climatlab.service.data.model

import java.util.*

data class CancelRequestBody(val requestId: String, val dateTime: Date = Date(), val comment: String? = null)
