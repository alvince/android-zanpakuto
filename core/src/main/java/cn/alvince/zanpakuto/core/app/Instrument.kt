package cn.alvince.zanpakuto.core.app

import android.os.Build

fun checkOsVersion(versionCode: Int): Boolean = Build.VERSION.SDK_INT >= versionCode

inline fun <T> meetVersion(versionCode: Int, block: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= versionCode) block() else null
}
