package cn.alvince.zanpakuto.rxjava2.internal

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Created by alvince on 2021/7/6
 *
 * @author alvince.zy@gmail.com
 */
internal class DestroyedLifecycleOwner : LifecycleOwner {

    private var _lifecycleRegistry: LifecycleRegistry? = null

    override fun getLifecycle(): Lifecycle {
        initialize()
        return _lifecycleRegistry!!
    }

    private fun initialize() {
        if (_lifecycleRegistry == null) {
            _lifecycleRegistry = LifecycleRegistry(this).apply { currentState = Lifecycle.State.DESTROYED }
        }
    }
}
