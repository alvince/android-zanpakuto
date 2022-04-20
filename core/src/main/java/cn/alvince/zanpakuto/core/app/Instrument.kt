package cn.alvince.zanpakuto.core.app

import android.os.Build

inline fun checkOsVersion(versionCode: Int): Boolean = Build.VERSION.SDK_INT >= versionCode

inline fun <T> meetVersion(versionCode: Int, block: () -> T): T? {
    return if (checkOsVersion(versionCode)) block() else null
}
