package ru.climatlab.service.data.background

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import ru.climatlab.service.App
import ru.climatlab.service.R
import ru.climatlab.service.addSchedulers
import ru.climatlab.service.data.backend.ClimatLabRepositoryProvider
import ru.climatlab.service.ui.login.LoginActivity

class LocationBackgroundService : Service() {

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

        private const val NOTIFICATION_ID = 65798755 // random int
        fun isServiceRunning(): Boolean {
            val activityManager = App.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                service?.let {
                    if (LocationBackgroundService::class.java.canonicalName == it.service.className) return true
                }
            }
            return false
        }
    }

    private lateinit var locationProviderClient: FusedLocationProviderClient

    private val request = LocationRequest().apply {
        interval = 1 * 60 * 1000
        fastestInterval = 1 * 30 * 1000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                ClimatLabRepositoryProvider.instance.sendUserLocation(location.latitude, location.longitude)
                    .addSchedulers()
                    .subscribe({
                        Log.d(this@LocationBackgroundService.javaClass.simpleName, "Location updated")
                    }, {})
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        start()
    }

    private fun start() {
        locationProviderClient = FusedLocationProviderClient(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.ongoing_notification_channel_id)
        val bigStyle = NotificationCompat.BigTextStyle()
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(bigStyle)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentTitle(getString(R.string.background_work))
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder {
        throw NotImplementedError("${this.javaClass.simpleName} is not bind service!")
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                start()
                locationProviderClient.requestLocationUpdates(request, locationCallback, null)
            }
            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopService()
            }
        }
        return START_STICKY
    }

    private fun stopService() {
        locationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }
}