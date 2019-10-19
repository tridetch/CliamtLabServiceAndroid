package ru.climatlab.service.ui.requestDetailsInfo

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_request_details.*
import kotlinx.android.synthetic.main.cancel_request_confirmation_dialog.view.*
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
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> this@RequestDetailsActivity.finish() }
            .show()
    }

    override fun showRequestDetailsInfo(request: Request) {
        confirmButton.setOnClickListener { presenter.onAcceptRequest(request) }
        rejectButton.setOnClickListener {
            val cancelRequestConfirmationDialog =
                layoutInflater.inflate(R.layout.cancel_request_confirmation_dialog, null)
            AlertDialog.Builder(this)
                .setTitle(R.string.cancel_request_confirmation_dialog_title)
                .setView(cancelRequestConfirmationDialog)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    presenter.onCancelRequest(
                        request,
                        cancelRequestConfirmationDialog.reasonInputLayout.editText!!.text.toString()
                    )
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
        clientFullNameTextView.text = request.clientInfo?.fullName()
        descriptionTextView.text = request.description
        equipmentTextView.text = request.equipmentId
        addressTextView.text = request.address
        phoneNumber.text = request.clientInfo?.phone
        dateTextView.text = SimpleDateFormat("dd.MM hh:mm", Locale.getDefault()).format(Date(request.date))
        if (request.addressDetails.isBlank()) {
            addressDetailsTextView.visibility = View.GONE
            addressDetailsLabelTextView.visibility = View.GONE
        } else {
            addressDetailsTextView.visibility = View.VISIBLE
            addressDetailsLabelTextView.visibility = View.VISIBLE
            addressDetailsTextView.text = request.addressDetails
        }

        callButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:8${request.clientInfo?.phone}")))
        }
        buildRouteButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=${request.getCoordinates().latitude},${request.getCoordinates().longitude}(${request.address})")
                    ), getString(R.string.map_chooser_title)
                )
            )
        }
    }
}
