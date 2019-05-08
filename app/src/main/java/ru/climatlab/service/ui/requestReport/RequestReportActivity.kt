package ru.climatlab.service.ui.requestReport

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.ImageQuality
import com.fxn.utility.PermUtil
import kotlinx.android.synthetic.main.activity_request_report.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.RequestType
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import java.io.ByteArrayOutputStream


class RequestReportActivity : BaseActivity(), RequestReportView {
    companion object {
        const val EXTRA_KEY_REQUEST_ID = "extra_key_request_id"
    }

    private val REQUEST_CODE_RESULT_PHOTO = 1

    @InjectPresenter
    lateinit var presenter: RequestReportPresenter

    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_report)

        presenter.onAttach(intent.getStringExtra(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID))


        confirmButton.setOnClickListener {
            presenter.onReportConfirm(
                model = modelEditText.text.toString(),
                brand = brandEditText.text.toString(),
                serialNumber = serialNumberEditText.text.toString(),
                voltage = voltageEditText.text.toString(),
                grounding = groundingCheckBox.isChecked,
                stabilizer = stabilizerCheckBox.isChecked,
                dielectricCoupling = dielectricCouplingCheckBox.isChecked,
                inletGasPressure = inletGasPressureEditText.text.toString(),
                minimumGasOnTheBoiler = minGasEditText.text.toString(),
                maximumGasOnTheBoiler = maxGasEditText.text.toString(),
                co = coEditText.text.toString(),
                co2 = co2EditText.text.toString(),
                recommendations = recommendationsInputLayout.editText!!.text.toString(),
                performedWork = performedWorkInputLayout.editText!!.text.toString(),
                amountToPay = summaryToPayEditText.text.toString(),
                amountForTheRoad = summaryForRoadEditText.text.toString(),
                amountOfPart = summaryForPartsEditText.text.toString(),
                requestType = RequestType.values()[requestTypeSpinner.selectedItemPosition]
            )
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.request_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            requestTypeSpinner.adapter = adapter
        }

        photoAdapter = PhotoAdapter(mutableListOf(), object : PhotoAdapter.InteractionListener {
            override fun onPhotoRemove(position: Int) {
                presenter.onPhotoRemoved(position)
            }
        })
        photosList.adapter = photoAdapter

        addPhotoButton.setOnClickListener {
            val options = Options.init()
                .setRequestCode(REQUEST_CODE_RESULT_PHOTO)
                .setImageQuality(ImageQuality.HIGH)
                .setImageResolution(720, 480)
                .setCount(99)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_FULL_SENSOR)

            Pix.start(this, options)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_RESULT_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val photos = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    photos.forEach {
                        val bm = BitmapFactory.decodeFile(it)
                        val baos = ByteArrayOutputStream()
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos) //bm is the bitmap object
                        val b = baos.toByteArray()
                        presenter.onTakePhoto(
                            Uri.parse(it),
                            "data:image/jpeg;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"
                        )
                    }
                }
            }
        }
    }

    override fun setupPhoto(photos: MutableList<Uri>) {
        photoAdapter.setupDataSet(photos)
    }

    override fun onPhotoAdded() {
        photoAdapter.itemAdded()
    }

    override fun onPhotoRemoved(position: Int) {
        photoAdapter.itemRemoved(position)
    }

    override fun showRequestNotFoundError() {
        AlertDialog.Builder(this)
            .setMessage(R.string.request_not_found_error_message)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> this@RequestReportActivity.finish() }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, Options.init().setRequestCode(100))
                } else {
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }

    override fun showMessage(reportSent: RequestReportView.Message) {
        Toast.makeText(this, R.string.request_report_successfully_sent_mwssage, Toast.LENGTH_LONG).show()
    }
}
