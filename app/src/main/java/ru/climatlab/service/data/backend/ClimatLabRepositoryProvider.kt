package ru.climatlab.service.data.backend

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
object ClimatLabRepositoryProvider {
val instance: ClimatLabRepository by lazy { ClimatLabRepositoryImpl() }
}