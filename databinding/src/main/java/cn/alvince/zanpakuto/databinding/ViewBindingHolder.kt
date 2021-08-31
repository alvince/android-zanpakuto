package cn.alvince.zanpakuto.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

interface IViewBindingHolder<T : ViewDataBinding> {

    val binding: T?

    fun clearBinding(clear: T.() -> Unit = {})

    @Throws(IllegalStateException::class)
    fun requireBinding(): T

    class Holder<VB : ViewDataBinding> : IViewBindingHolder<VB> {

        override val binding: VB? get() = _binding

        private var _binding: VB? = null
        private var _inflated: Boolean = false
        private var _cleared: Boolean = false

        override fun clearBinding(clear: VB.() -> Unit) {
            binding?.apply(clear)
            _cleared = true
            _binding = null
        }

        override fun requireBinding(): VB {
            if (!_inflated) {
                throw IllegalStateException("No binding inflated.")
            }
            if (_cleared) {
                throw IllegalStateException("Binding instance cleared.")
            }
            return _binding!!
        }

        @Throws(IllegalStateException::class)
        @MainThread
        internal fun inflate(binding: VB) {
            if (_inflated) {
                throw IllegalStateException("ViewDataBinding already inflated.")
            }
            _binding = binding
            _inflated = true
        }
    }
}

interface ActivityBindingHolder<T : ViewDataBinding> : IViewBindingHolder<T> {

    fun inflateBinding(activity: ComponentActivity, init: (binding: T) -> Unit)

    fun ComponentActivity.inflate(onClear: ((binding: T) -> Unit)? = null, init: (binding: T) -> Unit)
}

interface FragmentBindingHolder<T : ViewDataBinding> : IViewBindingHolder<T> {

    fun inflateBinding(inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean, block: (binding: T) -> Unit): View

    fun Fragment.inflate(
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean = false,
        onClear: ((binding: T) -> Unit)? = null,
        init: (binding: T) -> Unit
    ): View
}
