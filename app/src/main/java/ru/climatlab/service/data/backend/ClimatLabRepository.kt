package ru.climatlab.service.data.backend

import io.reactivex.Completable

interface ClimatLabRepository {
    fun login(login: String, password: String): Completable
}
