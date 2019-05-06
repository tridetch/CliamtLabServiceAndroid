package ru.climatlab.service.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
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
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.cancel_request_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.nav_header_drawer.view.*
import kotlinx.android.synthetic.main.request_bottom_sheet.*
import kotlinx.android.synthetic.main.request_bottom_sheet.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import ru.climatlab.service.R
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.background.LocationBackgroundService
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.login.LoginActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import ru.climatlab.service.ui.requestReport.RequestReportActivity
import ru.climatlab.service.ui.requestsList.RequestsListActivity

class MapActivity : AppCompatActivity(), MapView, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.title_activity_map)

        loadingDialog = ProgressDialog(this).apply {
            isIndeterminate = true
            setCancelable(false)
            setMessage(getString(R.string.loading_message))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

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

        navView.getHeaderView(0).navHeaderUserName.text = PreferencesRepository.getCurrentUserInfo()?.getFullName()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_all_requests -> {
                startActivity(intentFor<RequestsListActivity>(RequestsListActivity.EXTRA_REQUESTS_FILTER to RequestStatus.NewRequest))
            }
            R.id.nav_accepted_requests -> {
                startActivity(intentFor<RequestsListActivity>(RequestsListActivity.EXTRA_REQUESTS_FILTER to RequestStatus.InWork))
            }
            R.id.nav_instructions -> {
                val instructionIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instructions_url)))
                if (instructionIntent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(instructionIntent, getString(R.string.web_chooser_title)))
                }
                startActivity(instructionIntent)
            }
            R.id.nav_exit -> {
                presenter.onLogoutClick()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        isStateSaved = true
        mvpDelegate.onSaveInstanceState(outState)
        mvpDelegate.onDetach()

        val bundle = Bundle()
        outState.putBundle(KEY_MAP_VIEW_OUT_STATE, bundle)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        isStateSaved = false
        if (isGoogleMapReady) {
            mvpDelegate.onAttach()
        }
        presenter.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        } else {
            startLocationService()
        }
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
            startLocationService()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationBackgroundService::class.java)
        intent.action = LocationBackgroundService.ACTION_START_FOREGROUND_SERVICE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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
                            request.requestInfo.getCoordinates()!!.latitude,
                            request.requestInfo.getCoordinates()!!.longitude
                        )
                    )
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            when (request.requestInfo.status) {
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
        requestInfoCard.clientFullNameTextView.text = request.clientResponseModel.fullName()
        requestInfoCard.dateTextView.text = DateUtils.formatDateTime(this, request.requestInfo.date, DateUtils.FORMAT_NUMERIC_DATE)
        requestInfoCard.officeTitleNameTextView.text = request.requestInfo.office
        requestInfoCard.equipmentTextView.text = request.requestInfo.equipmentId
        requestInfoCard.phoneNumber.text = "8${request.clientResponseModel.phone}"
        requestInfoCard.addressTextView.text = request.requestInfo.address
        requestInfoCard.descriptionTextView.text = request.requestInfo.description
        requestInfoCard.callButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:8${request.clientResponseModel.phone}")))
        }
        requestInfoCard.buildRouteButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=${request.requestInfo.getCoordinates()?.latitude},${request.requestInfo.getCoordinates()?.longitude}(${request.requestInfo.address})")
                    ), getString(R.string.map_chooser_title)
                )
            )
        }
        when (request.requestInfo.status) {
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
                    startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
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
                    startActivity(intentFor<RequestReportActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
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
        startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
    }

    override fun showRequestReportScreen(request: Request) {
        startActivity(intentFor<RequestReportActivity>(RequestReportActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
    }

    override fun showLoginScreen() {
        val intent = Intent(this, LocationBackgroundService::class.java)
        intent.action = LocationBackgroundService.ACTION_STOP_FOREGROUND_SERVICE
        startService(intent)
        finish()
        startActivity<LoginActivity>()
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
                    it.requestInfo.getCoordinates().latitude,
                    it.requestInfo.getCoordinates().longitude
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
