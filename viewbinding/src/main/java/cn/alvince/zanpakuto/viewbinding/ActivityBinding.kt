package cn.alvince.zanpakuto.viewbinding

import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

class ActivityViewBindingHolder<T : ViewBinding> : ActivityViewBinding<T> {

    override val binding: T? get() = _bindingHolder.binding

    private val _bindingHolder = IViewBindingHolder.Holder<T>()

    override fun requireBinding(): T = _bindingHolder.requireBinding()

    override fun clearBinding(clear: T.() -> Unit) {
        _bindingHolder.clearBinding(clear)
    }

    override fun ComponentActivity.inflate(inflate: () -> T, onClear: ((T) -> Unit)?, init: ((T) -> Unit)?): T {
        return inflate()
            .also {
                setContentView(it.root)
                handleBinding(this, it, onClear, init)
            }
    }

    override fun ComponentActivity.inflate(bindingClass: Class<T>, init: (T) -> Unit) {
        try {
            bindingClass.getDeclaredMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as T
        } catch (ex: NoSuchMethodException) {
            Log.e(TAG, "Fail to inflate binding<${bindingClass::javaClass.name}> in ${this::javaClass.name}", ex)
            null
        }?.also {
            setContentView(it.root)
            handleBinding(this, it, null, init)
        }
    }

    private fun handleBinding(lifecycleOwner: LifecycleOwner, binding: T, onClear: ((T) -> Unit)?, init: ((T) -> Unit)?) {
        _bindingHolder.bind(binding)
        init?.invoke(binding)
        ObserverWrapper(lifecycleOwner) {
            clearBinding { onClear?.invoke(this) }
        }
            .attach()
    }
}

/**
 * Creates the [IViewBindingHolder] for [Fragment]s.
 *
 * Example for use:
 * ```
 * class MyActivity : ComponentActivity(), ActivityViewBinding<MyActivityBinding> by cn.alvince.zanpakuto.viewbinding.ActivityBinding() {
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         …
 *         // replace setContentView(), and hold binding instance
 *         inflate(
 *             inflate = { MyActivityBinding.inflate(layoutInflater) },
 *             /* option: */onClear = { it.onClear() },
 *         ) { binding ->
 *             // init with binding
 *             …
 *         }
 *         …
 *     }
 *
 *     // Optional: perform clear binding
 *     private fun MyActivityBinding.onClear() {
 *         …
 *     }
 *
 *     …
 * }
 * ```
 */
@Suppress("FunctionName") // delegate ActivityViewBindingHolder implementation
inline fun <reified T : ViewBinding> ActivityBinding(): ActivityViewBinding<T> = ActivityViewBindingHolder()
