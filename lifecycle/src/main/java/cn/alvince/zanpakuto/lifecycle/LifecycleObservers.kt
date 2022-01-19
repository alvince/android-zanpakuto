/*
 * Created by alvince on 2022/1/19
 *
 * @author alvincezhang@didiglobal.com
 */

package cn.alvince.zanpakuto.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Create [LifecycleEventObserver]
 *
 * @param removeOnDestroy remove this observer from associated [Lifecycle] while event [Lifecycle.Event.ON_DESTROY]
 */
fun lifecycleObserver(removeOnDestroy: Boolean = true, onStateChanged: (source: LifecycleOwner, event: Event) -> Unit): LifecycleObserver {
    if (!removeOnDestroy) {
        return LifecycleEventObserver(onStateChanged)
    }
    return object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
            }
            onStateChanged(source, event)
        }
    }
}

/**
 * add [LifecycleEventObserver], and remove automatic on destroy
 */
inline fun Lifecycle.addObserverRemoveOnDestroy(crossinline observer: (source: LifecycleOwner, event: Event) -> Unit) {
    addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
            }
            observer(source, event)
        }
    })
}

/**
 * register action, and invoke on lifecycle destroy
 */
inline fun Lifecycle.doOnDestroy(crossinline onDestroy: (source: LifecycleOwner) -> Unit) {
    addObserver(lifecycleObserver { source, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            onDestroy(source)
        }
    })
}
