package com.highskygo.alertify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Stops the alarm when the STOP action on the notification is tapped. */
class StopReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_STOP = "com.highskygo.alertify.ACTION_STOP"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        AlarmNotifier.cancel(context)
    }
}
