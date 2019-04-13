package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.model.MapCoordinates
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport
import ru.climatlab.service.data.model.RequestStatus
import kotlin.random.Random

class ClimatLabRepositoryImpl : ClimatLabRepository {
    private var cachedRequests = listOf<RequestModel>()

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

    override fun getRequests(requestFilter: RequestStatus?): Single<List<RequestModel>> {
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
                cachedRequests
            }
    }

    override fun sendRequestReport(requestReport: RequestReport): Completable {
        return ClimatLabApiClient.climatLabService.sendRequestReport(requestReport = requestReport)
    }

    override fun getRequest(requestId: String): RequestModel? {
        return cachedRequests.firstOrNull { it.id == requestId }
    }

    override fun acceptRequest(request: RequestModel): Completable {
        return ClimatLabApiClient.climatLabService.acceptRequest(requestId = request.id)
    }

    override fun cancelRequest(request: RequestModel, comment: String): Completable {
        return ClimatLabApiClient.climatLabService.cancelRequest(requestId = request.id, comment = comment)
    }
}
