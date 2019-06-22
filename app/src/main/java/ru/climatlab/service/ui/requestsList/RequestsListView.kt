package ru.climatlab.service.ui.requestsList

import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestResponseModel
import ru.climatlab.service.ui.BaseMvpView

interface RequestsListView: BaseMvpView {

    fun updateData(requests: List<Request>)
    fun showRequestDetailsScreen(request: Request)
    fun showRequestReportScreen(request: Request)
    fun showLoginScreen()
}
