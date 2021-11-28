package cn.alvince.zanpakuto.core.graphics

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build

/**
 * Provide [Canvas] draw-compat functions
 */
interface CanvasCompat {
    fun Canvas.drawArcCompat(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, useCenter: Boolean, paint: Paint)
}

/**
 * ```
 * class MyView @JvmOverride constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr),
 *     CanvasCompat by CanvasHolder() {
 *
 *     override fun onDraw(canvas: Canvas) {
 *         …
 *         canvas.drawArcCompat(l, t, r, b, start, sweep, false, paint)
 *         …
 *     }
 * }
 * ```
 */
@Suppress("FunctionName") // create CanvasCompat impl proxy
fun CanvasHolder(): CanvasCompat = CanvasCompatHolder()

class CanvasCompatHolder : CanvasCompat {

    private var rectHolder: RectF? = null

    private val rect: RectF
        get() = synchronized(this) {
            rectHolder ?: RectF().also { rectHolder = it }
        }

    override fun Canvas.drawArcCompat(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        paint: Paint
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawArc(0F, 0F, 0F, 0F, 0F, 0F, false, paint)
        } else {
            drawArc(rect, startAngle, sweepAngle, useCenter, paint)
        }
    }
}
