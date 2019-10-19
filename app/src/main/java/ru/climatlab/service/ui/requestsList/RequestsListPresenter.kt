package ru.climatlab.service.ui.requestsList

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class RequestsListPresenter : BasePresenter<RequestsListView>() {

    private var requestFilter: RequestStatus? = null

    fun onAttach(requestFilter: RequestStatus?) {
        this.requestFilter = requestFilter
    }

    private fun updateRequests(requestFilter: RequestStatus?) {
        ClimatLabRepositoryProvider.instance
            .getRequests(requestFilter)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({ viewState.updateData(it) }, this::handleError)
    }

    fun onRequestClick(request: Request) {
        when (request.status) {
            RequestStatus.InWork -> viewState.showRequestReportScreen(request)
            else -> viewState.showRequestDetailsScreen(request)
        }
    }

    fun onLogoutClick() {
        ClimatLabRepositoryProvider.instance
            .logOut()
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showLoginScreen()
            }, this::handleError)
    }

    fun onResume() {
        updateRequests(requestFilter)
            ClimatLabRepositoryProvider.instance
                .updateNotificationTokenIfNeeded()
                .addSchedulers()
                .subscribe({}, this::handleError)
    }
}
