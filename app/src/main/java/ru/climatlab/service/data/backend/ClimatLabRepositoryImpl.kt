package ru.climatlab.service.data.backend

import io.reactivex.Completable
import ru.climatlab.service.data.PreferencesRepository

class ClimatLabRepositoryImpl : ClimatLabRepository {
    override fun login(login: String, password: String): Completable {
        return ClimatLabApiClient.climatLabService.login(login, password).map {
            PreferencesRepository.putToken(it.token)
        }.toCompletable()
    }
}
