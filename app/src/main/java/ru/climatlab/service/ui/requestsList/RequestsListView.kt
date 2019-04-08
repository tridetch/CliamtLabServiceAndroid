package ru.climatlab.service.ui.requestsList

import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BaseMvpView

interface RequestsListView: BaseMvpView {

    fun updateData(requests: List<RequestModel>)
}
