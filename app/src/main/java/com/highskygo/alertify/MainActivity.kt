package com.highskygo.alertify

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.highskygo.alertify.model.AppInfo

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: AppListAdapter
    private lateinit var tvPermStatus: TextView
    private lateinit var btnGrant: Button
    private var allApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPostNotificationsIfNeeded()

        tvPermStatus = findViewById(R.id.tvPermStatus)
        btnGrant = findViewById(R.id.btnGrant)
        btnGrant.setOnClickListener { openNotificationAccessSettings() }

        findViewById<MaterialSwitch>(R.id.switchMaster).apply {
            isChecked = Prefs.isMasterEnabled(this@MainActivity)
            setOnCheckedChangeListener { _, checked ->
                Prefs.setMasterEnabled(this@MainActivity, checked)
            }
        }

        adapter = AppListAdapter { app, on -> Prefs.toggleApp(this, app.packageName, on) }
        findViewById<RecyclerView>(R.id.rvApps).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = filter(s?.toString() ?: "")
            override fun afterTextChanged(s: Editable?) {}
        })

        loadApps()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    private fun loadApps() {
        Thread {
            val apps = AppUtils.loadLaunchableApps(this)
            runOnUiThread {
                allApps = apps
                adapter.submit(apps)
            }
        }.start()
    }

    private fun filter(q: String) {
        val lower = q.lowercase()
        adapter.submit(
            if (lower.isBlank()) allApps
            else allApps.filter {
                it.label.lowercase().contains(lower) || it.packageName.contains(lower)
            }
        )
    }

    private fun updatePermissionStatus() {
        if (isNotificationAccessGranted()) {
            tvPermStatus.text = "\u2705 Notification access granted. Alertify is active."
            btnGrant.text = "Manage access"
        } else {
            tvPermStatus.text = "\u26A0\uFE0F Notification access required. Without it, Alertify cannot detect notifications."
            btnGrant.text = "Grant notification access"
        }
    }

    private fun isNotificationAccessGranted(): Boolean {
        val cn = ComponentName(this, AlertifyNotificationListenerService::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }

    private fun openNotificationAccessSettings() {
        try {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }
}
