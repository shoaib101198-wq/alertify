package com.highskygo.alertify

import android.content.Context
import android.content.Intent
import com.highskygo.alertify.model.AppInfo

object AppUtils {

    fun labelForPackage(ctx: Context, pkg: String): String = try {
        val pm = ctx.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    } catch (e: Exception) {
        pkg
    }

    /** Returns all user-launchable apps (excluding Alertify itself), sorted by name. */
    fun loadLaunchableApps(ctx: Context): List<AppInfo> {
        val pm = ctx.packageManager
        val monitored = Prefs.getMonitoredApps(ctx)
        val result = ArrayList<AppInfo>()
        val seen = HashSet<String>()

        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        for (ri in pm.queryIntentActivities(intent, 0)) {
            val pkg = ri.activityInfo.packageName
            if (pkg == ctx.packageName) continue
            if (!seen.add(pkg)) continue
            try {
                val ai = pm.getApplicationInfo(pkg, 0)
                result.add(
                    AppInfo(
                        packageName = pkg,
                        label = pm.getApplicationLabel(ai).toString(),
                        icon = pm.getApplicationIcon(ai),
                        monitored = monitored.contains(pkg)
                    )
                )
            } catch (e: Exception) { /* skip */ }
        }
        result.sortBy { it.label.lowercase() }
        return result
    }
}
