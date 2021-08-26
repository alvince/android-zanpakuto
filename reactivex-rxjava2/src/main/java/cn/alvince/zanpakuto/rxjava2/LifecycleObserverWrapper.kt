package cn.alvince.zanpakuto.rxjava2

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * [androidx.lifecycle.LifecycleObserver] wrapper
 *
 * Created by alvince on 2021/4/15
 *
 * @author alvince.zy@gmail.com
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
class LifecycleObserverWrapper(
    private val owner: LifecycleOwner,
    private val onChanged: (LifecycleOwner, Lifecycle.Event) -> Unit
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source == owner && event == Lifecycle.Event.ON_DESTROY) {
            source.lifecycle.removeObserver(this)
        }
        onChanged(source, event)
    }
}
