package ru.climatlab.service.ui.requestDetailsInfo

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_request_details.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.ui.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

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

    override fun showRequestDetailsInfo(request: Request) {
        clientFullNameTextView.text = request.clientResponseModel.fullName()
        descriptionTextView.text = request.requestInfo.description
        equipmentTextView.text = request.requestInfo.equipmentId
        addressTextView.text = request.requestInfo.address
        phoneNumber.text = request.clientResponseModel.phone
        dateTextView.text = SimpleDateFormat("dd.MM hh:mm", Locale.getDefault()).format(Date(request.requestInfo.date))
        if (request.requestInfo.addressDetails.isNullOrBlank()) {
            addressDetailsTextView.visibility = View.GONE
        } else {
            addressDetailsTextView.visibility = View.VISIBLE
            addressDetailsTextView.text = request.requestInfo.addressDetails
        }

        callButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:8${request.clientResponseModel.phone}")))
        }
        buildRouteButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=${request.requestInfo.getCoordinates()?.latitude},${request.requestInfo.getCoordinates()?.longitude}(${request.requestInfo.address})")
                    ), getString(R.string.map_chooser_title)
                )
            )
        }
    }
}
