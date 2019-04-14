package ru.climatlab.service.ui.requestDetailsInfo

import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestResponseModel
import ru.climatlab.service.ui.BaseMvpView

interface RequestDetailsView: BaseMvpView {
    fun showRequestNotFoundError()
    fun showRequestDetailsInfo(request: Request)
}
