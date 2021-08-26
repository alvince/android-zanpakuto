package cn.alvince.zanpakuto.lifecycle.binding

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cn.alvince.zanpakuto.lifecycle.LifecycleScope

/**
 * [androidx.lifecycle.Lifecycle] binding registry
 *
 * Created by alvince on 2021/4/15
 *
 * @author alvince.zy@gmail.com
 */
interface LifecycleBindingRegistry<T> {

    val attached: Boolean

    @MainThread
    fun attachToLifecycle(owner: LifecycleOwner)

    @MainThread
    fun bindToLifecycle(entry: T, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY)
}

/**
 * Composite [androidx.lifecycle.Lifecycle] binding registry
 */
interface CompositeLifecycleRegistry<T> : LifecycleBindingRegistry<T> {

    fun bindLifecycleScoped(scope: LifecycleScope, entry: T, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY)
}
