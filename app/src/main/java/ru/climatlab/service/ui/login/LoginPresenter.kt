package ru.climatlab.service.ui.login

import com.arellomobile.mvp.InjectViewState
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.ui.BasePresenter

@InjectViewState
class LoginPresenter : BasePresenter<LoginView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (PreferencesRepository.getToken().isNotBlank()) {
            viewState.showNextScreen()
        }
    }

    fun login(login: String, password: String) {
        ClimatLabRepositoryProvider.instance.login(login, password)
                .addSchedulers()
                .doOnSubscribe { viewState.showLoading(true) }
                .doFinally { viewState.showLoading(false) }
                .subscribe({ viewState.showNextScreen() }, this::handleError)
    }
}
