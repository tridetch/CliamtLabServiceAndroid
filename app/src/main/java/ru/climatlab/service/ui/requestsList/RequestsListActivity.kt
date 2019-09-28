package ru.climatlab.service.ui.requestsList

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_requests_list.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.nav_header_drawer.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import ru.climatlab.service.BuildConfig
import ru.climatlab.service.R
import ru.climatlab.service.data.PreferencesRepository
import ru.climatlab.service.data.background.LocationBackgroundService
import ru.climatlab.service.data.model.Request
import ru.climatlab.service.data.model.RequestStatus
import ru.climatlab.service.ui.BaseActivity
import ru.climatlab.service.ui.login.LoginActivity
import ru.climatlab.service.ui.map.MapActivity
import ru.climatlab.service.ui.requestDetailsInfo.RequestDetailsActivity
import ru.climatlab.service.ui.requestReport.RequestReportActivity

class RequestsListActivity : BaseActivity(), RequestsListView, NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_CODE_LOCATION = 0

    companion object {
        /** Must be one of {@link RequestStatus} or empty if filter disabled*/
        const val EXTRA_REQUESTS_FILTER = "EXTRA_REQUESTS_FILTER"
    }

    @InjectPresenter
    lateinit var presenter: RequestsListPresenter

    private lateinit var requestsAdapter: RequestsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.title_activity_requests)

        requestsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        requestsAdapter =
            RequestsRecyclerViewAdapter(mutableListOf(), object : RequestsRecyclerViewAdapter.InteractionListener {
                override fun onClick(request: Request) {
                    presenter.onRequestClick(request)
                }
            })
        requestsRecyclerView.adapter = requestsAdapter
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        navView.getHeaderView(0).navHeaderUserName.text = PreferencesRepository.getCurrentUserInfo()?.getFullName()
        navView.getHeaderView(0).navHeaderTitle.text = "${getString(R.string.app_title)} (v.${BuildConfig.VERSION_CODE}"
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val requestFilter = intent?.getSerializableExtra(EXTRA_REQUESTS_FILTER) as RequestStatus?
        supportActionBar!!.title = when (requestFilter) {
            RequestStatus.NewRequest -> "${getString(R.string.title_activity_requests)} (${getString(R.string.requests_filter_new)})"
            RequestStatus.InWork -> "${getString(R.string.title_activity_requests)} (${getString(R.string.requests_filter_in_work)})"
            RequestStatus.Cancelled -> "${getString(R.string.title_activity_requests)} ${getString(R.string.requests_filter_cancelled)})"
            null -> getString(R.string.title_activity_requests)
        }
        presenter.onAttach(requestFilter)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_request_list->{
                startActivity(intentFor<RequestsListActivity>(RequestsListActivity.EXTRA_REQUESTS_FILTER to RequestStatus.NewRequest))
            }
            R.id.nav_map -> {
                startActivity<MapActivity>()
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
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

        presenter.onResume()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
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

    override fun updateData(requests: List<Request>) {
        requestsAdapter.updateDataSet(requests)
    }

    override fun showRequestDetailsScreen(request: Request) {
        startActivity(intentFor<RequestDetailsActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
    }

    override fun showRequestReportScreen(request: Request) {
        startActivity(intentFor<RequestReportActivity>(RequestDetailsActivity.EXTRA_KEY_REQUEST_ID to request.requestInfo.id))
    }

    override fun showLoginScreen() {
        val intent = Intent(this, LocationBackgroundService::class.java)
        intent.action = LocationBackgroundService.ACTION_STOP_FOREGROUND_SERVICE
        startService(intent)
        finish()
        startActivity<LoginActivity>()
    }

}
