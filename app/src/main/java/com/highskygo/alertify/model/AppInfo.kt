package com.highskygo.alertify.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    var monitored: Boolean
)
