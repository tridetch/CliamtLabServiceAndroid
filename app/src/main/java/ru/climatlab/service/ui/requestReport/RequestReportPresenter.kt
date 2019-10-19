package ru.climatlab.service.ui.requestReport

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestReport
import ru.climatlab.service.data.model.RequestType
import ru.climatlab.service.ui.BasePresenter
import java.util.*

@InjectViewState
class RequestReportPresenter : BasePresenter<RequestReportView>() {

    private lateinit var request: Request
    private var requestReport = RequestReport()
    private var photoUriList: MutableList<Uri> = mutableListOf()

    fun onAttach(requestId: String) {
        val cachedRequest = ClimatLabRepositoryProvider.instance.getRequest(requestId)
        if (cachedRequest == null) {
            viewState.showRequestNotFoundError()
        } else {
            request = cachedRequest
            requestReport = requestReport.copy(requestId = cachedRequest.id)
        }
        viewState.setupPhoto(photoUriList)
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
        performedWork: String = requestReport.performedWork,
        amountToPay: String = requestReport.amountToPay,
        amountForTheRoad: String = requestReport.amountForTheRoad,
        amountOfPart: String = requestReport.amountOfPart,
        resultPhoto: MutableList<String> = requestReport.resultPhotos,
        requestType: RequestType = requestReport.requestType
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
            performedWork = performedWork,
            amountToPay = amountToPay,
            amountForTheRoad = amountForTheRoad,
            amountOfPart = amountOfPart,
            resultPhotos = resultPhoto,
            requestType = requestType
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

    fun onTakePhoto(photoUri: Uri, base64Image: String) {
        photoUriList.add(photoUri)
        requestReport.resultPhotos.add(base64Image)
        viewState.onPhotoAdded()
    }

    fun onPhotoRemoved(position: Int) {
        photoUriList.removeAt(position)
        requestReport.resultPhotos.removeAt(position)
        viewState.onPhotoRemoved(position)
    }

}
