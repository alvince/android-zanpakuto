package cn.alvince.zanpakuto.core.widget

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import java.lang.ref.WeakReference

class ToastCreator internal constructor(private val context: Context?) {

    @StringRes
    var resId: Int = 0
    var text: String? = null
    var duration: Int = Toast.LENGTH_SHORT
    var gravity: Int = Gravity.BOTTOM

    private val _contextRef = WeakReference(context)

    internal fun make(): Toast? = _contextRef.get()
        ?.takeIf { validate() }
        ?.let {
            if (text.isNullOrEmpty()) {
                Toast.makeText(context, resId, duration)
            } else {
                Toast.makeText(context, text, duration)
            }
        }

    internal fun show(): Toast? {
        return make()
            ?.apply {
                this.gravity
            }
            ?.also { it.show() }
    }

    private fun validate(): Boolean = resId != 0 && !text.isNullOrEmpty()
}

/**
 * Create and show a [Toast]
 *
 * ```
 * toast {
 *     text = "toast text"
 *     //resId = R.string.toast
 *     duration = Toast.LENGTH_SHORT
 * }
 * ```
 */
fun Context.toast(supplier: ToastCreator.() -> Unit): Toast? {
    return ToastCreator(this)
        .apply(supplier)
        .show()
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast? =
    toast {
        this.resId = resId
        this.duration = duration
    }

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT): Toast? =
    toast {
        this.text = text
        this.duration = duration
    }
