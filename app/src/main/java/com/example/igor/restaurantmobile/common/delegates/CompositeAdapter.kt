package ro.lensa.common.recycler_view.delegates

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItemDiffCallback
import com.example.igor.restaurantmobile.common.delegates.DelegateBinder

@Suppress("UNCHECKED_CAST")
class CompositeAdapter(
    val delegates: SparseArray<DelegateBinder<DelegateAdapterItem, RecyclerView.ViewHolder>>
) : ListAdapter<DelegateAdapterItem, RecyclerView.ViewHolder>(DelegateAdapterItemDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        for (i in 0 until delegates.size()) {
            if (delegates[i].modelClass == getItem(position).javaClass) {
                return delegates.keyAt(i)
            }
        }
        throw NullPointerException("Can not get viewType for position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return delegates[viewType].createViewHolder(layoutInflater, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        onBindViewHolder(holder, position, mutableListOf())

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val delegateAdapter = delegates[getItemViewType(position)]
        println("onBindViewHolder payloads " + payloads)
        if (delegateAdapter != null) {
            val delegatePayloads = mutableListOf<DelegateAdapterItem.Payloadable>()
            payloads.forEach {
                if(it is List<*>)
                    delegatePayloads.addAll(it as List<DelegateAdapterItem.Payloadable>)
            }
            delegateAdapter.bindViewHolder(getItem(position), holder, delegatePayloads)
        } else {
            throw NullPointerException("can not find adapter for position $position")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        delegates[holder.itemViewType].onViewRecycled(holder)
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        delegates[holder.itemViewType].onViewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        delegates[holder.itemViewType].onViewAttachedToWindow(holder)
        super.onViewAttachedToWindow(holder)
    }

    class Builder {
        private var count: Int = 0
        private val delegates: SparseArray<DelegateBinder<DelegateAdapterItem, RecyclerView.ViewHolder>> =
            SparseArray()

        fun add(delegateBinder: DelegateBinder<out DelegateAdapterItem, *>): Builder {
            delegates.put(
                count++,
                delegateBinder as DelegateBinder<DelegateAdapterItem, RecyclerView.ViewHolder>
            )
            return this
        }

        fun build(): CompositeAdapter {
            require(count != 0) { "Register at least one adapter" }
            return CompositeAdapter(delegates)
        }
    }

    fun removeItem(position: Int) {
        val items = mutableListOf<DelegateAdapterItem?>()
        items.addAll(currentList)
        if (items[position] != null) {
            items.removeAt(position)
            submitList(items)
        }
    }

    fun removeItem(item: DelegateAdapterItem) {
        val items = mutableListOf<DelegateAdapterItem>()
        items.addAll(currentList)
        if (items.contains(item)) {
            items.remove(item)
            submitList(items)
        }
    }

    fun addItem(item: DelegateAdapterItem, position: Int = 0) {
        val items = mutableListOf<DelegateAdapterItem>()
        items.addAll(currentList)
        if (position >= currentList.size) {
            items.add(item)
        } else
            items.add(position, item)
        submitList(items)
    }
}

@Suppress("UNCHECKED_CAST")
class  CompositeAdapterNoDiff(
    private val delegates: SparseArray<DelegateBinder<DelegateAdapterItem, RecyclerView.ViewHolder>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items = mutableListOf<DelegateAdapterItem>()

    var selectedUnicItems = mutableListOf<Any>()

    override fun getItemViewType(position: Int): Int {
        for (i in 0 until delegates.size()) {
            if (delegates[i].modelClass == items[position].javaClass) {
                return delegates.keyAt(i)
            }
        }
        throw NullPointerException("Can not get viewType for position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return delegates[viewType].createViewHolder(layoutInflater, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        onBindViewHolder(holder, position, mutableListOf())

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val delegateAdapter = delegates[getItemViewType(position)]
        println("onBindViewHolder payloads " + payloads)
        if (delegateAdapter != null) {
            val delegatePayloads = mutableListOf<DelegateAdapterItem.Payloadable>()
            payloads.forEach {
                if(it is List<*>)
                    delegatePayloads.addAll(it as List<DelegateAdapterItem.Payloadable>)
            }
            delegateAdapter.bindViewHolder(items[position], holder, delegatePayloads)
        } else {
            throw NullPointerException("can not find adapter for position $position")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        delegates[holder.itemViewType].onViewRecycled(holder)
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        delegates[holder.itemViewType].onViewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        delegates[holder.itemViewType].onViewAttachedToWindow(holder)
        super.onViewAttachedToWindow(holder)
    }

    override fun getItemCount(): Int = items.size

    class Builder {
        private var count: Int = 0
        private val delegates: SparseArray<DelegateBinder<DelegateAdapterItem, RecyclerView.ViewHolder>> =
            SparseArray()

        fun add(delegateBinder: DelegateBinder<out DelegateAdapterItem, *>): Builder {
            delegates.put(
                count++,
                delegateBinder as DelegateBinder<DelegateAdapterItem, RecyclerView.ViewHolder>
            )
            return this
        }

        fun build(): CompositeAdapterNoDiff {
            require(count != 0) { "Register at least one adapter" }
            return CompositeAdapterNoDiff(delegates)
        }
    }

    fun submitList(newList: List<DelegateAdapterItem>){
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = items[oldItemPosition]
                val newItem = newList[newItemPosition]
                return oldItem::class == newItem::class && oldItem.id() == newItem.id()
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = items[oldItemPosition]
                val newItem = newList[newItemPosition]
                return oldItem.content == newItem.content
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = items[oldItemPosition]
                val newItem = newList[newItemPosition]
                return oldItem.payload(newItem)
            }
        })
        diff.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newList)
    }

    fun clearSelections(){
        selectedUnicItems.clear()
    }
}