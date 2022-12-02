package com.example.igor.restaurantmobile.presentation.preview_order.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.delegates.DelegateBinder
import com.example.igor.restaurantmobile.common.delegates.Item
import com.example.igor.restaurantmobile.data.remote.models.bill.OrderItem
import com.example.igor.restaurantmobile.databinding.ItemNewOrderLineBinding
import java.text.DecimalFormat

data class ItemOrder(
    val tag: String,
    val id: String,
    val line: OrderItem,
    val name: String
) : Item

class ItemOrderBinder (val item: ItemOrder) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemOrderBinder) {
            payloads.apply {
                if (item.line.count != other.item.line.count)
                    add(Payloads.OnCountChanged(other.item.line.count))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnCountChanged(val count: Double?) : Payloads()
    }
}

class ItemOrderDelegate(private val onItemClick: (item: OrderItem) -> Unit) :
    DelegateBinder<ItemOrderBinder, ItemOrderDelegate.ItemOrderViewHolder>(
        ItemOrderBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemNewOrderLineBinding.inflate(inflater, parent, false)

        return ItemOrderViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemOrderBinder,
        viewHolder: ItemOrderViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemOrderBinder.Payloads.OnCountChanged -> {
                        viewHolder.loadCount(it.count)
                    }
                }
            }
            viewHolder.setClicks(model.item)
        }
    }

    inner class ItemOrderViewHolder(
        private val binding: ItemNewOrderLineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemOrder) {
            loadName(item.name)
            loadCount(item.line.count)
            loadPrice(item.line.sumAfterDiscount)
            setClicks(item)

            if(item.line.internUid == "00000000-0000-0000-0000-000000000000"){
                itemView.isEnabled = false
            }
        }

        fun loadCount(count: Double?) {
            if(count == 0.0){
                binding.textCount.text = "0"
            }
            else{
                binding.textCount.text = DecimalFormat(".0#").format(count)
            }
        }

        fun loadName(name: String) {
            binding.textNameAssortment.text = name
        }

        fun setClicks(item: ItemOrder) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.line)
            }
        }
        fun loadPrice(sumAfterDiscount: Double) {
            if(sumAfterDiscount == 0.0){
                binding.textPrice.text = "0 MDL"
            }
            else{
                binding.textPrice.text = DecimalFormat(".0#").format(sumAfterDiscount) + " MDL"
            }
        }
    }


}
