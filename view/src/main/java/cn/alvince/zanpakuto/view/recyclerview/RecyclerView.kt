package cn.alvince.zanpakuto.view.recyclerview

import android.content.Context
import androidx.annotation.MainThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbsListRecyclerAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected val source: MutableList<T> = ArrayList()

    override fun getItemCount(): Int {
        val override = overrideItemCount()
        if (override != -1) {
            return override
        }
        return source.size
    }

    protected fun overrideItemCount(): Int = -1

    @MainThread
    fun updateWithDiff(list: List<T>, diffCallback: DiffUtil.ItemCallback<T>) {
        diffCallback.calculateDiff(source.toList(), list)
            .also { result ->
                clear()
                source += list
                result.dispatchUpdatesTo(this)
            }
    }

    open fun clear() {
        source.clear()
    }
}

val RecyclerView.ViewHolder.context: Context get() = this.itemView.context
