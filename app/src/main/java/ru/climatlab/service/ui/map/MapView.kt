package ru.climatlab.service.ui.map

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestResponseModel
import ru.climatlab.service.ui.BaseMvpView

interface MapView: BaseMvpView {
    enum class Message {
        RequestAccepted, RequestCanceled
    }

    fun showRequests(requests: List<Request>)
    fun showRequestBottomCard(request: Request)
    fun hideRequestBottomCard()
    fun showRequestDetails(request: Request)
    fun showRequestReportScreen(request: Request)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: MapView.Message)
    fun focusRequests(requests: List<Request>)
    fun focusCurrentLocation()
}
