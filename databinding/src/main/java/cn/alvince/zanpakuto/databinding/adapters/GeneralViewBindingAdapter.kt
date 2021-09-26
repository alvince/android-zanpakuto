@file:JvmName("GeneralViewBindingAdapter")

package cn.alvince.zanpakuto.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import cn.alvince.zanpakuto.view.gone
import cn.alvince.zanpakuto.view.visible

@BindingAdapter("visible")
fun View.changeVisible(visible: Boolean) {
    visible(visible)
}

@BindingAdapter("visibleOrGone")
fun View.changeVisibleOrGone(visibleOrGone: Boolean) {
    if (visibleOrGone) {
        visible(true)
    } else {
        gone()
    }
}
