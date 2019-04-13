package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport

interface ClimatLabRepository {
    fun login(login: String, password: String): Completable
    fun getRequests(): Single<List<RequestModel>>
    fun getRequest(requestId: String): RequestModel?
    fun sendRequestReport(requestReport: RequestReport): Completable
    fun logOut():Completable
    fun acceptRequest(request: RequestModel): Completable
    fun cancelRequest(request: RequestModel, comment: String): Completable
}
