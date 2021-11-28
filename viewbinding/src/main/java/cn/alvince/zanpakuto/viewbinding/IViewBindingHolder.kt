package cn.alvince.zanpakuto.viewbinding

import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

interface IViewBindingHolder<T : ViewBinding> {

    val binding: T?

    @Throws(IllegalStateException::class)
    fun requireBinding(): T

    fun clearBinding(clear: T.() -> Unit = {})

    open class Holder<VB : ViewBinding> : IViewBindingHolder<VB> {

        override val binding: VB? get() = _binding

        private var _binding: VB? = null
        private var _inflated: Boolean = false
        private var _cleared: Boolean = false

        override fun clearBinding(clear: VB.() -> Unit) {
            binding?.apply { clear() }
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

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @Throws(IllegalStateException::class)
        @MainThread
        fun bind(binding: VB) {
            if (_inflated) {
                throw IllegalStateException("ViewDataBinding already inflated.")
            }
            _binding = binding
            _inflated = true
        }
    }
}

interface ActivityViewBinding<T : ViewBinding> : IViewBindingHolder<T> {

    fun ComponentActivity.inflate(inflate: () -> T, onClear: ((T) -> Unit)? = null, init: ((T) -> Unit)? = null): T

    fun ComponentActivity.inflate(bindingClass: Class<T>, init: (T) -> Unit)
}

interface FragmentViewBinding<T : ViewBinding> : IViewBindingHolder<T> {

    fun Fragment.inflate(inflate: () -> T, onClear: ((binding: T) -> Unit)? = null, init: (binding: T) -> Unit): View
}
