package cn.alvince.zanpakuto.core.context

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import cn.alvince.zanpakuto.core.content.color
import cn.alvince.zanpakuto.core.content.colorStateList
import cn.alvince.zanpakuto.core.content.dataDir
import cn.alvince.zanpakuto.core.content.drawable
import cn.alvince.zanpakuto.core.content.externalCacheDirs
import cn.alvince.zanpakuto.core.content.externalFilesDirs
import cn.alvince.zanpakuto.core.content.getDimension
import cn.alvince.zanpakuto.core.content.getDimensionPixelSize
import cn.alvince.zanpakuto.core.content.startActivitySafely
import java.io.File

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.getSysService",
    ReplaceWith("getSysService", "cn.alvince.zanpakuto.core.content.getSysService")
)
inline fun <reified T> Context.getSysService(serviceClass: Class<T>): T? {
    return ContextCompat.getSystemService(this, serviceClass)
}

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.getSysServiceName",
    ReplaceWith("getSysServiceName", "cn.alvince.zanpakuto.core.content.getSysServiceName")
)
inline fun <reified T> Context.getSysServiceName(serviceClass: Class<T>): String? {
    return ContextCompat.getSystemServiceName(this, serviceClass)
}

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.getDimension",
    ReplaceWith("getDimension", "cn.alvince.zanpakuto.core.content.getDimension")
)
fun Context.getDimension(@DimenRes id: Int): Float = getDimension(id)

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.getDimensionPixelSize",
    ReplaceWith("getDimensionPixelSize", "cn.alvince.zanpakuto.core.content.getDimensionPixelSize")
)
@Px
fun Context.getDimensionPixelSize(@DimenRes id: Int): Int = getDimensionPixelSize(id)

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.color",
    ReplaceWith("color", "cn.alvince.zanpakuto.core.content.color")
)
@ColorInt
fun Context.color(@ColorRes id: Int): Int = color(id)

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.colorStateList",
    ReplaceWith("colorStateList", "cn.alvince.zanpakuto.core.content.colorStateList")
)
fun Context.colorStateList(@ColorRes id: Int): ColorStateList? = colorStateList(id)

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.drawable",
    ReplaceWith("drawable", "cn.alvince.zanpakuto.core.content.drawable")
)
fun Context.drawable(@DrawableRes id: Int): Drawable? = drawable(id)

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.dataDir",
    ReplaceWith("dataDir", "cn.alvince.zanpakuto.core.content.dataDir")
)
fun Context.dataDir(): File? = dataDir()

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.externalCacheDirs",
    ReplaceWith("externalCacheDirs", "cn.alvince.zanpakuto.core.content.externalCacheDirs")
)
fun Context.externalCacheDirs(): Array<File> = externalCacheDirs()

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.externalFilesDirs",
    ReplaceWith("externalFilesDirs", "cn.alvince.zanpakuto.core.content.externalFilesDirs")
)
fun Context.externalFilesDirs(type: String): Array<File> = externalFilesDirs(type)

@Deprecated(
    "Replace with cn.alvince.zanpakuto.core.content.startActivitySafely",
    ReplaceWith("startActivitySafely", "cn.alvince.zanpakuto.core.content.startActivitySafely")
)
fun Context.startActivitySafely(intent: Intent, options: Bundle? = null): Boolean = startActivitySafely(intent, options)
