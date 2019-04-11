package ru.climatlab.service.ui.requestReport

import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestReport
import ru.climatlab.service.ui.BasePresenter

class RequestReportPresenter: BasePresenter<RequestReportView>() {

    private lateinit var request: RequestModel

    private var requesrReport = RequestReport()
    fun onAttach(requestId: String?) {
        val cachedRequest = ClimatLabRepositoryProvider.instance.getRequest(requestId)
        if (cachedRequest == null) {
            viewState.showRequestNotFoundError()
        } else {
            request = cachedRequest
        }

    }


}
