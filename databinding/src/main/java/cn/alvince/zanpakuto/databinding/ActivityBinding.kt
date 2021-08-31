package cn.alvince.zanpakuto.databinding

import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

class ActivityViewDataBindingHolder<T : ViewDataBinding>(@LayoutRes private val layoutRes: Int) : ActivityBindingHolder<T> {

    override val binding: T? get() = _bindingHolder.binding

    private val _bindingHolder = IViewBindingHolder.Holder<T>()

    override fun inflateBinding(activity: ComponentActivity, init: (binding: T) -> Unit) {
        DataBindingUtil.setContentView<T>(activity, layoutRes)
            .also { binding ->
                _bindingHolder.inflate(binding)
                init(binding)
            }
    }

    override fun clearBinding(clear: T.() -> Unit) {
        _bindingHolder.clearBinding(clear)
    }

    override fun requireBinding(): T = _bindingHolder.requireBinding()

    override fun ComponentActivity.inflate(onClear: ((binding: T) -> Unit)?, init: (binding: T) -> Unit) {
        inflateBinding(this, init)
        ObserverWrapper(this) {
            clearBinding {
                onClear?.invoke(this)
            }
        }
            .attach()
    }
}

/**
 * Creates the [IViewBindingHolder] for [Fragment]s.
 *
 * Example for use:
 * ```
 * class MyActivity : ComponentActivity(), ActivityBindingHolder<MyActivityBinding> by ActivityBinding(R.layout.my_fragment) {
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         …
 *         // replace setContentView(), and hold binding instance
 *         inflateBinding(/* option: */onClear = { onClear() }) { binding ->
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
 * }
 * ```
 */
@Suppress("FunctionName") // delegate ActivityBindingHolder implements
inline fun <reified T : ViewDataBinding> ActivityBinding(@LayoutRes layoutRes: Int): ActivityBindingHolder<T> {
    return ActivityViewDataBindingHolder(layoutRes)
}
