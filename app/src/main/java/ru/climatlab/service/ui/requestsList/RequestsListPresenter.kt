package ru.climatlab.service.ui.requestsList

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class RequestsListPresenter : BasePresenter<RequestsListView>() {
    fun onRequestClick(request: RequestModel) {

    }
}
