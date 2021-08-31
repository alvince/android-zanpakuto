package cn.alvince.zanpakuto.databinding

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * [androidx.databinding.ViewDataBinding] lifecycle observer wrapper
 */
class ObserverWrapper(private val owner: LifecycleOwner, private var onClear: (() -> Unit)?) : LifecycleEventObserver {

    private var attached: Boolean = false

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source == owner && event == Lifecycle.Event.ON_DESTROY) {
            detach()
        }
    }

    fun attach() {
        if (attached) {
            return
        }
        if (owner.lifecycle.currentState <= Lifecycle.State.STARTED) {
            owner.lifecycle.addObserver(this)
            attached = true
        }
    }

    private fun detach() {
        owner.lifecycle.removeObserver(this)
        onClear?.invoke()
        onClear = null
    }
}
