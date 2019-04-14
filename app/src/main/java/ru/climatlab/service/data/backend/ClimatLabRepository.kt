package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.model.*

interface ClimatLabRepository {
    fun login(login: String, password: String): Completable
    fun getRequests(requestFilter: RequestStatus? = null): Single<List<Request>>
    fun getRequest(requestId: String): Request?
    fun sendRequestReport(requestReport: RequestReport): Completable
    fun logOut():Completable
    fun acceptRequest(request: RequestResponseModel): Completable
    fun cancelRequest(request: RequestResponseModel, comment: String): Completable
    fun getClients(): Single<List<ClientResponseModel>>
    fun updateData(): Completable
}
