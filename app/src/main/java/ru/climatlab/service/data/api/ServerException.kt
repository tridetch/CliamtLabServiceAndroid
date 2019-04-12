package ru.climatlab.service.data.api

/**
 * Created by tridetch on 12.04.2019. CliamtLabService
 */
open class ServerException(code: Int, message: String) : RuntimeException(message)