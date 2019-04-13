package ru.climatlab.service.ui.requestReport

import ru.climatlab.service.ui.BaseMvpView

interface RequestReportView : BaseMvpView {
    enum class Message {
        ReportSent
    }
    fun showRequestNotFoundError()
    fun showMessage(reportSent: Message)
}
