package cn.alvince.zanpakuto.lifecycle

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.getTagX
import androidx.lifecycle.setTagIfAbsentX
import androidx.savedstate.SavedStateRegistryOwner
import java.io.Closeable
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

private const val INTERNAL_LIFECYCLE_OWNER_STORE_KEY = "view_model_lifecycle_owner"
private const val INTERNAL_TAG_FROM_LIFECYCLE_FACTORY = "tag_view_model_from_lifecycle_factory"

val ViewModel.viewModelLifecycle: Lifecycle get() = this.asLifecycleOwner().lifecycle

@Throws(IllegalStateException::class)
fun ViewModel.asLifecycleOwner(): ViewModelLifecycleOwner {
    check(getTagX<Boolean>(INTERNAL_TAG_FROM_LIFECYCLE_FACTORY) == true) {
        "Invalid: require create by `by lifecycleViewModels()`, or override getDefaultViewModelProviderFactory() with returns LifecycleViewModelFactories::fromXxx()"
    }
    return acquireLifecycleOwner().also {
        check(it.isAttached) { "Invalid: must called after instantiation complete." }
    }
}

/**
 * Returns a property delegate to access [ViewModel] by **default** scoped to this [Fragment]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewModel: MyViewModel by lifecycleViewModels()
 * }
 * ```
 */
@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.lifecycleViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: { defaultViewModelProviderFactory }
    return AutoLifecycleViewModelLazy(VM::class, { viewModelStore }, factoryPromise, { this }, { !isChangingConfigurations })
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.lifecycleViewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer, null, null)

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityLifecycleViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = createViewModelLazy(
    VM::class,
    { requireActivity().viewModelStore },
    factoryProducer ?: { requireActivity().defaultViewModelProviderFactory },
    { requireActivity() },
    { activity?.isChangingConfigurations != true },
)

@MainThread
fun <VM : ViewModel> Fragment.createViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null,
    lifecycleOwnerProducer: (() -> LifecycleOwner)? = null,
    hostDestroyPredicate: (() -> Boolean)? = null,
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: { defaultViewModelProviderFactory }
    val lifecycleOwnerPromise = lifecycleOwnerProducer ?: { this }
    return AutoLifecycleViewModelLazy(viewModelClass, { storeProducer() }, factoryPromise, lifecycleOwnerPromise, hostDestroyPredicate)
}

/**
 * Customized `lazy` delegate for [ViewModel] with lifecycle
 */
class AutoLifecycleViewModelLazy<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory,
    private val lifecycleOwnerProducer: () -> LifecycleOwner,
    private val hostDestroyPredicate: (() -> Boolean)?,
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val realFactory =
                    if (factory is LifecycleViewModelFactory) factory else LifecycleViewModelFactory(factory, lifecycleOwnerProducer, hostDestroyPredicate)
                realFactory.afterCreate = { onCreate(it) }
                ViewModelProvider(storeProducer(), realFactory).get(viewModelClass.java).also {
                    cached = it
                    it.acquireLifecycleOwner().attachToHost(lifecycleOwnerProducer(), hostDestroyPredicate ?: { true })
                }
            } else {
                viewModel
            }
        }

    final override fun isInitialized(): Boolean = cached != null

    @CallSuper
    protected open fun onCreate(viewModel: ViewModel) {
    }
}

object LifecycleViewModelFactories {
    fun fromActivity(activity: ComponentActivity, delegateFactory: ViewModelProvider.Factory?): ViewModelProvider.Factory {
        return getLifecycleViewModelFactory(
            activity.application,
            activity,
            activity,
            activity.intent?.extras,
            delegateFactory ?: activity.defaultViewModelProviderFactory
        ) {
            !activity.isChangingConfigurations
        }
    }

    fun fromFragment(fragment: Fragment, delegateFactory: ViewModelProvider.Factory?): ViewModelProvider.Factory {
        return getLifecycleViewModelFactory(
            fragment.requireActivity().application,
            fragment,
            fragment,
            fragment.arguments,
            delegateFactory ?: fragment.defaultViewModelProviderFactory,
        ) {
            true
        }
    }

