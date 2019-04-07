package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.model.RequestModel

interface ClimatLabRepository {
    fun login(login: String, password: String): Completable
    fun getRequests(): Single<List<RequestModel>>
}
