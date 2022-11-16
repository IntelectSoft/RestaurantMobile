package com.example.igor.restaurantmobile.presentation.table.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.delegates.DelegateBinder
import com.example.igor.restaurantmobile.common.delegates.Item
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.databinding.ItemBillBinding
import com.example.igor.restaurantmobile.databinding.ItemTableBinding

data class ItemTable(
    val tag: String,
    val id: String,
    val name: String
) : Item

class ItemTableBinder (val item: ItemTable) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemTableBinder) {
            payloads.apply {
                if (item.name != other.item.name)
                    add(Payloads.OnNameChanged(other.item.name))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnNameChanged(val name: String) : Payloads()
    }
}

class ItemTableDelegate(private val onItemClick: (item: String) -> Unit) :
    DelegateBinder<ItemTableBinder, ItemTableDelegate.ItemTableViewHolder>(
        ItemTableBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemTableBinding.inflate(inflater, parent, false)
//        val padding = parent.context.resources.getDimensionPixelSize(R.dimen._16sdp)
//        //val height: Int = (parent.measuredHeight - padding) / 3
//        val height = (parent.measuredWidth - padding) / 2
//        val layoutParams: ViewGroup.LayoutParams = view.promotionCard.layoutParams
//        layoutParams.height = height
//        layoutParams.width = height
//        view.promotionCard.layoutParams = layoutParams
        return ItemTableViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemTableBinder,
        viewHolder: ItemTableViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemTableBinder.Payloads.OnNameChanged -> {
                        viewHolder.loadName(it.name)
                    }
                }
            }
            viewHolder.setClicks(model.item)
        }
    }

    inner class ItemTableViewHolder(
        private val binding: ItemTableBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemTable) {
            loadName(item.name)

            setClicks(item)
        }

        fun loadName(name: String) {
            binding.tableList.text = name
        }

        fun setClicks(item: ItemTable) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.id)
            }
        }
    }
}
