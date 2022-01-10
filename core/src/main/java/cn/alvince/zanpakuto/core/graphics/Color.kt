package cn.alvince.zanpakuto.core.graphics

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.Size
import java.util.Locale

/**
 * Color value class
 *
 * Created by alvince on 2021/12/27
 *
 * @author alvince.zy@gmail.com
 */
inline class Color(private val rawValue: Long) {

    @get:ColorInt
    val colorInt: Int
        get() = rawValue.toInt()

    @get:IntRange(from = 0, to = 255)
    val alpha: Int
        get() = RawColor.alpha(colorInt)

    @get:IntRange(from = 0, to = 255)
    val red: Int
        get() = RawColor.red(colorInt)

    @get:IntRange(from = 0, to = 255)
    val green: Int
        get() = RawColor.green(colorInt)

    @get:IntRange(from = 0, to = 255)
    val blue: Int
        get() = RawColor.blue(colorInt)

    operator fun component1(): Int = red

    operator fun component2(): Int = green

    operator fun component3(): Int = blue

    operator fun component4(): Int = alpha

    fun toHSV(@Size(3) hsv: FloatArray): FloatArray {
        RawColor.colorToHSV(colorInt, hsv)
        return hsv
    }

    @Size(3)
    fun toHSVArray(): FloatArray {
        return FloatArray(3).also { RawColor.colorToHSV(colorInt, it) }
    }

    inline fun toComponents(action: (r: Int, g: Int, b: Int) -> Unit) {
        val (r, g, b) = this
        action(r, g, b)
    }

    inline fun toComponents(action: (r: Int, g: Int, b: Int, alpha: Int) -> Unit) {
        val (r, g, b, alpha) = this
        action(r, g, b, alpha)
    }

    companion object {

        val UNKNOWN = Color((1L shl 32) or 0L)

        private val COLOR_WEB_PATTERN = "^#([0-1a-fA-F]{6}|[0-1a-fA-F]{8})$".toRegex()

        // from android.graphics.Color.sColorNameMap
        private val COLOR_NAME_ARRAY = arrayOf(
            "black",
            "darkgray",
            "gray",
            "lightgray",
            "white",
            "red",
            "green",
            "blue",
            "yellow",
            "cyan",
            "magenta",
            "aqua",
            "fuchsia",
            "darkgrey",
            "grey",
            "lightgrey",
            "lime",
            "maroon",
            "navy",
            "olive",
            "purple",
            "silver",
            "teal"
        )

        fun of(colorString: String): Color {
            val stdColorFmt = colorString.let {
                if (it[0] == '#') it.matches(COLOR_WEB_PATTERN) else COLOR_NAME_ARRAY.contains(it.toLowerCase(Locale.ROOT))
            }
            if (!stdColorFmt) {
                return UNKNOWN
            }
            return try {
                Color(RawColor.parseColor(colorString).toLong())
            } catch (ex: IllegalArgumentException) {
                UNKNOWN
            }
        }

        fun of(@ColorInt color: Int): Color {
            return Color(color.toLong())
        }

        fun of(@IntRange(from = 0L, to = 255L) red: Int, @IntRange(from = 0L, to = 255L) green: Int, @IntRange(from = 0L, to = 255L) blue: Int): Color {
            return Color(RawColor.rgb(red, green, blue).toLong())
        }

        fun of(
            @IntRange(from = 0L, to = 255L) alpha: Int,
            @IntRange(from = 0L, to = 255L) red: Int,
            @IntRange(from = 0L, to = 255L) green: Int,
            @IntRange(from = 0L, to = 255L) blue: Int
        ): Color {
            return Color(RawColor.argb(alpha, red, green, blue).toLong())
        }

        fun of(@Size(3) hsv: FloatArray, @IntRange(from = 0L, to = 255L) alpha: Int = 0xFF): Color {
            if (hsv.size < 3) {
                throw IllegalArgumentException("3 components required for hsv")
            }
            return Color(RawColor.HSVToColor(alpha, hsv).toLong())
        }

        fun of(
            @FloatRange(from = 0.0, to = 1.0) h: Float,
            @FloatRange(from = 0.0, to = 1.0) s: Float,
            @FloatRange(from = 0.0, to = 1.0) v: Float,
            @IntRange(from = 0L, to = 255L) alpha: Int = 0xFF
        ): Color {
            return Color(RawColor.HSVToColor(alpha, floatArrayOf(h, s, v)).toLong())
        }
    }
}

inline fun Color.orColor(supplier: () -> Color): Color {
    return this.takeIf { it != Color.UNKNOWN } ?: supplier()
}

private typealias RawColor = android.graphics.Color
