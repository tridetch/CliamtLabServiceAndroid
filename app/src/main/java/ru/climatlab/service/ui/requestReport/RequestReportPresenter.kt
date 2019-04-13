package ru.climatlab.service.ui.requestReport

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport
import ru.climatlab.service.ui.BasePresenter
import java.util.*

@InjectViewState
class RequestReportPresenter : BasePresenter<RequestReportView>() {

    private lateinit var request: RequestModel
    private var requestReport = RequestReport()

    fun onAttach(requestId: String) {
        val cachedRequest = ClimatLabRepositoryProvider.instance.getRequest(requestId)
        if (cachedRequest == null) {
            viewState.showRequestNotFoundError()
        } else {
            request = cachedRequest
            requestReport = requestReport.copy(requestId = cachedRequest.id)
        }

    }

    fun onReportConfirm(
        model: String = requestReport.model,
        brand: String = requestReport.brand,
        serialNumber: String = requestReport.serialNumber,
        presenceOfPickup: String = requestReport.presenceOfPickup,
        voltage: String = requestReport.voltage,
        grounding: Boolean = requestReport.grounding,
        stabilizer: Boolean = requestReport.stabilizer,
        dielectricCoupling: Boolean = requestReport.dielectricCoupling,
        inletGasPressure: String = requestReport.inletGasPressure,
        minimumGasOnTheBoiler: String = requestReport.minimumGasOnTheBoiler,
        maximumGasOnTheBoiler: String = requestReport.maximumGasOnTheBoiler,
        co: String = requestReport.co,
        co2: String = requestReport.co2,
        recommendations: String = requestReport.recommendations,
        amountToPay: String = requestReport.amountToPay,
        amountForTheRoad: String = requestReport.amountForTheRoad,
        amountOfPart: String = requestReport.amountOfPart,
        boilerPhoto: String = requestReport.boilerPhoto,
        resultPhoto: String = requestReport.resultPhoto
    ) {
        requestReport = requestReport.copy(
            date = Date(),
            model = model,
            brand = brand,
            serialNumber = serialNumber,
            presenceOfPickup = presenceOfPickup,
            voltage = voltage,
            grounding = grounding,
            stabilizer = stabilizer,
            dielectricCoupling = dielectricCoupling,
            inletGasPressure = inletGasPressure,
            minimumGasOnTheBoiler = minimumGasOnTheBoiler,
            maximumGasOnTheBoiler = maximumGasOnTheBoiler,
            co = co,
            co2 = co2,
            recommendations = recommendations,
            amountToPay = amountToPay,
            amountForTheRoad = amountForTheRoad,
            amountOfPart = amountOfPart,
            boilerPhoto = boilerPhoto,
            resultPhoto = resultPhoto
        )
        ClimatLabRepositoryProvider.instance
            .sendRequestReport(requestReport)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showMessage(RequestReportView.Message.ReportSent)
                viewState.closeScreen()
            }, this::handleError)
    }

    fun onBoilerPhotoTaken(boilerPhotoBase64: String) {
        requestReport = requestReport.copy(boilerPhoto = boilerPhotoBase64)
    }

    fun onResultPhotoTaken(resultPhotoBase64: String) {
        requestReport = requestReport.copy(resultPhoto = resultPhotoBase64)
    }
}
