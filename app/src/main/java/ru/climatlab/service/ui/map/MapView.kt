package ru.climatlab.service.ui.map

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BaseMvpView

interface MapView: BaseMvpView {
    enum class Message {
        RequestAccepted, RequestCanceled
    }

    fun showRequests(requests: List<RequestModel>)
    fun showRequestBottomCard(selectedRequest: RequestModel)
    fun hideRequestBottomCard()
    fun showLoginScreen()
    fun showRequestDetails(selectedRequest: RequestModel)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: MapView.Message)
}
