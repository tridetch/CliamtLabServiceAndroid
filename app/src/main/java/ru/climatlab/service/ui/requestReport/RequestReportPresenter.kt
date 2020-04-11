package ru.climatlab.service.ui.requestReport

import android.annotation.SuppressLint
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.*
import ru.climatlab.service.ui.BasePresenter
import java.io.File
import java.util.*

@InjectViewState
class RequestReportPresenter : BasePresenter<RequestReportView>() {

    private lateinit var request: Request
    private var requestReport = RequestReport()
    private var paymentRequest: PaymentRequest? = null
    private var paymentInfo: PaymentInfo? = null
    private val resultPhotos: MutableList<SelectedFile> = mutableListOf()
    private var photoUriList: MutableList<Uri> = mutableListOf()
    private val files: MutableList<SelectedFile> = mutableListOf()
    private val userInfo: CurrentUserResponse? = PreferencesRepository.getCurrentUserInfo()

    fun onAttach(requestId: String) {
        val cachedRequest = ClimatLabRepositoryProvider.instance.getRequest(requestId)
        if (cachedRequest == null) {
            viewState.showRequestNotFoundError()
        } else {
            request = cachedRequest
            requestReport = requestReport.copy(requestId = cachedRequest.id)
        }
        viewState.setupRequestInfo(request)
        viewState.setupPhoto(photoUriList)
        viewState.setupFiles(files)
    }

    @SuppressLint("CheckResult")
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
        amountToPay: String = requestReport.amountToPay,//change to double
        amountForTheRoad: String = requestReport.amountForTheRoad,
        amountOfPart: String = requestReport.amountOfPart,
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
            requestType = requestType
        )
        paymentRequest = PaymentRequest(
            amount = amountToPay.toDouble(),
            login = userInfo?.login2can ?: "",
            password = userInfo?.password2can ?: "",
            description = request.getPaymentDescription(),
            receiptEmail = request.clientInfo?.email ?: ""
        )
        sendRequest()
    }

    private fun sendRequest() {
        ClimatLabRepositoryProvider.instance
            .sendRequestReport(requestReport, resultPhotos.plus(files))
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showMessage(RequestReportView.Message.ReportSent)
                paymentRequest?.let {
                    viewState.acceptPayment(it)
                }
            }, this::handleError)
    }

    fun onTakePhotoFile(
        photoUri: Uri,
        file: File,
        coordinates: Coordinates
    ) {
        photoUriList.add(photoUri)
        resultPhotos.add(
            SelectedFile(
                request_id = request.id,
                file_name = photoUri.lastPathSegment ?: "",
                file = file,
                location = coordinates.toString()
            )
        )
        viewState.onPhotoAdded()
    }

    fun onPhotoRemoved(position: Int) {
        photoUriList.removeAt(position)
        resultPhotos.removeAt(position)
        viewState.onPhotoRemoved(position)
    }

    fun onFileSelected(file: File, filename: String) {
        files.add(
            SelectedFile(
                request_id = request.id,
                file_name = filename,
                file = file
            )
        )
        viewState.onFileAdded()
    }

    private fun Request.getPaymentDescription() =
        """Договор № ${request.clientInfo?.contractNumber}
            |Заявка № ${request.id}""".trimMargin()


    fun onFileRemoved(position: Int) {
        files.removeAt(position)
        viewState.onFileRemoved(position)
    }

    fun onPaymentInfo(info: PaymentInfo) {
        paymentInfo = info
        sendPaymentInfo(info)
    }

    private fun sendPaymentInfo(info: PaymentInfo) {
        ClimatLabRepositoryProvider.instance
            .sendPaymentInfo(info)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showMessage(RequestReportView.Message.PaymentInfoSent)
                viewState.closeScreen()
            }, this::handleError)
    }

    fun onRetrySendClick() {
        when {
            paymentRequest != null && paymentInfo == null -> sendRequest()
            paymentInfo != null -> sendPaymentInfo(paymentInfo!!)
        }
    }

}
