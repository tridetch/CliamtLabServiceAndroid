package ru.climatlab.service.ui.map

import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BaseMvpView

interface MapView: BaseMvpView {
    fun showRequests(requests: List<RequestModel>)
    fun showRequestBottomCard(selectedRequest: RequestModel)
    fun hideRequestBottomCard()
    fun showLoginScreen()
}
