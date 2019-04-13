package ru.climatlab.service.ui.requestReport

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_request_report.*
import ru.climatlab.service.R
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import java.io.ByteArrayOutputStream
import java.io.File


class RequestReportActivity : BaseActivity(), RequestReportView {
    companion object {
        const val EXTRA_KEY_REQUEST_ID = "extra_key_request_id"
    }

    private val REQUEST_CODE_BOILER_PHOTO = 1
    private val REQUEST_CODE_RESULT_PHOTO = 2

    @InjectPresenter
    lateinit var presenter: RequestReportPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_report)

        presenter.onAttach(intent.getStringExtra(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID))

        boilerPhotoCard.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .maxResultSize(480, 720)
                .start(REQUEST_CODE_BOILER_PHOTO)
        }
        resultPhotoCard.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .maxResultSize(480, 720)
                .start(REQUEST_CODE_RESULT_PHOTO)
        }

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
                amountToPay = summaryToPayEditText.text.toString(),
                amountForTheRoad = summaryForRoadEditText.text.toString(),
                amountOfPart = summaryForPartsEditText.text.toString()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_BOILER_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data
                    //You can get File object from intent
                    val file: File = ImagePicker.getFile(data)!!
                    //You can also get File Path from intent
                    val filePath: String = ImagePicker.getFilePath(data)!!

                    val bm = BitmapFactory.decodeFile(ImagePicker.getFilePath(data))
                    val baos = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, baos) //bm is the bitmap object
                    val b = baos.toByteArray()
                    val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                    presenter.onBoilerPhotoTaken(encodedImage!!)

                    boilerPhotoImageView.visibility = View.VISIBLE
                    boilerPhotoPlaceholder.visibility = View.GONE

                    Glide.with(this)
                        .load(fileUri)
                        .into(boilerPhotoImageView)
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CODE_RESULT_PHOTO->{
                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data
                    //You can get File object from intent
                    val file: File = ImagePicker.getFile(data)!!
                    //You can also get File Path from intent
                    val filePath: String = ImagePicker.getFilePath(data)!!

                    val bm = BitmapFactory.decodeFile(ImagePicker.getFilePath(data))
                    val baos = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, baos) //bm is the bitmap object
                    val b = baos.toByteArray()
                    val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                    presenter.onResultPhotoTaken(encodedImage!!)

                    resultPhotoImageView.visibility = View.VISIBLE
                    resultPhotoPlaceholder.visibility = View.GONE

                    Glide.with(this)
                        .load(fileUri)
                        .into(resultPhotoImageView)
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun showRequestNotFoundError() {
        AlertDialog.Builder(this)
            .setMessage(R.string.request_not_found_error_message)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> this@RequestReportActivity.finish() }
            .show()
    }

    override fun showMessage(reportSent: RequestReportView.Message) {
        Toast.makeText(this, R.string.request_report_successfully_sent_mwssage, Toast.LENGTH_LONG).show()
    }
}
