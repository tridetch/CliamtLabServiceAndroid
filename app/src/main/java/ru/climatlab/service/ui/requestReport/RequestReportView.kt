package ru.climatlab.service.ui.requestReport

import android.net.Uri
import ru.climatlab.service.data.model.PaymentRequest
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.SelectedFile
import ru.climatlab.service.ui.BaseMvpView

interface RequestReportView : BaseMvpView {
    enum class Message {
        ReportSent,
        PaymentInfoSent
    }
    fun showRequestNotFoundError()
    fun showMessage(reportSent: Message)
    fun setupPhoto(photos: MutableList<Uri>)
    fun onPhotoAdded()
    fun onPhotoRemoved(position: Int)
    fun setupFiles(files: MutableList<SelectedFile>)
    fun onFileAdded()
    fun onFileRemoved(position: Int)
    fun acceptPayment(paymentRequest: PaymentRequest)
    fun setupRequestInfo(request: Request)
    fun disablePaymentButton()
    fun disableReportButton()
}
