package com.highskygo.alertify

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Listens to ALL notifications. When one comes from a monitored app,
 * it fires the looping alarm. Requires the user to grant "Notification access".
 */
class AlertifyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        val pkg = sbn.packageName ?: return

        if (pkg == packageName) return                  // ignore our own
        if (!Prefs.isMasterEnabled(this)) return        // master switch off
        if (!Prefs.isMonitored(this, pkg)) return       // app not selected

        val extras = sbn.notification?.extras
        val title = extras?.getString(Notification.EXTRA_TITLE)
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val appLabel = AppUtils.labelForPackage(this, pkg)

        AlarmNotifier.trigger(
            ctx = this,
            appLabel = appLabel,
            title = title ?: appLabel,
            text = text ?: ""
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) { /* no-op */ }
}
