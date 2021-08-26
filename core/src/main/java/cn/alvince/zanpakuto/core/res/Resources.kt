package cn.alvince.zanpakuto.core.res

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

@ColorInt
fun Resources.color(@ColorRes id: Int, theme: Resources.Theme? = null): Int {
    return try {
        ResourcesCompat.getColor(this, id, theme)
    } catch (ex: Resources.NotFoundException) {
        0
    }
}

fun Resources.drawable(@DrawableRes id: Int, theme: Resources.Theme? = null): Drawable? {
    return try {
        ResourcesCompat.getDrawable(this, id, theme)
    } catch (ex: Resources.NotFoundException) {
        null
    }
}

fun Resources.drawableForDensity(@DrawableRes id: Int, density: Int, theme: Resources.Theme? = null): Drawable? {
    return try {
        ResourcesCompat.getDrawableForDensity(this, id, density, theme)
    } catch (ex: Resources.NotFoundException) {
        null
    }
}
