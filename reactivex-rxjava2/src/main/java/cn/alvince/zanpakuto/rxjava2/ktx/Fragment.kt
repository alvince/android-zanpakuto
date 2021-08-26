package cn.alvince.zanpakuto.rxjava2.ktx

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cn.alvince.zanpakuto.rxjava2.RxFragmentLifecycleRegistry
import cn.alvince.zanpakuto.rxjava2.internal.DestroyedLifecycleOwner

@MainThread
fun Fragment.rxLifecycleRegistry(): RxFragmentLifecycleRegistry {
    if (this is RxFragmentLifecycleRegistry) {
        return this
    }
    return obtainRxRegistry()
}

private val fragmentRegistriesRecord = RegistryHolder<RxFragmentLifecycleRegistry>()

private val destroyedRegistryHolder = RxFragmentLifecycleRegistry().apply {
    DestroyedLifecycleOwner().also {
        attachToLifecycle(it)
        attachToViewLifecycle(it)
    }
}

@MainThread
private fun Fragment.obtainRxRegistry(): RxFragmentLifecycleRegistry {
    val key = this.hashCode()
    val exist = fragmentRegistriesRecord[key]
    if (exist != null) {
        return exist
    }
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
        return destroyedRegistryHolder
    }
    return RxFragmentLifecycleRegistry().also { registry ->
        registry.attachToLifecycle(this)
        fragmentRegistriesRecord.put(key, registry)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    source.lifecycle.removeObserver(this)
                    (source as? Fragment)?.clear()
                }
            }
        })
        viewLifecycleOwnerLiveData.observe(this) { registry.attachToViewLifecycle(it) }
    }
}

private fun Fragment.clear() {
    val hashKey = this.hashCode()
    fragmentRegistriesRecord.clear(hashKey)
}
