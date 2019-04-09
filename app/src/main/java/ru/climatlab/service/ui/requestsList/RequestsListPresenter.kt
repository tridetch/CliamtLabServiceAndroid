package ru.climatlab.service.ui.requestsList

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class RequestsListPresenter : BasePresenter<RequestsListView>() {
    override fun onFirstViewAttach() {
        ClimatLabRepositoryProvider.instance
            .getRequests()
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({ viewState.updateData(it) }, this::handleError)
    }

    fun onRequestClick(request: RequestModel) {
        when (request.status) {
            RequestStatus.InWork -> viewState.showRequestDetailsScreen(request)
            else -> viewState.showRequestReportScreen(request)
        }
    }
}
