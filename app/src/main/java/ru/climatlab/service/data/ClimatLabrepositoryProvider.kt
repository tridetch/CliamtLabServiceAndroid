package ru.climatlab.service.data

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
object ClimatLabrepositoryProvider {
val instance: ClimatLabRepository by lazy { ClimatLabRepositoryImpl() }
}