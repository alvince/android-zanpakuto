package cn.alvince.zanpakuto.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.alvince.zanpakuto.viewbinding.IViewBindingHolder

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

internal class ViewDataBindingHolder<VB : ViewDataBinding> : IViewBindingHolder.Holder<VB>() {

    override fun clearBinding(clear: VB.() -> Unit) {
        super.clearBinding {
            clear()
            unbind()
        }
    }
}
