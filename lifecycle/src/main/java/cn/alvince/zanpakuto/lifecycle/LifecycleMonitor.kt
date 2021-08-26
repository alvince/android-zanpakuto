package cn.alvince.zanpakuto.lifecycle

import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.lifecycle.*

interface LifecycleMonitor {

    fun monitorLifecycle(owner: LifecycleOwner)
}

interface CompoundLifecycleObserver {

    fun addObserver(observer: LifecycleObserver)
}

/**
 * An implementation of [LifecycleMonitor] for [ViewModel].
 *
 * Supported [Lifecycle] event perceived
 */
class ViewModelLifecycleMonitor(private val defaultObserver: (LifecycleOwner, Lifecycle.Event) -> Unit) :
    LifecycleMonitor {

    private var _observer: ObserverWrapper? = null

    override fun monitorLifecycle(owner: LifecycleOwner) {
        val wrapper = obtainObserver(owner)
        owner.lifecycle.addObserver(wrapper)
    }

    fun addObserver(observer: LifecycleObserver) {
        _observer?.addObserver(observer)
    }

    private fun obtainObserver(owner: LifecycleOwner): LifecycleObserver {
        val observer = _observer
        return observer ?: ObserverWrapper(owner).also {
            _observer = it
            it.addObserver(LifecycleEventObserver { source, event ->
                defaultObserver(source, event)
                if (event == Lifecycle.Event.ON_DESTROY) {
                    _observer = null
                }
            })
        }
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
class ObserverWrapper(private val owner: LifecycleOwner) : LifecycleEventObserver,
    CompoundLifecycleObserver {

    private val observers = mutableListOf<LifecycleObserver>()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        // first dispatch all
        dispatchStateChanged(source, event)
        // while destroy: detach and clear all observers
        if (event == Lifecycle.Event.ON_DESTROY && source == owner) {
            detachObserver()
        }
    }

    @MainThread
    fun detachObserver() {
        owner.lifecycle.removeObserver(this)
        observers.clear()
    }

    override fun addObserver(observer: LifecycleObserver) {
        synchronized(this) {
            if (observers.contains(observer)) {
                return@synchronized
            }
            observers.add(observer)
        }
    }

    private fun dispatchStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        observers.takeIf { it.isNotEmpty() }
            ?.toTypedArray()
            ?.forEach { observer ->
                observer.dispatchEvent(source, event)
            }
    }

    private fun LifecycleObserver.dispatchEvent(source: LifecycleOwner, event: Lifecycle.Event) {
        if (this is LifecycleEventObserver) {
            this.onStateChanged(source, event)
            return
        }
        if (this is DefaultLifecycleObserver) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> this.onCreate(source)
                Lifecycle.Event.ON_START -> this.onStart(source)
                Lifecycle.Event.ON_RESUME -> this.onResume(source)
                Lifecycle.Event.ON_PAUSE -> this.onPause(source)
                Lifecycle.Event.ON_STOP -> this.onStop(source)
                Lifecycle.Event.ON_DESTROY -> this.onDestroy(source)
                else -> {
                    // no-op
                }
            }
        }
    }
}
