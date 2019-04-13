package ru.climatlab.service.ui

import com.arellomobile.mvp.MvpPresenter

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
open class BasePresenter<View : BaseMvpView> : MvpPresenter<View>() {
    fun handleError(e: Throwable) {
        e.printStackTrace()
        viewState.showError(e.message ?: "Undefined error")
    }
}