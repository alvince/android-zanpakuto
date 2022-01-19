package cn.alvince.zanpakuto.core.dimens

import android.content.Context
import android.util.TypedValue
import androidx.annotation.Px
import kotlin.math.roundToInt

/**
 * Value DP or SP dimension
 *
 * Created by alvince on 2021/8/27
 *
 * @author alvince.zy@gmail.com
 */
inline class ValueDimensions(private val valueAndUnit: Long) {

    companion object {
        private const val DIMENSION_VALUE_MASK: Long = 0xFFFFFFFF

        fun of(value: Float, unit: Int): ValueDimensions {
            return ValueDimensions((unit.toLong() shl 32) or value.toRawBits().toLong())
        }
    }

    /**
     * The unit of this dimension. One of the COMPLEX_UNIT_* constants in [TypedValue].
     */
    val unit: Int get() = (valueAndUnit shr 32).toInt()

    val value: Float get() = Float.fromBits((valueAndUnit and DIMENSION_VALUE_MASK).toInt())

    /**
     * Returns dimen-pixel value
     */
    @Px
    fun toPx(context: Context): Float {
        return TypedValue.applyDimension(unit, value, context.resources.displayMetrics)
    }

    /**
     * Returns dimen-pixel value in [Int]
     */
    @Px
    fun toPxInt(context: Context): Int = toPx(context).roundToInt()
}

val Float.dp: ValueDimensions get() = ValueDimensions.of(this, TypedValue.COMPLEX_UNIT_DIP)

val Float.sp: ValueDimensions get() = ValueDimensions.of(this, TypedValue.COMPLEX_UNIT_SP)

val Int.dp: ValueDimensions get() = this.toFloat().dp

val Int.sp: ValueDimensions get() = this.toFloat().sp
