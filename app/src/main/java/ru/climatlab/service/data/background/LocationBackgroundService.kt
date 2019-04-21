package ru.climatlab.service.data.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import ru.climatlab.service.App
import ru.climatlab.service.R
import ru.climatlab.service.ui.login.LoginActivity

class LocationBackgroundService : Service() {

    companion object {
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

    override fun onCreate() {
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
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.DEFAULT_LIGHTS)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(bigStyle)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
//                .setContentTitle(getString(R.string.rent_via_bluetooth_title))
//                .setContentText(getString(R.string.rent_via_bluetooth_text))
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (notificationManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= 26) {
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        throw NotImplementedError("${this.javaClass.simpleName} is not bind service!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    private fun handleBleError(e: Throwable) {

    }

}