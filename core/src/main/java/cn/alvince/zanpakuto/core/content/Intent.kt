package cn.alvince.zanpakuto.core.content

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build

inline fun Intent.createChooser(sender: IntentSender? = null, title: () -> String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        Intent.createChooser(this, title(), sender)
    } else {
        Intent.createChooser(this, title())
    }
}

inline fun <reified T : Activity> Context.startActivity(setup: Intent.() -> Unit = {}) {
    startActivitySafely(Intent(this, T::class.java).apply(setup))
}

inline fun Context.startActivity(component: ComponentName, setup: Intent.() -> Unit = {}) {
    startActivitySafely(Intent.makeMainActivity(component).apply(setup))
}
