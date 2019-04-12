package ru.climatlab.service.ui.requestDetailsInfo

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class RequestDetailsPresenter : BasePresenter<RequestDetailsView>() {

    private lateinit var request: RequestModel

    fun onAttach(requestId: String) {
        val cachedRequest = ClimatLabRepositoryProvider.instance.getRequest(requestId)
        if (cachedRequest == null) {
            viewState.showRequestNotFoundError()
        } else {
            request = cachedRequest
            viewState.showRequestDetailsInfo(request)
        }
    }
}
