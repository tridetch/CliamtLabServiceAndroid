package ru.climatlab.service.ui.map

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class MapPresenter : BasePresenter<MapView>() {
    private var selectedRequest: RequestModel? = null

    override fun onFirstViewAttach() {
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
}
