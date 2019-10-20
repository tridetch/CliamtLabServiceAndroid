package ru.climatlab.service.ui.requestReport

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Base64OutputStream
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
import ru.climatlab.service.data.model.SelectedFile
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import java.io.*


class RequestReportActivity : BaseActivity(), RequestReportView {
    companion object {
        const val EXTRA_KEY_REQUEST_ID = "extra_key_request_id"
    }

    private val REQUEST_CODE_RESULT_PHOTO = 1
    private val REQUEST_CODE_RESULT_DOC = 2

    @InjectPresenter
    lateinit var presenter: RequestReportPresenter

    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var fileAdapter: FileAdapter

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
        fileAdapter = FileAdapter(mutableListOf(), object : FileAdapter.InteractionListener {
            override fun onFileRemove(position: Int) {
                presenter.onFileRemoved(position)
            }
        })
        fileList.adapter = fileAdapter
        addFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //intent.putExtra("browseCoa", itemToBrowse);
            //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
            try {
                //startActivityForResult(chooser, FILE_SELECT_CODE);
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), REQUEST_CODE_RESULT_DOC)
            } catch (ex: Exception) {
                println("browseClick :$ex")//android.content.ActivityNotFoundException ex
            }

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
            REQUEST_CODE_RESULT_DOC -> {
                try {
                    val uri = data?.data?.let { uri: Uri ->
                        /*
                                            if (filesize >= FILE_SIZE_LIMIT) {
                                                Toast.makeText(
                                                    this,
                                                    "The selected file is too large. Selet a new file with size less than 2mb",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                        */
                        val mimeType = contentResolver.getType(uri)
                        val filename: String
                        if (mimeType == null) {
                            val path = getPath(this, uri)
                            if (path == null) {
                                filename = uri?.lastPathSegment
                            } else {
                                val file = File(path)
                                filename = file.getName()
                            }
                        } else {
                            val returnUri = data.getData()
                            val returnCursor = contentResolver.query(returnUri, null, null, null, null)
                            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                            returnCursor.moveToFirst()
                            filename = returnCursor.getString(nameIndex)
                            val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
                        }
                        val sourcePath = getExternalFilesDir(null)!!.toString()
                        try {
                            val fileSave = File(sourcePath + "/" + filename)
                            copyFileStream(fileSave, uri, this)
                            presenter.onFileSelected(convertImageFileToBase64(fileSave), filename)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
//                    }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun copyFileStream(dest: File, uri: Uri, context: Context) {
        var ins: InputStream? = null
        var os: OutputStream? = null
        try {
            ins = context.getContentResolver().openInputStream(uri)
            os = FileOutputStream(dest)
            val buffer: ByteArray? = ByteArray(1024)
            var length: Int = ins.read(buffer)
            while (length > 0) {
                os.write(buffer, 0, length)
                length = ins.read(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ins?.close()
            os?.close()
        }
    }

    fun convertImageFileToBase64(imageFile: File): String {

        return FileInputStream(imageFile).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close() // This line is required, see comments
                    outputStream.toString()
                }
            }
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/{$split[1}]"
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        }
        // MediaStore (and general)
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val index = cursor!!.getColumnIndexOrThrow(column)
                return cursor!!.getString(index)
            }
        } finally {
            if (cursor != null) {
            }
            cursor!!.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    override fun setupPhoto(photos: MutableList<Uri>) {
        photoAdapter.setupDataSet(photos)
    }

    override fun setupFiles(files: MutableList<SelectedFile>) {
        fileAdapter.setupDataSet(files)
    }

    override fun onPhotoAdded() {
        photoAdapter.itemAdded()
    }

    override fun onFileAdded() {
        fileAdapter.itemAdded()
    }

    override fun onPhotoRemoved(position: Int) {
        photoAdapter.itemRemoved(position)
    }

    override fun onFileRemoved(position: Int) {
        fileAdapter.itemRemoved(position)
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
