package ru.climatlab.service.ui.requestReport

import android.net.Uri
import ru.climatlab.service.ui.BaseMvpView

interface RequestReportView : BaseMvpView {
    enum class Message {
        ReportSent
    }
    fun showRequestNotFoundError()
    fun showMessage(reportSent: Message)
    fun setupPhoto(photos: MutableList<Uri>)
    fun onPhotoAdded()
    fun onPhotoRemoved(position: Int)
}
