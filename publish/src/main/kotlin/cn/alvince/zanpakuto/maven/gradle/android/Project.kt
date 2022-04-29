package cn.alvince.zanpakuto.maven.gradle.android

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

inline val Project.isAndroidApp: Boolean get() = plugins.hasPlugin("com.android.application")

inline val Project.isAndroidLib: Boolean get() = plugins.hasPlugin("com.android.library")

inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T
