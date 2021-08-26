package cn.alvince.zanpakuto.rxjava2.ktx

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.alvince.zanpakuto.rxjava2.RxLifecycleRegistry
import cn.alvince.zanpakuto.rxjava2.internal.DestroyedLifecycleOwner

@MainThread
fun ComponentActivity.rxLifecycleRegistry(): RxLifecycleRegistry {
    if (this is RxLifecycleRegistry) {
        return this
    }
    return obtainDisposableRegistry()
}

private val activityRegistriesRecord = RegistryHolder<RxLifecycleRegistry>()

private val destroyedRegistryHolder = RxLifecycleRegistry().apply { attachToLifecycle(
    DestroyedLifecycleOwner()
) }

@MainThread
private fun ComponentActivity.obtainDisposableRegistry(): RxLifecycleRegistry {
    val key = this.hashCode()
    val exist = activityRegistriesRecord[key]
    if (exist != null) {
        return exist
    }
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
        return destroyedRegistryHolder
    }
    return RxLifecycleRegistry().also { registry ->
        registry.attachToLifecycle(this)
        activityRegistriesRecord.put(key, registry)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    source.lifecycle.removeObserver(this)
                    (source as? ComponentActivity)?.clear()
                }
            }
        })
    }
}

private fun ComponentActivity.clear() {
    val hashKey = this.hashCode()
    activityRegistriesRecord.clear(hashKey)
}
