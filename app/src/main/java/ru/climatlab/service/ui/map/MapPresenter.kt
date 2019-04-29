package ru.climatlab.service.ui.map

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class MapPresenter : BasePresenter<MapView>() {
    private var selectedRequest: Request? = null
    private var needFocusAllRequests: Boolean = true

    fun onResume() {
        updateRequests()
        ClimatLabRepositoryProvider.instance
            .updateNotificationTokenIfNeeded()
            .addSchedulers()
            .subscribe({}, this::handleError)
    }

    private fun updateRequests() {
        ClimatLabRepositoryProvider.instance.getRequests()
            .addSchedulers().subscribe({
                viewState.showRequests(it)
                if (needFocusAllRequests) {
                    needFocusAllRequests = false
                    viewState.focusRequests(it)
                }
            }, this::handleError)
    }

    fun onRequestSelected(request: Request?) {
        selectedRequest = request
        if (selectedRequest != null) {
            viewState.showRequestBottomCard(request!!)
        } else {
            viewState.hideRequestBottomCard()
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

    fun onAcceptRequest(request: Request) {
        ClimatLabRepositoryProvider.instance.acceptRequest(request.requestInfo)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showMessage(MapView.Message.RequestAccepted)
                viewState.hideRequestBottomCard()
                viewState.showRequestReportScreen(request)
            }, this::handleError)
    }

    fun onCancelRequest(request: Request, comment: String) {
        ClimatLabRepositoryProvider.instance.cancelRequest(request.requestInfo, comment)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.hideRequestBottomCard()
                viewState.showMessage(MapView.Message.RequestCanceled)
                updateRequests()
            }, this::handleError)
    }
}
