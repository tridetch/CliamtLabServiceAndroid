package ru.climatlab.service.ui

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import ru.climatlab.service.R
import ru.climatlab.service.androidXMoxy.MvpAppCompatActivity

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
open class BaseActivity : MvpAppCompatActivity(), BaseMvpView {

    lateinit var loadingDialog: ProgressDialog
    private var errorAlert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = ProgressDialog(this).apply {
            isIndeterminate = true
            setCancelable(false)
            setMessage(getString(R.string.loading_message))
        }
    }

    override fun showLoading(needShow: Boolean) {
        if (needShow) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    override fun showError(message: String?) {
        errorAlert?.dismiss()
        AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message).show()
    }
}