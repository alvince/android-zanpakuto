/*
 * Created by alvince on 2022/1/21
 *
 * @author alvincezhang@didiglobal.com
 */

package cn.alvince.zanpakuto.view.recyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

val RecyclerView.ViewHolder.context: Context get() = this.itemView.context

interface IBindable<T> {
    fun bind(data: T, payloads: MutableList<Any>?)
}

abstract class AbsViewHolder<DATA>(itemView: View) : RecyclerView.ViewHolder(itemView), IBindable<DATA> {
    open fun bindNone() {}
}

@DslMarker
annotation class ViewHolderDslMarker

@ViewHolderDslMarker
class ViewHolderMaker<T> {

    private var viewSupplier: (() -> View)? = null
    private var bindNoneHook: (() -> Unit)? = null

    fun contentView(supplier: () -> View) {
        this.viewSupplier = supplier
    }

    fun onBindNone(bindNone: () -> Unit) {
        this.bindNoneHook = bindNone
    }

    fun make(): RecyclerView.ViewHolder {
        val viewProvider = requireNotNull(viewSupplier) { "Require contentView called." }
        val bindNoneCallback = bindNoneHook
        return object : AbsViewHolder<T>(viewProvider()) {

            override fun bind(data: T, payloads: MutableList<Any>?) {
                TODO("Not yet implemented")
            }

            override fun bindNone() {
                bindNoneCallback?.invoke()
            }
        }
    }
}
