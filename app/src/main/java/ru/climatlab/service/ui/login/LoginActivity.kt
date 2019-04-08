package ru.climatlab.service.ui.login

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import ru.climatlab.service.R
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.map.MapActivity

class LoginActivity : BaseActivity(), LoginView {

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton.setOnClickListener { presenter.login(phoneInputLayout.editText!!.text.toString(), passwordInputLayout.editText!!.text.toString()) }
    }

    override fun showNextScreen() {
        finish()
        startActivity<MapActivity>()
    }
}
