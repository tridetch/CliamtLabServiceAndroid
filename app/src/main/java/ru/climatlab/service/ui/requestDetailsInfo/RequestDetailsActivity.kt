package ru.climatlab.service.ui.requestDetailsInfo

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_request_details.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.ui.BaseActivity

class RequestDetailsActivity : BaseActivity(), RequestDetailsView {

    companion object {
        const val EXTRA_KEY_REQUEST_ID = "extra_key_request_id"
    }

    @InjectPresenter
    lateinit var presenter: RequestDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)

        presenter.onAttach(intent.getStringExtra(EXTRA_KEY_REQUEST_ID))
    }

    override fun showRequestNotFoundError() {
        AlertDialog.Builder(this)
            .setMessage(R.string.request_not_found_error_message)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> this@RequestDetailsActivity.finish()}
            .show()
    }

    override fun showRequestDetailsInfo(request: RequestModel) {
        clientFullNameTextView.text = request.clientId
        descriptionTextView.text = request.description
        equipmentTextView.text = request.description
        addressTextView.text = request.address
    }
}
