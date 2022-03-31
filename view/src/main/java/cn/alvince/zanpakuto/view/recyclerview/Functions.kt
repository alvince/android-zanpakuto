/**
 * Created by alvince on 2022/1/21
 *
 * @author alvincezhang@didiglobal.com
 */

package cn.alvince.zanpakuto.view.recyclerview

import androidx.recyclerview.widget.RecyclerView

/**
 * Config [RecyclerView.Adapter] by dsl
 * ```
 * recyclerView.listAdapter<String> {
 *     onCreateViewHolder { parent, viewType ->
 *
 *     }
 *     onBindViewHolder { holder, position, item ->
 *
 *     }
 *     /* optional */ getItemCount { source ->
 *         // override adapter items count
 *     }
 *     /* optional */ getItemViewType { position, item ->
 *         // override item view-type
 *     }
 *     /* optional */ getItem { position, source ->
 *         // override retrieve item data
 *     }
 *     /* optional */ getItemId { position, item ->
 *         // override item-id, with setHasStableIds
 *     }
 * }
 * ```
 */
fun <DATA> RecyclerView.listAdapter(supplier: RecyclerAdapterMaker<DATA, RecyclerView.ViewHolder>.() -> Unit): AbsRecyclerListAdapter<DATA, RecyclerView.ViewHolder> {
    return RecyclerAdapterMaker<DATA, RecyclerView.ViewHolder>().apply(supplier)
        .make()
        .also { this.adapter = it }
}

/**
 * Config [RecyclerView.Adapter] by dsl
 * ```
 * recyclerView.listAdapter<String, RecyclerView.ViewHolder> {
 *     onCreateViewHolder { parent, viewType ->
 *
 *     }
 *     onBindViewHolder { holder, position, item ->
 *
 *     }
 *     /* optional */ getItemCount { source ->
 *         // override adapter items count
 *     }
 *     /* optional */ getItemViewType { position, item ->
 *         // override item view-type
 *     }
 *     /* optional */ getItem { position, source ->
 *         // override retrieve item data
 *     }
 *     /* optional */ getItemId { position, item ->
 *         // override item-id, with setHasStableIds
 *     }
 * }
 * ```
 */
fun <DATA, VH : RecyclerView.ViewHolder> RecyclerView.listAdapterTyped(supplier: RecyclerAdapterMaker<DATA, VH>.() -> Unit): AbsRecyclerListAdapter<DATA, VH> {
    return RecyclerAdapterMaker<DATA, VH>().apply(supplier)
        .make()
        .also { this.adapter = it }
}

inline fun <reified T> typedViewHolder(supplier: ViewHolderMaker<T>.() -> Unit): RecyclerView.ViewHolder {
    return ViewHolderMaker<T>().apply(supplier).make()
}

fun interface IInterface {
    fun function()
}

fun simpleViewHolder(supplier: ViewHolderMaker<Any>.() -> Unit): RecyclerView.ViewHolder = typedViewHolder(supplier)
