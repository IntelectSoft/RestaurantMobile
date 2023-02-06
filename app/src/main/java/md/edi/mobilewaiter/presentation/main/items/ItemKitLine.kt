package md.edi.mobilewaiter.presentation.main.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.data.remote.response.bills.LineItem
import md.edi.mobilewaiter.databinding.ItemBillLineBinding
import md.edi.mobilewaiter.databinding.ItemKitLineBinding
import java.text.DecimalFormat

data class ItemKitLine(
    val tag: String,
    val line: LineItem,
    val isLast: Boolean
) : Item

class ItemKitLineBinder(val item: ItemKitLine) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemKitLineBinder) {
            payloads.apply {
                if (item.line.QueueNumber != other.item.line.QueueNumber)
                    add(Payloads.OnNumberChanged(other.item.line.QueueNumber))
                if (item.line.AssortimentUid != other.item.line.AssortimentUid)
                    add(Payloads.OnNameChanged(other.item.line.AssortimentUid))
                if (item.line.Count != other.item.line.Count)
                    add(Payloads.OnCountChanged(other.item.line.Count))
                if (item.line.SumAfterDiscount != other.item.line.SumAfterDiscount)
                    add(Payloads.OnSumChanged(other.item.line.SumAfterDiscount))
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

class ItemKitLineDelegate(
    private val onItemClick: (item: LineItem) -> Unit
) :
    DelegateBinder<ItemKitLineBinder, ItemKitLineDelegate.ItemKitLineViewHolder>(
        ItemKitLineBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemKitLineBinding.inflate(inflater, parent, false)

        return ItemKitLineViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemKitLineBinder,
        viewHolder: ItemKitLineViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {

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

    inner class ItemKitLineViewHolder(
        private val binding: ItemKitLineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemKitLine) {
            loadName(item.line.AssortimentUid)
            loadCount(item.line.Count)
            loadSum(item.line.SumAfterDiscount)
            if (item.isLast) {
                binding.divider11.isVisible = false
            }
        }

        fun loadSum(sum: Double) {
            if(sum == 0.0){
                binding.textLineSum.text = "0"
            }
            else{
                binding.textLineSum.text = DecimalFormat(".0#").format(sum)
            }
        }

        fun loadName(id: String) {
            val tableName = AssortmentController.getAssortmentNameById(id)
            binding.textNameAssortment.text = tableName
        }

        fun loadCount(count: Double) {
            if (count == 0.0) {
                binding.textCount.text = "0"
            } else {
                binding.textCount.text = DecimalFormat(".0#").format(count)
            }
        }

        fun setClicks(item: LineItem) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }
}
