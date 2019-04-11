package ru.climatlab.service.ui.requestReport

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_request_report.*
import ru.climatlab.service.R
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity

class RequestReportActivity : BaseActivity(), RequestReportView {
    companion object {
        const val EXTRA_KEY_REQUEST_ID = "extra_key_request_id"
    }

    @InjectPresenter
    lateinit var presenter: RequestReportPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_report)

        presenter.onAttach(intent.getStringExtra(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID))

        confirmButton.setOnClickListener { presenter.onReportConfirm() }
    }

    override fun showRequestNotFoundError() {
        AlertDialog.Builder(this)
            .setMessage(R.string.request_not_found_error_message)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> this@RequestReportActivity.finish() }
            .show()
    }

}
