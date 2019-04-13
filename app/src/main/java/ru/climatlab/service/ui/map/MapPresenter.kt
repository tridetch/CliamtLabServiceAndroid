package ru.climatlab.service.ui.map

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class MapPresenter : BasePresenter<MapView>() {
    private var selectedRequest: RequestModel? = null

    fun onResume() {
        ClimatLabRepositoryProvider.instance.getRequests()
            .addSchedulers().subscribe({
                viewState.showRequests(it)
            }, this::handleError)
    }

    fun onRequestSelected(request: RequestModel?) {
        selectedRequest = request
        if (selectedRequest != null) {
            viewState.showRequestBottomCard(selectedRequest!!)
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

    fun onAcceptRequest(selectedRequest: RequestModel) {
        ClimatLabRepositoryProvider.instance.acceptRequest(selectedRequest)
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showMessage(MapView.Message.RequestAccepted)
                viewState.showRequestReportScreen(selectedRequest)
            }, this::handleError)
    }

    fun onCancelRequest(selectedRequest: RequestModel, comment: String) {
     ClimatLabRepositoryProvider.instance.cancelRequest(selectedRequest, comment)
         .addSchedulers()
         .doOnSubscribe { viewState.showLoading(true) }
         .doFinally { viewState.showLoading(false) }
         .subscribe({
             viewState.hideRequestBottomCard()
             viewState.showMessage(MapView.Message.RequestCanceled)
         }, this::handleError)
    }
}
