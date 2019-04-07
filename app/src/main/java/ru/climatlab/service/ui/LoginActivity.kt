package ru.climatlab.service.ui

import android.os.Bundle
import ru.climatlab.service.R
import ru.climatlab.service.androidXMoxy.MvpAppCompatActivity

class LoginActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
