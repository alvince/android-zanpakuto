package cn.alvince.zanpakuto.core.content

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import java.io.File

inline fun <reified T> Context.getSysService(): T? {
    return ContextCompat.getSystemService(this, T::class.java)
}

inline fun <reified T> Context.getSysServiceName(): String? {
    return ContextCompat.getSystemServiceName(this, T::class.java)
}

fun Context.getDimension(@DimenRes id: Int): Float {
    return try {
        resources.getDimension(id)
    } catch (ex: Resources.NotFoundException) {
        0F
    }
}

@Px
fun Context.getDimensionPixelSize(@DimenRes id: Int): Int {
    return try {
        resources.getDimensionPixelSize(id)
    } catch (ex: Resources.NotFoundException) {
        0
    }
}

@ColorInt
fun Context.color(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.colorStateList(@ColorRes id: Int): ColorStateList? {
    return ContextCompat.getColorStateList(this, id)
}

fun Context.drawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Context.dataDir(): File? {
    return ContextCompat.getDataDir(this)
}

fun Context.externalCacheDirs(): Array<File> {
    return ContextCompat.getExternalCacheDirs(this)
}

fun Context.externalFilesDirs(type: String): Array<File> {
    return ContextCompat.getExternalFilesDirs(this, type)
}

fun Context.startActivitySafely(intent: Intent, options: Bundle? = null): Boolean {
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PackageManager.MATCH_ALL
    } else {
        PackageManager.MATCH_DEFAULT_ONLY
    }
    if (packageManager.resolveActivity(intent, flags) == null) {
        return false
    }
    startActivity(intent, options)
    return true
}
