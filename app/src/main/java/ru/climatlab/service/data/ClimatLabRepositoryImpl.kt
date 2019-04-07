package ru.climatlab.service.data

import io.reactivex.Completable

class ClimatLabRepositoryImpl : ClimatLabRepository {
    override fun login(login: String, password: String): Completable {
        return ClimatLabApiClient.climatLabService.login(login, password)
    }
}
