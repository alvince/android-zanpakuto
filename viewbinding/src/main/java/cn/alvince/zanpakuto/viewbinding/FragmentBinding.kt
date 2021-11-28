package cn.alvince.zanpakuto.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

class FragmentViewBindingHolder<T : ViewBinding> : FragmentViewBinding<T> {

    override val binding: T? get() = _bindingHolder.binding

    private val _bindingHolder = IViewBindingHolder.Holder<T>()

    override fun requireBinding(): T = _bindingHolder.requireBinding()

    override fun clearBinding(clear: T.() -> Unit) {
        _bindingHolder.clearBinding(clear)
    }

    override fun Fragment.inflate(inflate: () -> T, onClear: ((binding: T) -> Unit)?, init: (binding: T) -> Unit): View =
        inflate().also {
            _bindingHolder.bind(it)
            init(it)
            ObserverWrapper(viewLifecycleOwner) {
                clearBinding { onClear?.invoke(this) }
            }
                .attach()
        }
            .root
}

/**
 * Creates the [IViewBindingHolder] for [Fragment]s.
 *
 * Example for use:
 * ```
 * class MyFragment : Fragment(), FragmentViewBinding<MyFragmentBinding> by cn.alvince.zanpakuto.viewbinding.FragmentBinding() {
 *
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
 *         inflate(
 *             inflate = { MyFragmentBinding.inflate(inflater, container, false) },
 *             /* option: */onClear = { it.onClear() },
 *         ) {
 *             // init binding, views and states here
 *         }
 *
 *     // Optional: perform clear binding
 *     private fun MyFragmentBinding.onClear() {
 *         …
 *     }
 *
 *     …
 * }
 * ```
 */
@Suppress("FunctionName") // delegate FragmentViewBindingHolder implementation
inline fun <reified T : ViewBinding> FragmentBinding(): FragmentViewBinding<T> = FragmentViewBindingHolder()
