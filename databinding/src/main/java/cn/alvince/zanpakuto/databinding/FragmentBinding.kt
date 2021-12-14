package cn.alvince.zanpakuto.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.alvince.zanpakuto.viewbinding.IViewBindingHolder
import cn.alvince.zanpakuto.viewbinding.ObserverWrapper

class FragmentViewDataBindingHolder<T : ViewDataBinding>(@LayoutRes private val layoutRes: Int) : FragmentBindingHolder<T> {

    override val binding: T? get() = _bindingHolder.binding

    private val _bindingHolder = ViewDataBindingHolder<T>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean,
        block: (binding: T) -> Unit
    ): View {
        return DataBindingUtil.inflate<T>(inflater, layoutRes, root, attachToRoot)
            .also {
                _bindingHolder.bind(it)
                block(it)
            }
            .root
    }

    override fun clearBinding(clear: T.() -> Unit) {
        _bindingHolder.clearBinding(clear)
    }

    override fun requireBinding(): T = _bindingHolder.requireBinding()

    override fun Fragment.inflate(
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean,
        onClear: ((binding: T) -> Unit)?,
        init: (binding: T) -> Unit,
    ): View =
        inflateBinding(inflater, root, attachToRoot) {
            init(it)
            ObserverWrapper(viewLifecycleOwner) {
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
 * class MyFragment : Fragment(), FragmentBindingHolder<MyFragmentBinding> by FragmentBindingHolder(R.layout.my_fragment) {
 *
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
 *         return inflate(inflater, container, false, /* option: */onClear = { it.onClear() }) { binding ->
 *             // init with binding
 *             …
 *         }
 *     }
 *
 *     // Optional: perform clear binding
 *     private fun MyFragmentBinding.onClear() {
 *         …
 *     }
 * }
 * ```
 */
@Suppress("FunctionName") // delegate FragmentBindingHolder create
inline fun <reified T : ViewDataBinding> FragmentBinding(@LayoutRes layoutRes: Int): FragmentBindingHolder<T> {
    return FragmentViewDataBindingHolder(layoutRes)
}
