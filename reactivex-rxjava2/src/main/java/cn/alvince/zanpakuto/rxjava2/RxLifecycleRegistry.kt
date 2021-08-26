package cn.alvince.zanpakuto.rxjava2

import android.util.Log
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cn.alvince.zanpakuto.lifecycle.LifecycleScope
import cn.alvince.zanpakuto.lifecycle.binding.CompositeLifecycleRegistry
import cn.alvince.zanpakuto.lifecycle.binding.LifecycleBindingRegistry
import io.reactivex.disposables.Disposable

/**
 * RxJava lifecycle binding registry
 *
 * Created by alvince on 2021/4/15
 *
 * @author alvince.zy@gmail.com
 */
interface RxLifecycleRegistry : LifecycleBindingRegistry<Disposable>

/**
 * RxJava lifecycle binding registry vai [androidx.fragment.app.Fragment]
 *
 * Created by alvince on 2021/4/15
 *
 * @author alvince.zy@gmail.com
 */
interface RxFragmentLifecycleRegistry : RxLifecycleRegistry, CompositeLifecycleRegistry<Disposable> {
    /**
     * Should called after [Fragment.onCreateView]
     */
    @MainThread
    fun attachToViewLifecycle(viewLifecycleOwner: LifecycleOwner)
}

sealed class AbsRxLifecycleRegistry : RxLifecycleRegistry {

    override val attached: Boolean get() = disposableRegistry != null

    private var disposableRegistry: DisposableLifecycleRegistry? = null

    @MainThread
    override fun attachToLifecycle(owner: LifecycleOwner) {
        if (disposableRegistry == null || disposableRegistry?.disposed == true) {
            disposableRegistry = DisposableLifecycleRegistry(owner)
        }
    }

    @MainThread
    override fun bindToLifecycle(entry: Disposable, event: Lifecycle.Event) {
        val registry = disposableRegistry ?: throw IllegalStateException("Must call attachToLifecycle(LifecycleOwner) first.")
        registry.bindUntil(entry, event)
    }
}

class SimpleRxLifecycleRegistry : AbsRxLifecycleRegistry()

class RxFragmentLifecycleCompositeRegistry : AbsRxLifecycleRegistry(), RxFragmentLifecycleRegistry {

    private val viewDisposableRegistry = SimpleRxLifecycleRegistry()

    @MainThread
    override fun attachToLifecycle(owner: LifecycleOwner) {
        super.attachToLifecycle(owner)
        if (owner is Fragment) {
            observeViewLifecycle(owner)
        }
    }

    @MainThread
    override fun attachToViewLifecycle(viewLifecycleOwner: LifecycleOwner) {
        if (viewDisposableRegistry.attached) {
            return
        }
        viewDisposableRegistry.attachToLifecycle(viewLifecycleOwner)
    }

    override fun bindLifecycleScoped(scope: LifecycleScope, entry: Disposable, event: Lifecycle.Event) {
        if (scope == LifecycleScope.VIEW) {
            if (viewDisposableRegistry.attached) {
                viewDisposableRegistry.bindToLifecycle(entry, event)
                return
            }
            Log.w(TAG, "View lifecycle registry not attached, bind to component lifecycle.")
        }
        super.bindToLifecycle(entry, event)
    }

    private fun observeViewLifecycle(fragment: Fragment) {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
            viewLifecycleOwner?.also { attachToViewLifecycle(it) }
        }
    }
}

/**
 * Provides [RxLifecycleRegistry] for manage [Disposable] state via [LifecycleOwner]
 *
 * For example, delegate [androidx.core.app.ComponentActivity] lifecycle:
 * ```
 * class SampleActivity : AppCompatActivity(), RxLifecycleRegistry by SimpleRxLifecycleRegistry() {
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         attachToLifecycle(this)
 *     }
 * }
 * ```
 */
@Suppress("FunctionName") // delegate RxLifecycleRegistry
fun RxLifecycleRegistry(): RxLifecycleRegistry = SimpleRxLifecycleRegistry()

/**
 * Provides [RxFragmentLifecycleRegistry] for manage [Disposable] state via [LifecycleOwner]
 *
 * ```
 * class SampleFragment : Fragment(), RxFragmentLifecycleRegistry by RxFragmentLifecycleRegistry() {
 *
 *     override fun onAttach(context: Context) {
 *         super.onAttach(context)
 *         attachToLifecycle(this)
 *     }
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         attachToViewLifecycle(viewLifecycleOwner)
 *     }
 * }
 * ```
 */
@Suppress("FunctionName") // delegate RxFragmentLifecycleRegistry
fun RxFragmentLifecycleRegistry(): RxFragmentLifecycleRegistry = RxFragmentLifecycleCompositeRegistry()
