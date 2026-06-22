package com.highskygo.alertify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/** Builds the full-screen alarm notification and starts/stops the alarm sound. */
object AlarmNotifier {
    const val CHANNEL_ID = "alertify_alarm_channel"
    const val NOTIF_ID = 4711

    fun trigger(ctx: Context, appLabel: String, title: String, text: String) {
        createChannel(ctx)
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val fsIntent = Intent(ctx, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AlarmActivity.EXTRA_APP_LABEL, appLabel)
            putExtra(AlarmActivity.EXTRA_TITLE, title)
            putExtra(AlarmActivity.EXTRA_TEXT, text)
        }
        val fsPending = PendingIntent.getActivity(
            ctx, 0, fsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(ctx, StopReceiver::class.java).apply {
            action = StopReceiver.ACTION_STOP
        }
        val stopPending = PendingIntent.getBroadcast(
            ctx, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        @Suppress("DEPRECATION")
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notification.Builder(ctx, CHANNEL_ID)
        else
            Notification.Builder(ctx)

        builder.setContentTitle("\uD83D\uDD14 $appLabel")
            .setContentText(if (text.isNotEmpty()) text else "Tap STOP to silence the alarm")
            .setSmallIcon(R.drawable.ic_alarm)
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(Notification.PRIORITY_MAX)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fsPending, true)
            .setContentIntent(fsPending)
            .addAction(R.drawable.ic_stop, "STOP", stopPending)

        nm.notify(NOTIF_ID, builder.build())
        AlarmPlayer.start(ctx)
    }

    fun cancel(ctx: Context) {
        (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIF_ID)
        AlarmPlayer.stop()
    }

    private fun createChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val ch = NotificationChannel(
                    CHANNEL_ID,
                    "Alertify Alarm",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Plays an alarm for monitored app notifications"
                    setSound(null, null)          // sound handled by AlarmPlayer
                    enableVibration(false)        // vibration handled by AlarmPlayer
                    setBypassDnd(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                nm.createNotificationChannel(ch)
            }
        }
    }
}
