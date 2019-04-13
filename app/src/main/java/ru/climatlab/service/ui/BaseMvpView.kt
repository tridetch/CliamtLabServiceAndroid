package ru.climatlab.service.ui

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.climatlab.service.ui.map.MapView

@StateStrategyType(AddToEndSingleStrategy::class)
interface BaseMvpView: MvpView {
    fun showLoading(needShow: Boolean)
    fun showError(message: String?)
    fun closeScreen()
}
