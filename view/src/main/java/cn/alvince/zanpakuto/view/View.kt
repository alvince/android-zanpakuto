package cn.alvince.zanpakuto.view

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

val View.visible: Boolean get() = visibility == View.VISIBLE

fun View.gone() {
    visibility = View.GONE
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

/**
 * Fast register click callback
 */
fun View.onClick(block: (View) -> Unit) {
    setOnClickListener(block)
}

/**
 * Fast register long-click callback
 */
fun View.onLongClick(block: (View) -> Boolean) {
    setOnLongClickListener(block)
}

/**
 * Fast register context-click callback
 */
@TargetApi(Build.VERSION_CODES.M)
@RequiresApi(Build.VERSION_CODES.M)
fun View.onContextClick(onContextClick: (View) -> Boolean) {
    setOnContextClickListener(onContextClick)
}

/**
 * Clear registered [View.OnClickListener] and [View.OnLongClickListener]
 *
 * Also clear [View.OnContextClickListener] on above [Build.VERSION_CODES.M]
 */
fun View.clearClicks() {
    setOnClickListener(null)
    setOnLongClickListener(null)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setOnContextClickListener(null)
    }
}
