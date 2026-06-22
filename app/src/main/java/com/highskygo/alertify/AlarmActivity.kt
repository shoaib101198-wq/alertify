package com.highskygo.alertify

import android.content.Context
import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Full-screen alarm screen shown over the lock screen with a STOP button. */
class AlarmActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_APP_LABEL = "extra_app_label"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_TEXT = "extra_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()
        setContentView(R.layout.activity_alarm)

        val appLabel = intent.getStringExtra(EXTRA_APP_LABEL) ?: "App"
        findViewById<TextView>(R.id.tvAppLabel).text = appLabel
        findViewById<TextView>(R.id.tvTitle).text = intent.getStringExtra(EXTRA_TITLE) ?: appLabel
        findViewById<TextView>(R.id.tvText).text = intent.getStringExtra(EXTRA_TEXT) ?: ""

        findViewById<Button>(R.id.btnStop).setOnClickListener { stopAlarm() }
    }

    private fun stopAlarm() {
        AlarmNotifier.cancel(this)
        finish()
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
                .requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @Deprecated("Back press stops the alarm")
    override fun onBackPressed() {
        stopAlarm()
    }
}
