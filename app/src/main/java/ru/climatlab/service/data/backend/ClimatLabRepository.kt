package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.model.*

interface ClimatLabRepository {
    fun login(login: String, password: String): Completable
    fun getRequests(vararg requestFilter: RequestStatus?): Single<List<Request>>
    fun getRequest(requestId: String): Request?
    fun sendRequestReport(
        requestReport: RequestReport,
        resultPhotos: MutableList<SelectedFile>
    ): Completable
    fun logOut():Completable
    fun acceptRequest(request: Request): Completable
    fun cancelRequest(request: Request, comment: String): Completable
    fun getClients(): Single<List<ClientResponseModel>>
    fun updateData(): Completable
    fun updateNotificationTokenIfNeeded(): Completable
    fun sendUserLocation(lat: Double, lng: Double): Completable
}
