/*
 * Created by alvince on 2022/1/20
 *
 * @author alvincezhang@didiglobal.com
 */

package cn.alvince.zanpakuto.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import cn.alvince.zanpakuto.core.app.meetVersion

open class ActivityLifecycleCallbacksAdapter : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}

/**
 * Compatible register [Application.ActivityLifecycleCallbacks] from [Activity]
 */
fun Activity.registerLifecycleCallbacks(callback: Application.ActivityLifecycleCallbacks) {
    meetVersion(VERSION_CODES.Q) { this.registerActivityLifecycleCallbacks(callback) }
        ?: this.application.registerActivityLifecycleCallbacks(callback)
}

/**
 * Compatible unregister [Application.ActivityLifecycleCallbacks] from [Activity]
 */
fun Activity.unregisterLifecycleCallbacks(callback: Application.ActivityLifecycleCallbacks) {
    meetVersion(VERSION_CODES.Q) { this.unregisterActivityLifecycleCallbacks(callback) }
        ?: this.application.unregisterActivityLifecycleCallbacks(callback)
}

inline fun Activity.doOnDestroy(crossinline onDestroy: Activity.() -> Unit) {
    if (this is LifecycleOwner) {
        this.lifecycle.doOnDestroy { onDestroy(this) }
        return
    }
    registerLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
        override fun onActivityDestroyed(activity: Activity) {
            super.onActivityDestroyed(activity)
            onDestroy(activity)
            unregisterLifecycleCallbacks(this)
        }
    })
}
