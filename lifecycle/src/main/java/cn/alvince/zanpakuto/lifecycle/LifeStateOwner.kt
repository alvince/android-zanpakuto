package cn.alvince.zanpakuto.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

interface LiveStateOwner {
    /**
     * Indicate that host is active
     */
    val isActive: Boolean

    /**
     * Indicate that host is living or disposed
     */
    val isLiving: Boolean
}

interface LifecycleLiveStateOwner : LiveStateOwner, LifecycleEventObserver

class SimpleLifecycleLiveStateOwner : LifecycleLiveStateOwner {

    override val isActive: Boolean get() = activeState

    override val isLiving: Boolean get() = liveState

    private var activeState: Boolean = false
    private var liveState: Boolean = false

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> liveState = true
            Lifecycle.Event.ON_START -> activeState = true
            Lifecycle.Event.ON_STOP -> activeState = false
            Lifecycle.Event.ON_DESTROY -> liveState = false
            else -> {
            }
        }
    }
}
