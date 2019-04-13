package ru.climatlab.service.data.backend

import io.reactivex.Completable
import io.reactivex.Single
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.model.MapCoordinates
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport
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

    override fun getRequests(): Single<List<RequestModel>> {
        val random = Random
/*
        cachedRequests = List(20) {
            RequestModel(
                id = UUID.randomUUID().toString(),
                clientId = "ClientId $it",
                address = "Address $it",
                description = "Description $it",
                equipmentId = "EquipmentId $it",
                office = "Office $it",
                type = RequestType.values()[random.nextInt(0..2)],
                status = RequestStatus.values()[random.nextInt(0..3)],
                coordinates = MapCoordinates(
                    44.05 + random.nextDouble() / 100,
                    43.05 + random.nextDouble() / 100
                )
            )
        }
        return Single.just(cachedRequests)
*/
        return ClimatLabApiClient.climatLabService.getRequests()
            .map { requests ->
                requests.map {
                    it.copy(
                        coordinates = MapCoordinates(
                            44.05 + random.nextDouble() / 100,
                            43.05 + random.nextDouble() / 100
                        )
                    )
                }
            }
    }

    override fun sendRequestReport(requestReport: RequestReport) {
        return ClimatLabApiClient.climatLabService.sendRequestReport(requestReport = requestReport)
    }

    override fun getRequest(requestId: String): RequestModel? {
        return cachedRequests.firstOrNull { it.id == requestId }
    }

}
