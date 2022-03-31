/*
 * Created by alvince on 2022/1/21
 *
 * @author alvincezhang@didiglobal.com
 */

package cn.alvince.zanpakuto.view.recyclerview

import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbsRecyclerListAdapter<DATA, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected val source: List<DATA> get() = _source

    private var _source: MutableList<DATA> = mutableListOf()

    final override fun onBindViewHolder(holder: VH, position: Int) {
        itemAt(position).also { onBindViewHolder(holder, position, it) }
            ?: holder.takeIf { it is AbsViewHolder<*> }.also {
                (it as AbsViewHolder<*>).bindNone()
            }
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int = _source.size

    @Synchronized
    fun modifySource(block: MutableList<DATA>.() -> Unit) {
        _source.apply(block)
    }

    @MainThread
    fun updateWithDiff(list: List<DATA>, diffCallback: DiffUtil.ItemCallback<DATA>) {
        diffCallback.calculateDiff(source.toList(), list)
            .also { result ->
                clear()
                _source += list
                result.dispatchUpdatesTo(this)
            }
    }

    fun clear() {
        _source.clear()
    }

    protected abstract fun onBindViewHolder(holder: VH, position: Int, item: DATA?)

    protected open fun itemAt(position: Int): DATA? {
        return _source.getOrNull(position)
    }
}

@DslMarker
annotation class AdapterDslMarker

@AdapterDslMarker
class RecyclerAdapterMaker<DATA, VH : RecyclerView.ViewHolder> {

    private var onCreateViewHolderHook: ((parent: ViewGroup, viewType: Int) -> VH)? = null
    private var onBindViewHolderHook: ((holder: VH, position: Int, item: DATA?) -> Unit)? = null
    private var getItemCountHook: ((source: List<DATA>) -> Int)? = null
    private var getItemIdHook: ((position: Int, item: DATA?) -> Long)? = null
    private var getItemViewTypeHook: ((position: Int, item: DATA?) -> Int)? = null
    private var getItemHook: ((position: Int, source: List<DATA>) -> DATA?)? = null

    fun onCreateViewHolder(onCreate: (parent: ViewGroup, viewType: Int) -> VH) {
        this.onCreateViewHolderHook = onCreate
    }

    fun onBindViewHolder(onBind: (holder: VH, position: Int, item: DATA?) -> Unit) {
        this.onBindViewHolderHook = onBind
    }

    fun getItemViewType(getItemViewType: (position: Int, item: DATA?) -> Int) {
        this.getItemViewTypeHook = getItemViewType
    }

    fun getItemId(getItemId: (position: Int, item: DATA?) -> Long) {
        this.getItemIdHook = getItemId
    }

    fun getItem(getItem: (position: Int, source: List<DATA>) -> DATA) {
        this.getItemHook = getItem
    }

    internal fun make(): AbsRecyclerListAdapter<DATA, VH> {
        val createViewHolderCallback = requireNotNull(onCreateViewHolderHook) { "Require onCreateViewHolder called." }
        val bindViewHolderCallback = requireNotNull(onBindViewHolderHook) { "Require onBindViewHolder called." }
        val getItemCountCallback = getItemCountHook
        val getItemViewTypeCallback = getItemViewTypeHook
        val getItemCallback = getItemHook
        val getItemIdCallback = getItemIdHook
        return object : AbsRecyclerListAdapter<DATA, VH>() {

            init {
                getItemIdCallback?.also { setHasStableIds(true) }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return createViewHolderCallback(parent, viewType)
            }

            override fun onBindViewHolder(holder: VH, position: Int, item: DATA?) {
                bindViewHolderCallback(holder, position, item)
            }

            override fun getItemId(position: Int): Long = getItemIdCallback?.invoke(position, itemAt(position)) ?: super.getItemId(position)

            override fun getItemCount(): Int = getItemCountCallback?.invoke(source) ?: super.getItemCount()

            override fun getItemViewType(position: Int): Int = getItemViewTypeCallback?.invoke(position, itemAt(position)) ?: super.getItemViewType(position)

            override fun itemAt(position: Int): DATA? = getItemCallback?.invoke(position, source) ?: super.itemAt(position)
        }
    }
}
