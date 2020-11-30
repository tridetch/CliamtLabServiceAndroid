package ru.climatlab.service.data.backend

import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.model.*

class ClimatLabRepositoryImpl : ClimatLabRepository {
    private var cachedRequests = listOf<Request>()
    private var cachedClients = listOf<ClientResponseModel>()

    override fun login(login: String, password: String): Completable {
        return ClimatLabApiClient.climatLabService.login(login, password).map {
            PreferencesRepository.putCurrentUserInfo(it)
        }.ignoreElement()
    }

    override fun logOut(): Completable {
        return Completable.create {
            PreferencesRepository.putCurrentUserInfo(null)
            it.onComplete()
        }
    }

    override fun updateData(): Completable {
        return getClients().ignoreElement()
    }

    override fun getRequests(vararg requestFilter: RequestStatus?): Single<List<Request>> {
        return ClimatLabApiClient.climatLabService.getRequests()
            .map { requests ->
                cachedRequests =
                    requests.filter { if (requestFilter.filterNotNull().isNotEmpty()) it.status in requestFilter else true }
                        .map { it.mapToRequest() }
                cachedRequests
            }
    }

    override fun sendRequestReport(
        requestReport: RequestReport,
        resultPhotos: List<SelectedFile>
    ): Completable {
        return ClimatLabApiClient.climatLabService.sendRequestReport(requestReport = requestReport)
            .andThen(sendPhotos(resultPhotos))
    }

    override fun sendPaymentInfo(paymentInfo: PaymentInfo): Completable {
        return ClimatLabApiClient.climatLabService.sendPaymentInfo(paymentInfo = paymentInfo)
    }

    private fun sendPhotos(resultPhotos: List<SelectedFile>): Completable {
        return Observable.fromIterable(resultPhotos).flatMapCompletable {
            val requestFile = RequestBody.create(MultipartBody.FORM, it.file)
            ClimatLabApiClient.climatLabService.sendPhoto(
                RequestBody.create(MultipartBody.FORM, it.request_id),
                RequestBody.create(MultipartBody.FORM, it.location),
                MultipartBody.Part.createFormData("userfile", it.file_name, requestFile)
            )
        }
    }

    override fun getRequest(requestId: String): Request? {
        return cachedRequests.firstOrNull { it.id == requestId }
    }

    override fun acceptRequest(request: Request): Completable {
        return ClimatLabApiClient.climatLabService.acceptRequest(acceptRequestBody = AcceptRequestBody(requestId = request.id))
    }

    override fun cancelRequest(request: Request, comment: String): Completable {
        return ClimatLabApiClient.climatLabService.cancelRequest(
            cancelRequestBody = CancelRequestBody(
                requestId = request.id,
                comment = comment
            )
        )
    }

    override fun getClients(): Single<List<ClientResponseModel>> {
        return ClimatLabApiClient.climatLabService.getClients().map {
            cachedClients = it
            it
        }
    }

    override fun updateNotificationTokenIfNeeded(): Completable {
        return Completable.fromCallable {
            if (PreferencesRepository.getIsNeedUpdateToken()) {
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let {
                            ClimatLabApiClient.climatLabService.postNotificationToken(TokenRequestBody(it.token))
                                .addSchedulers().subscribe({
                                    PreferencesRepository.putIsNeedUpdateToken(false)
                                }, {})
                        }
                    }
                }
            }
        }
    }

    override fun sendUserLocation(lat: Double, lng: Double): Completable {
        return ClimatLabApiClient.climatLabService.sendUserLocation(Coordinates(lat, lng))
    }
}

private fun RequestResponseModel.mapToRequest(): Request {
    return Request(
        id = id,
        comment = comment ?: "",
        date = date,
        equipment = equipment ?: "",
        boilerBrand = boilerBrand ?: "",
        boilerModel = boilerModel ?: "",
        status = status ?: RequestStatus.Cancelled,
        type = type ?: RequestType.WarrantyRepair,
        office = office ?: "",
        description = description ?: "",
        address = address ?: "",
        addressDetails = addressDetails ?: "",
        clientInfo = clientInfo,
        latlng = latlng
    )
}
