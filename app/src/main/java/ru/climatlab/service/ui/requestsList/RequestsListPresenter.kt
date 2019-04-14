package ru.climatlab.service.ui.requestsList

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class RequestsListPresenter : BasePresenter<RequestsListView>() {

    fun onAttach(requestFilter: RequestStatus?) {
        ClimatLabRepositoryProvider.instance
            .getRequests(requestFilter)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({ viewState.updateData(it) }, this::handleError)
    }

    fun onRequestClick(request: Request) {
        when (request.requestInfo.status) {
            RequestStatus.InWork -> viewState.showRequestReportScreen(request)
            else -> viewState.showRequestDetailsScreen(request)
        }
    }
}
