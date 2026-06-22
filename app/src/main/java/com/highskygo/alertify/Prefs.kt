package com.highskygo.alertify

import android.content.Context

/** Stores which apps to monitor + master on/off switch. */
object Prefs {
    private const val PREF = "alertify_prefs"
    private const val KEY_APPS = "monitored_apps"
    private const val KEY_ENABLED = "master_enabled"

    fun getMonitoredApps(ctx: Context): MutableSet<String> {
        val sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return HashSet(sp.getStringSet(KEY_APPS, emptySet()) ?: emptySet())
    }

    fun setMonitoredApps(ctx: Context, apps: Set<String>) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putStringSet(KEY_APPS, apps).apply()
    }

    fun isMonitored(ctx: Context, pkg: String): Boolean =
        getMonitoredApps(ctx).contains(pkg)

    fun toggleApp(ctx: Context, pkg: String, on: Boolean) {
        val set = getMonitoredApps(ctx)
        if (on) set.add(pkg) else set.remove(pkg)
        setMonitoredApps(ctx, set)
    }

    fun isMasterEnabled(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setMasterEnabled(ctx: Context, on: Boolean) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ENABLED, on).apply()
    }
}
