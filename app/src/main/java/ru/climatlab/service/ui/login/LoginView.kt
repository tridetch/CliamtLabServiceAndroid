package ru.climatlab.service.ui.login

import com.arellomobile.mvp.MvpView
import ru.climatlab.service.ui.BaseMvpView

interface LoginView: BaseMvpView {
    fun showNextScreen()
}
