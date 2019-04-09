package ru.climatlab.service.ui.requestDetailsInfo

import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BaseMvpView

interface RequestDetailsView: BaseMvpView {
    fun showRequestNotFoundError()
    fun showRequestDetailsInfo(request: RequestModel)
}
