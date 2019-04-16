package ru.climatlab.service.ui.login

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class LoginPresenter : BasePresenter<LoginView>() {

    private var login = ""
    private var password = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (PreferencesRepository.getToken().isNotBlank()) {
            ClimatLabRepositoryProvider.instance.updateData().addSchedulers()
                .doOnSubscribe { viewState.showLoading(true) }
                .doFinally { viewState.showLoading(false) }
                .subscribe({
                    viewState.showNextScreen()
                }, this::handleError)
        }
    }

    fun login() {
        ClimatLabRepositoryProvider.instance.login(login, password)
            .andThen(ClimatLabRepositoryProvider.instance.updateData())
            .addSchedulers()
            .doOnSubscribe { viewState.showLoading(true) }
            .doFinally { viewState.showLoading(false) }
            .subscribe({
                viewState.showNextScreen()
            }, this::handleError)
    }

    fun onLoginChanged(login: String) {
        this.login = login
    }

    fun onPasswordChanged(password: String) {
        this.password = password
    }
}
