package cn.alvince.zanpakuto.lifecycle

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlin.reflect.KClass

/**
 * An implementation of [ViewModel] with [Lifecycle] event perceived
 */
open class LifecycleViewModel : ViewModel(), LifecycleMonitor, CompoundLifecycleObserver, LifecycleLiveStateOwner by SimpleLifecycleLiveStateOwner() {

    private val lifecycleMonitor = ViewModelLifecycleMonitor { source, event ->
        onStateChanged(source, event)
        onLifecycleStateChanged(source, event)
    }

    final override fun monitorLifecycle(owner: LifecycleOwner) {
        lifecycleMonitor.monitorLifecycle(owner)
        onMonitorLifecycle(owner)
    }

    final override fun addObserver(observer: LifecycleObserver) {
        lifecycleMonitor.addObserver(observer)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    protected open fun onMonitorLifecycle(owner: LifecycleOwner) {
    }

    protected open fun onLifecycleStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    }
}

/**
 * An implementation of [AndroidViewModel] with [Lifecycle] event perceived
 */
open class LifecycleAppViewModel(application: Application) : AndroidViewModel(application),
    LifecycleMonitor, CompoundLifecycleObserver,
    LifecycleLiveStateOwner by SimpleLifecycleLiveStateOwner() {

    private val lifecycleMonitor = ViewModelLifecycleMonitor { source, event ->
        onStateChanged(source, event)
        onLifecycleStateChanged(source, event)
    }

    final override fun monitorLifecycle(owner: LifecycleOwner) {
        lifecycleMonitor.monitorLifecycle(owner)
        onMonitorLifecycle(owner)
    }

    final override fun addObserver(observer: LifecycleObserver) {
        lifecycleMonitor.addObserver(observer)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    protected open fun onMonitorLifecycle(owner: LifecycleOwner) {
    }

    protected open fun onLifecycleStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    }
}

/**
 * An implementation of [Lazy] used by [androidx.fragment.app.Fragment.viewModels] and
 * [androidx.activity.ComponentActivity.viewmodels].
 *
 * [storeProducer] is a lambda that will be called during initialization, [VM] will be created
 * in the scope of returned [ViewModelStore].
 *
 * [lifecycleOwnerProducer] is a lambda that will be called during initialization,
 * returned [LifecycleOwner] will be used for [VM] subscribe [Lifecycle] state change
 *
 * [factoryProducer] is a lambda that will be called during initialization,
 * returned [ViewModelProvider.Factory] will be used for creation of [VM]
 */
class LifecycleViewModelLazy<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val lifecycleOwnerProducer: () -> LifecycleOwner,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : Lazy<VM> {
    private var cached: VM? = null

    private val actor = ViewModelActor()

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                val lifecycleOwner = lifecycleOwnerProducer()

                ViewModelProvider(store, factory)
                    .get(viewModelClass.java)
                    .also {
                        cached = it
                        actor.applyLifecycle(it, lifecycleOwner)
                    }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}

inline fun <reified VM : LifecycleViewModel> ComponentActivity.viewModel(
    factory: ViewModelProvider.Factory = ViewModelProviders.newInstanceFactory()
): LifecycleViewModelLazy<VM> =
    LifecycleViewModelLazy(VM::class, { viewModelStore }, { this }, { factory })

inline fun <reified VM : LifecycleViewModel> Fragment.viewModel(
    factory: ViewModelProvider.Factory = ViewModelProviders.newInstanceFactory()
): LifecycleViewModelLazy<VM> =
    LifecycleViewModelLazy(VM::class, { viewModelStore }, { this }, { factory })

inline fun <reified VM : LifecycleAppViewModel> ComponentActivity.appViewModel(
    factory: ViewModelProvider.Factory = ViewModelProviders.appInjectFactory()
): LifecycleViewModelLazy<VM> =
    LifecycleViewModelLazy(VM::class, { viewModelStore }, { this }, { factory })

inline fun <reified VM : LifecycleAppViewModel> Fragment.appViewModel(
    factory: ViewModelProvider.Factory = ViewModelProviders.appInjectFactory()
): LifecycleViewModelLazy<VM> =
    LifecycleViewModelLazy(VM::class, { viewModelStore }, { this }, { factory })
