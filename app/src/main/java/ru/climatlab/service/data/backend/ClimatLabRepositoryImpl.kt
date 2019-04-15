package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.model.*
import kotlin.random.Random

class ClimatLabRepositoryImpl : ClimatLabRepository {
    private var cachedRequests = listOf<Request>()
    private var cachedClients = listOf<ClientResponseModel>()

    override fun login(login: String, password: String): Completable {
        return ClimatLabApiClient.climatLabService.login(login, password).map {
            PreferencesRepository.putToken(it.token)
        }.ignoreElement()
    }

    override fun logOut(): Completable {
        return Completable.create {
            PreferencesRepository.putToken("")
            it.onComplete()
        }
    }

    override fun updateData(): Completable {
        return getClients().ignoreElement()
    }

    override fun getRequests(requestFilter: RequestStatus?): Single<List<Request>> {
        val random = Random
        return ClimatLabApiClient.climatLabService.getRequests()
            .map { requests ->
                cachedRequests = requests.filter { if (requestFilter != null) it.status == requestFilter else true }
                    .map {
                        it.copy(
                            coordinates = MapCoordinates(
                                44.05 + random.nextDouble() / 100,
                                43.05 + random.nextDouble() / 100
                            )
                        )
                    }
                    .mapNotNull { request ->
                        val client = cachedClients.firstOrNull { client -> client.id == request.clientId }
                        if (client == null) null else Request(request, client)
                    }
                cachedRequests
            }
    }

    override fun sendRequestReport(requestReport: RequestReport): Completable {
        return ClimatLabApiClient.climatLabService.sendRequestReport(requestReport = requestReport)
    }

    override fun getRequest(requestId: String): Request? {
        return cachedRequests.firstOrNull { it.requestInfo.id == requestId }
    }

    override fun acceptRequest(request: RequestResponseModel): Completable {
        return ClimatLabApiClient.climatLabService.acceptRequest(acceptRequestBody = AcceptRequestBody(requestId = request.id))
    }

    override fun cancelRequest(request: RequestResponseModel, comment: String): Completable {
        return ClimatLabApiClient.climatLabService.cancelRequest(cancelRequestBody = CancelRequestBody(requestId = request.id, comment = comment))
    }

    override fun getClients(): Single<List<ClientResponseModel>> {
        return ClimatLabApiClient.climatLabService.getClients().map {
            cachedClients = it
            it
        }
    }
}
