package cn.alvince.zanpakuto.view.recyclerview

import androidx.recyclerview.widget.DiffUtil

@Suppress("FunctionName") // create functional DiffUtils callback
fun <T> DiffCallback(oldList: List<T>, newList: List<T>, callback: DiffUtil.ItemCallback<T>): DiffUtil.Callback {
    val oldSrc = if (oldList is MutableList) oldList.toList() else oldList
    return object : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldSrc.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldSrc.getOrNull(oldItemPosition) ?: return false
            val newItem = newList.getOrNull(newItemPosition) ?: return false
            return callback.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldSrc.getOrNull(oldItemPosition) ?: return false
            val newItem = newList.getOrNull(newItemPosition) ?: return false
            return callback.areContentsTheSame(oldItem, newItem)
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return callback.getChangePayload(oldSrc[oldItemPosition]!!, newList[newItemPosition]!!)
        }
    }
}

fun <T> DiffUtil.ItemCallback<T>.calculateDiff(oldList: List<T>, newList: List<T>): DiffUtil.DiffResult {
    return DiffUtil.calculateDiff(DiffCallback(oldList, newList, this))
}