    private fun getLifecycleViewModelFactory(
        application: Application,
        owner: SavedStateRegistryOwner,
        lifecycleOwner: LifecycleOwner,
        defaultArgs: Bundle?,
        extensionDelegate: ViewModelProvider.Factory?,
        destroyPredicate: () -> Boolean,
    ): ViewModelProvider.Factory {
        val delegate = extensionDelegate ?: SavedStateViewModelFactory(application, owner, defaultArgs)
        return LifecycleViewModelFactory(delegate, { lifecycleOwner }, destroyPredicate)
    }
}

/**
 * Extensional [LifecycleOwner] for [ViewModel]
 */
class ViewModelLifecycleOwner : LifecycleOwner, Closeable {

    internal val isAttached: Boolean get() = _attachedToHost

    private val lifecycleRegistry = LifecycleRegistry(this)

    private var _attachedToHost = false
    private var _hostLifecycleOwnerRef: WeakReference<LifecycleOwner>? = null

    private var hostDestroyDetermination: (() -> Boolean)? = null

    private val _hostLifecycleObserver = LifecycleEventObserver { source, event ->
        if (_hostLifecycleOwnerRef?.get() == source) {
            handleLifecycleEvent(event, false)
        }
    }

    init {
        // init with CREATED state
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun close() {
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY, true)
    }

    /**
     * Attach to host lifecycle
     *
     * @param hostLifecycleOwner host [LifecycleOwner]
     */
    fun attachToHost(hostLifecycleOwner: LifecycleOwner, destroyDetermination: () -> Boolean) {
        if (_hostLifecycleOwnerRef?.get() == hostLifecycleOwner) {
            return
        }
        hostDestroyDetermination = destroyDetermination
        _hostLifecycleOwnerRef?.get()?.lifecycle?.removeObserver(_hostLifecycleObserver)
        _hostLifecycleOwnerRef = WeakReference(hostLifecycleOwner)
        hostLifecycleOwner.lifecycle.addObserver(_hostLifecycleObserver)
        _attachedToHost = true
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event, forceUpdate: Boolean) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            if (hostDestroyDetermination?.invoke() != false || forceUpdate) {
                lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            }
            hostDestroyDetermination = null
            return
        }
        lifecycleRegistry.takeIf { it.currentState > Lifecycle.State.DESTROYED }?.handleLifecycleEvent(event)
    }
}

private class LifecycleViewModelFactory(
    private val delegateFactory: ViewModelProvider.Factory,
    private val lifecycleOwnerProducer: () -> LifecycleOwner,
    private val destroyPredicate: (() -> Boolean)?,
) : ViewModelProvider.Factory {

    var afterCreate: ((ViewModel) -> Unit)? = null

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val parentLifecycleOwner = lifecycleOwnerProducer()
        return delegateFactory.create(modelClass)
            .apply {
                setTagIfAbsentX(INTERNAL_TAG_FROM_LIFECYCLE_FACTORY, true)
                acquireLifecycleOwner().also { lifecycleOwner ->
                    lifecycleOwner.attachToHost(parentLifecycleOwner) { destroyPredicate?.invoke() != false }
                    /* hook viewModel.viewModelScope, use lifecycle.coroutineScope instead
                     * lifecycle.coroutineScope 是 lifecycle-runtime-ktx 的 internal class LifecycleCoroutineScopeImpl, 会进行类型校验无法替换
                     */
                    hookViewModelScope(lifecycleOwner.lifecycle)
                }
                afterCreate?.invoke(this)
            }
    }
}

private fun ViewModel.acquireLifecycleOwner(): ViewModelLifecycleOwner {
    return getTagX<ViewModelLifecycleOwner>(INTERNAL_LIFECYCLE_OWNER_STORE_KEY) ?: ViewModelLifecycleOwner().also {
        setTagIfAbsentX(INTERNAL_LIFECYCLE_OWNER_STORE_KEY, it)
    }
}

/**
 * @see androidx.lifecycle.JOB_KEY
 */
private fun ViewModel.hookViewModelScope(lifecycle: Lifecycle) {
    // back up exist ::viewModelScope, use another tag
    getTagX<Closeable>("androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY")?.also { exist ->
        setTagIfAbsentX("androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY.bak", exist)
    }
    // instead with lifecycle::coroutineScope
    setTagIfAbsentX("androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY", lifecycle.coroutineScope)
}
