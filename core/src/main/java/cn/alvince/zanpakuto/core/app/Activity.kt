package cn.alvince.zanpakuto.core.app

import android.app.Activity
import android.view.View

val Activity.contentView: View? get() = this.window?.decorView?.findViewById(android.R.id.content)

fun Activity.finishIfNeeded() {
    if (isFinishing || isDestroyed) {
        return
    }
    finish()
}
