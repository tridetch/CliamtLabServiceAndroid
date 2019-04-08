package ru.climatlab.service.ui.map

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.request_bottom_sheet.*
import kotlinx.android.synthetic.main.request_bottom_sheet.view.*
import ru.climatlab.service.R
import ru.climatlab.service.data.model.RequestModel
import ru.climatlab.service.data.model.RequestStatus

class MapActivity : AppCompatActivity(), MapView, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    lateinit var presenter: MapPresenter

    private val mvpDelegate: MvpDelegate<out MapActivity> by lazy { MvpDelegate(this) }
    private var isStateSaved: Boolean = false
    private var isGoogleMapReady: Boolean = false
    private lateinit var map: GoogleMap

    lateinit var loadingDialog: ProgressDialog
    private var errorAlert: AlertDialog? = null

    lateinit var requestBottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        private const val KEY_MAP_VIEW_OUT_STATE = "mapview_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpDelegate.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
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

        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.055, 43.055), 14f))

        map.setOnMarkerClickListener {
            presenter.onRequestSelected(it.tag as RequestModel)
            true
        }

        isGoogleMapReady = true
        mvpDelegate.onAttach()
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

    override fun showRequests(requests: List<RequestModel>) {
        map.clear()
        requests.forEach {
            val requestMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(it.coordinates.latitude, it.coordinates.longitude))
            )
                .setTag(it)
        }
    }

    override fun showRequestBottomCard(selectedRequest: RequestModel) {
        requestInfoCard.clientFullNameTextView.text = selectedRequest.clientId
        requestInfoCard.officeTitleNameTextView.text = selectedRequest.office
        requestInfoCard.equipmentTextView.text = selectedRequest.equipmentId
        requestInfoCard.addressTextView.text = selectedRequest.address
        when (selectedRequest.status) {
            RequestStatus.NewRequest -> {
                requestInfoCard.requestActionButton.background.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
                requestInfoCard.requestActionButton.text = getString(R.string.accept_request_button_label)
            }
            RequestStatus.InWork -> {
                requestInfoCard.requestActionButton.background.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
                requestInfoCard.requestActionButton.text = getString(R.string.cancel_request_button_label)
            }
            else -> {
            }
        }
        requestBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideRequestBottomCard() {
        requestBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}
