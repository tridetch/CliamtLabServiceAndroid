package ru.climatlab.service.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.cancel_request_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.request_bottom_sheet.*
import kotlinx.android.synthetic.main.request_bottom_sheet.view.*
import org.jetbrains.anko.intentFor
import ru.climatlab.service.R
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import ru.climatlab.service.ui.requestReport.RequestReportActivity
import java.text.SimpleDateFormat
import java.util.*

class MapActivity : AppCompatActivity(), MapView, OnMapReadyCallback {

    private val REQUEST_CODE_LOCATION = 0

    companion object {
        private const val KEY_MAP_VIEW_OUT_STATE = "mapview_state"
    }

    @InjectPresenter
    lateinit var presenter: MapPresenter
    private val mvpDelegate: MvpDelegate<out MapActivity> by lazy { MvpDelegate(this) }
    private var isStateSaved: Boolean = false
    private var isGoogleMapReady: Boolean = false

    private lateinit var map: GoogleMap
    lateinit var loadingDialog: ProgressDialog

    private var errorAlert: AlertDialog? = null
    lateinit var requestBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var locationProvider: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpDelegate.onCreate(savedInstanceState)
        locationProvider = FusedLocationProviderClient(this)
        setContentView(R.layout.activity_map)

        loadingDialog = ProgressDialog(this).apply {
            isIndeterminate = true
            setCancelable(false)
            setMessage(getString(R.string.loading_message))
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        isGoogleMapReady = false
        mapFragment.getMapAsync(this)

        requestBottomSheetBehavior = BottomSheetBehavior.from(requestInfoCard)
        requestBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> presenter.onRequestSelected(null)
                    else -> {
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_refresh -> {
                presenter.onUpdateRequestClick()
                Toast.makeText(this, getString(R.string.request_updates_message), Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        isStateSaved = true
        mvpDelegate.onSaveInstanceState(outState)
        mvpDelegate.onDetach()

        val bundle = Bundle()
        outState.putBundle(KEY_MAP_VIEW_OUT_STATE, bundle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_map_menu, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        isStateSaved = false
        if (isGoogleMapReady) {
            mvpDelegate.onAttach()
        }
        presenter.onResume()
    }

    override fun onStart() {
        super.onStart()
        isStateSaved = false
        if (isGoogleMapReady) {
            mvpDelegate.onAttach()
        }
    }

    override fun onStop() {
        super.onStop()
        mvpDelegate.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.isFinishing) {
            mvpDelegate.onDestroy()
            return
        }

        // When we rotate device isRemoving() return true for fragment placed in backstack
        // http://stackoverflow.com/questions/34649126/fragment-back-stack-and-isremoving
        if (isStateSaved) {
            isStateSaved = false
            return
        }

        mvpDelegate.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            map.isMyLocationEnabled = true
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMarkerClickListener {
            presenter.onRequestSelected(it.tag as Request)
            true
        }

        isGoogleMapReady = true
        mvpDelegate.onAttach()
    }

    override fun focusCurrentLocation() {
        try {
            val location: Location = map.myLocation
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 14f))
        } catch (e: Exception) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.055, 43.055), 14f))
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

    override fun showRequests(requests: List<Request>) {
        map.clear()
        requests.forEach { request ->
            map.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            request.getCoordinates()!!.latitude,
                            request.getCoordinates()!!.longitude
                        )
                    )
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            when (request.status) {
                                RequestStatus.NewRequest -> BitmapDescriptorFactory.HUE_RED
                                else -> {
                                    BitmapDescriptorFactory.HUE_CYAN
                                }
                            }
                        )
                    )
            ).tag = request
        }
    }

    override fun showRequestBottomCard(request: Request) {
        requestInfoCard.clientFullNameTextView.text = request.clientInfo?.fullName()
        requestInfoCard.dateTextView.text =
            SimpleDateFormat("dd.MM hh:mm", Locale.getDefault()).format(Date(request.date))
        requestInfoCard.officeTitleNameTextView.text = request.office
        requestInfoCard.equipmentTextView.text = request.equipmentId
        requestInfoCard.phoneNumber.text = "8${request.clientInfo?.phone}"
        requestInfoCard.addressTextView.text = request.address
        requestInfoCard.descriptionTextView.text = request.description
        if (request.addressDetails.isNullOrBlank()) {
            requestInfoCard.addressDetailsTextView.visibility = View.GONE
        } else {
            requestInfoCard.addressDetailsTextView.visibility = View.VISIBLE
            requestInfoCard.addressDetailsTextView.text = request.addressDetails
        }
        requestInfoCard.callButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:8${request.clientInfo?.phone}")))
        }
        requestInfoCard.buildRouteButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=${request.getCoordinates().latitude},${request.getCoordinates().longitude}(${request.address})")
                    ), getString(R.string.map_chooser_title)
                )
            )
        }
        when (request.status) {
            RequestStatus.NewRequest -> {
                requestInfoCard.requestActionButton.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
                requestInfoCard.requestActionButton.text = getString(R.string.accept_request_button_label)
                requestInfoCard.requestActionButton.setOnClickListener { presenter.onAcceptRequest(request) }
                requestInfoCard.openRequestImageButton.setOnClickListener {
                    hideRequestBottomCard()
                    startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.id))
                }
            }
            RequestStatus.InWork -> {
                requestInfoCard.requestActionButton.setOnClickListener {
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
                requestInfoCard.requestActionButton.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    )
                )
                requestInfoCard.requestActionButton.text = getString(R.string.cancel_request_button_label)
                requestInfoCard.openRequestImageButton.setOnClickListener {
                    startActivity(intentFor<RequestReportActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.id))
                }
            }
            else -> {
            }
        }
        requestBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun showMessage(message: MapView.Message) {
        val resId = when (message) {
            MapView.Message.RequestAccepted -> R.string.request_accepted_message
            MapView.Message.RequestCanceled -> R.string.request_canceled_message
        }
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
    }

    override fun hideRequestBottomCard() {
        requestBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun showRequestDetails(request: Request) {
        startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.id))
    }

    override fun showRequestReportScreen(request: Request) {
        startActivity(intentFor<RequestReportActivity>(RequestReportActivity.EXTRA_KEY_REQUEST_ID to request.id))
    }

    override fun closeScreen() {
        finish()
    }

    override fun focusRequests(requests: List<Request>) {
        if (requests.size < 2) return
        val boundsBuilder = LatLngBounds.builder()
        requests.onEach {
            boundsBuilder.include(
                LatLng(
                    it.getCoordinates().latitude,
                    it.getCoordinates().longitude
                )
            )
        }
        val view = mapView.view
        if (view != null) {
            val width = view.measuredWidth
            val height = view.measuredHeight
            val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), width, height, padding)
            map.animateCamera(cameraUpdate)
        }
    }
}
