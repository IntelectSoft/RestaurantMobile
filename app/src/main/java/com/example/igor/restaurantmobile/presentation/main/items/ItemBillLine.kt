package com.example.igor.restaurantmobile.presentation.main.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.delegates.DelegateBinder
import com.example.igor.restaurantmobile.common.delegates.Item
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.data.remote.response.bills.LineItem
import com.example.igor.restaurantmobile.databinding.ItemBillBinding
import com.example.igor.restaurantmobile.databinding.ItemBillLineBinding

data class ItemBillLine(
    val tag: String,
    val line: LineItem
) : Item

class ItemBillLineBinder(val item: ItemBillLine) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemBillBinder) {
            payloads.apply {
                if (item.line.Sum != other.item.bill.Sum)
                    add(Payloads.OnSumChanged(other.item.bill.Number, other.item.bill.Sum))

            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnSumChanged(val number: Int, val sum: Double) : Payloads()
    }
}

class ItemBillLineDelegate(
    private val onItemClick: (item: BillItem) -> Unit
) :
    DelegateBinder<ItemBillLineBinder, ItemBillLineDelegate.ItemBillViewHolder>(
        ItemBillLineBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemBillLineBinding.inflate(inflater, parent, false)

        return ItemBillViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemBillLineBinder,
        viewHolder: ItemBillViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
//                    is ItemBillBinder.Payloads.OnSumChanged -> {
//                        viewHolder.loadSum(it.number, it.sum)
//                    }
//                    is ItemBillBinder.Payloads.OnTableChanged -> {
//                        viewHolder.loadTable(it.tableId)
//                    }
                }
            }
        }
    }

    inner class ItemBillViewHolder(
        private val binding: ItemBillLineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemBillLine) {
            loadName(item.line.AssortimentUid)
        }

        private fun loadName(id: String) {
            val tableName = AssortmentController.getAssortmentNameById(id)
            binding.text.text = tableName
        }

        fun setClicks(item: ItemBill) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.bill)
            }
        }
    }
}
