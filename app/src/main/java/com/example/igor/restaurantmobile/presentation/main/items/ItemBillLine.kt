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
import java.text.DecimalFormat

data class ItemBillLine(
    val tag: String,
    val line: LineItem
) : Item

class ItemBillLineBinder(val item: ItemBillLine) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemBillLineBinder) {
            payloads.apply {
                if (item.line.QueueNumber != other.item.line.QueueNumber)
                    add(Payloads.OnNumberChanged(other.item.line.QueueNumber))
                if (item.line.AssortimentUid != other.item.line.AssortimentUid)
                    add(Payloads.OnNameChanged(other.item.line.AssortimentUid))
                if (item.line.Count != other.item.line.Count)
                    add(Payloads.OnCountChanged(other.item.line.Count))
                if (item.line.Sum != other.item.line.Sum)
                    add(Payloads.OnSumChanged(other.item.line.Sum))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnNumberChanged(val number: Int) : Payloads()
        data class OnNameChanged(val name: String) : Payloads()
        data class OnCountChanged(val count: Double) : Payloads()
        data class OnSumChanged(val sum: Double) : Payloads()
    }
}

class ItemBillLineDelegate(
    private val onItemClick: (item: LineItem) -> Unit
) :
    DelegateBinder<ItemBillLineBinder, ItemBillLineDelegate.ItemBillLineViewHolder>(
        ItemBillLineBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemBillLineBinding.inflate(inflater, parent, false)

        return ItemBillLineViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemBillLineBinder,
        viewHolder: ItemBillLineViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemBillLineBinder.Payloads.OnNumberChanged -> {
                        viewHolder.loadNumber(it.number)
                    }
                    is ItemBillLineBinder.Payloads.OnNameChanged -> {
                        viewHolder.loadName(it.name)
                    }
                    is ItemBillLineBinder.Payloads.OnCountChanged -> {
                        viewHolder.loadCount(it.count)
                    }
                    is ItemBillLineBinder.Payloads.OnSumChanged -> {
                        viewHolder.loadSum(it.sum)
                    }
                }
                viewHolder.setClicks(model.item.line)
            }
        }
    }

    inner class ItemBillLineViewHolder(
        private val binding: ItemBillLineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemBillLine) {
            loadName(item.line.AssortimentUid)
            loadNumber(item.line.QueueNumber)
            loadCount(item.line.Count)
            loadSum(item.line.Sum)
            loadStatus("Undefined")

            setClicks(item.line)
        }

        fun loadNumber(number: Int) {
            binding.textLineNumber.text = number.toString()
        }

        fun loadName(id: String) {
            val tableName = AssortmentController.getAssortmentNameById(id)
            binding.textLineName.text = tableName
        }

        fun loadCount(count: Double) {
            if(count == 0.0){
                binding.textLineCount.text = "0"
            }
            else{
                binding.textLineCount.text = DecimalFormat(".0#").format(count)
            }
        }

        fun loadSum(sum: Double) {
            if(sum == 0.0){
                binding.textLineSum.text = "0 MDL"
            }
            else{
                binding.textLineSum.text = DecimalFormat(".0#").format(sum) + " MDL"
            }
        }

        fun loadStatus(status: String) {
            binding.textLineStatus.text = status
        }

        fun setClicks(item: LineItem) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }
}
