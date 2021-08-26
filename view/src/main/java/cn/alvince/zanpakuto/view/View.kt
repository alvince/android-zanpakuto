package cn.alvince.zanpakuto.view

import android.view.View

val View.visible: Boolean get() = visibility == View.VISIBLE

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

fun View.gone() {
    visibility = View.GONE
}

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

/**
 * Clear registered [View.OnClickListener] and [View.OnLongClickListener]
 */
fun View.clearClicks() {
    setOnClickListener(null)
    setOnLongClickListener(null)
}
