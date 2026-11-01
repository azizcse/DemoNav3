package com.tecfort.navigationtest.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.tecfort.navigationtest.MainActivity
import com.tecfort.navigationtest.R
import com.tecfort.navigationtest.navigation.Dest
import com.tecfort.navigationtest.navigation.PendingNavigation

/**
 * Long-lived foreground service that simulates listening for server events
 * (e.g. new order offers) and renders a floating bottom-sheet overlay for them.
 *
 * A 2-minute self-rescheduling exact alarm fires even in Doze / lock-screen so
 * the overlay can be verified end-to-end.
 */
class EventTrackerService : Service() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var overlay: ComposeOrderOfferOverlay? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startInForeground()
        overlay = ComposeOrderOfferOverlay(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_DUMMY_OFFER -> {
                showDummyOffer()
                scheduleNextOffer()
            }
            ACTION_HIDE_OFFER -> overlay?.hide()
            ACTION_START_TIMER -> scheduleNextOffer()
            ACTION_STOP_TIMER -> cancelAlarm()
            else -> scheduleNextOffer()
        }
        return START_STICKY
    }

    private fun showDummyOffer() {
        if (!canDrawOverlays()) {
            mainHandler.post {
                Toast.makeText(
                    applicationContext,
                    "Grant \"Display over other apps\" permission to show the order sheet.",
                    Toast.LENGTH_LONG,
                ).show()
            }
            requestOverlayPermission()
            return
        }

        wakeScreenBriefly()

        val offer = OrderOffer(
            id = "ORD-" + System.currentTimeMillis(),
            restaurantName = "Takeout Express — Dhanmondi",
            pickupAddress = "House 42, Road 7, Dhanmondi",
            dropoffAddress = "Plot 11, Gulshan Ave, Gulshan 1",
            distanceKm = 4.8,
            payout = 180.0,
        )

        mainHandler.post {
            overlay?.show(
                offer = offer,
                onAccept = {
                    Log.d(TAG, "Accepted ${it.id}")
                    openOrderDetails(it)
                },
                onReject = { Log.d(TAG, "Rejected ${it.id}") },
            )
        }
    }

    private fun openOrderDetails(offer: OrderOffer) {
        PendingNavigation.navigateTo(
            Dest.OrderDetails(
                orderId = offer.id,
                restaurantName = offer.restaurantName,
                pickupAddress = offer.pickupAddress,
                dropoffAddress = offer.dropoffAddress,
                distanceKm = offer.distanceKm,
                payout = offer.payout,
            ),
        )
        if (MainActivity.isInForeground) {
            // Activity is already visible and collecting PendingNavigation; nothing more to do.
            return
        }
        val launch = Intent(this, MainActivity::class.java)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT,
            )
        runCatching { startActivity(launch) }
    }

    /** Turns the screen on for a few seconds so the overlay is visible when device was asleep. */
    @Suppress("DEPRECATION")
    private fun wakeScreenBriefly() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                PowerManager.ACQUIRE_CAUSES_WAKEUP or
                PowerManager.ON_AFTER_RELEASE,
            "$TAG:wake",
        )
        runCatching { wl.acquire(10_000L) }
    }

    private fun scheduleNextOffer() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = alarmPendingIntent()
        val triggerAt = SystemClock.elapsedRealtime() + INTERVAL_MS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
            mainHandler.post {
                Toast.makeText(
                    applicationContext,
                    "Grant \"Alarms & reminders\" permission for exact timing.",
                    Toast.LENGTH_LONG,
                ).show()
            }
            requestExactAlarmPermission()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
        } else {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
        }
        Log.d(TAG, "Next order offer scheduled in ${INTERVAL_MS / 1000}s")
    }

    private fun cancelAlarm() {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(alarmPendingIntent())
    }

    private fun alarmPendingIntent(): PendingIntent {
        val intent = Intent(this, EventTrackerService::class.java)
            .setAction(ACTION_SHOW_DUMMY_OFFER)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, ALARM_REQUEST_CODE, intent, flags)
        } else {
            PendingIntent.getService(this, ALARM_REQUEST_CODE, intent, flags)
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { startActivity(intent) }
    }

    private fun canDrawOverlays(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:$packageName"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { startActivity(intent) }
    }

    override fun onDestroy() {
        cancelAlarm()
        overlay?.destroy()
        overlay = null
        super.onDestroy()
    }

    private fun startInForeground() {
        val channelId = CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(channelId) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        "Event Tracker",
                        NotificationManager.IMPORTANCE_LOW,
                    ).apply {
                        description = "Listens for incoming order offers."
                    },
                )
            }
        }

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Listening for order offers")
            .setContentText("A new offer will arrive every 2 minutes (test mode).")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val TAG = "EventTrackerService"
        private const val CHANNEL_ID = "event_tracker_channel"
        private const val NOTIFICATION_ID = 4201
        private const val ALARM_REQUEST_CODE = 4202

        /** 2 minutes between simulated offers. */
        private const val INTERVAL_MS = 2L * 60L * 1000L

        const val ACTION_SHOW_DUMMY_OFFER = "com.tecfort.navigationtest.action.SHOW_DUMMY_OFFER"
        const val ACTION_HIDE_OFFER = "com.tecfort.navigationtest.action.HIDE_OFFER"
        const val ACTION_START_TIMER = "com.tecfort.navigationtest.action.START_TIMER"
        const val ACTION_STOP_TIMER = "com.tecfort.navigationtest.action.STOP_TIMER"

        fun start(context: Context) {
            val intent = Intent(context, EventTrackerService::class.java)
                .setAction(ACTION_START_TIMER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun triggerDummyOffer(context: Context) {
            val intent = Intent(context, EventTrackerService::class.java)
                .setAction(ACTION_SHOW_DUMMY_OFFER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
