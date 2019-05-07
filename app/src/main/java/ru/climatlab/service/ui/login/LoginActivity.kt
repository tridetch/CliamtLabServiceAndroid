package ru.climatlab.service.ui.login

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.arellomobile.mvp.presenter.InjectPresenter
import com.redmadrobot.inputmask.MaskedTextChangedListener
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
        val phoneEditText = phoneInputLayout.editText!!
        val listener = MaskedTextChangedListener.installOn(
            phoneEditText,
            "+7 ([000]) [000]-[00]-[00]",
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                    presenter.onLoginChanged(extractedValue)
                }
            }
        )
        phoneEditText.addTextChangedListener(listener)
        phoneEditText.onFocusChangeListener = listener

        passwordInputLayout.editText!!.addTextChangedListener(afterTextChanged = { presenter.onPasswordChanged(it.toString()) })

        loginButton.setOnClickListener { presenter.login() }
    }

    override fun showNextScreen() {
        val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
        closeScreen()
        startActivity<MapActivity>()
    }
}
