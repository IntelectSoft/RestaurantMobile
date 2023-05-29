package md.edi.mobilewaiter.presentation.main.items

import android.view.InputQueue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.data.remote.models.bill.OrderItem
import md.edi.mobilewaiter.data.remote.response.bills.LineItem
import md.edi.mobilewaiter.databinding.ItemBillLineBinding
import md.edi.mobilewaiter.databinding.ItemKitLineBinding
import md.edi.mobilewaiter.utils.HelperFormatter
import java.text.DecimalFormat

data class ItemKitLine(
    val tag: String,
    val assortmentId: String,
    val count: Double,
    val price: Double,
    val queueNumber: Int,
    val isLast: Boolean
) : Item

class ItemKitLineBinder(val item: ItemKitLine) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemKitLineBinder) {
            payloads.apply {
                if (item.queueNumber != other.item.queueNumber)
                    add(Payloads.OnNumberChanged(other.item.queueNumber))
                if (item.assortmentId != other.item.assortmentId)
                    add(Payloads.OnNameChanged(other.item.assortmentId))
                if (item.count != other.item.count)
                    add(Payloads.OnCountChanged(other.item.count))
                if (item.price != other.item.price)
                    add(Payloads.OnSumChanged(other.item.price))
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
    private val onItemClick: (item: Any) -> Unit
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
                viewHolder.setClicks(model.item)
            }
        }
    }

    inner class ItemKitLineViewHolder(
        private val binding: ItemKitLineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemKitLine) {
            loadName(item.assortmentId)
            loadCount(item.count)
            loadSum(item.price)
            if (item.isLast) {
                binding.divider11.isVisible = false
            }
        }

        fun loadSum(sum: Double) {
            binding.textLineSum.text = HelperFormatter.formatDouble(sum, false)
        }

        fun loadName(id: String) {
            var tableName = AssortmentController.getAssortmentNameById(id)
            if(tableName == "Not found"){
                tableName = AssortmentController.getCommentById(id)?.Comment ?: "Not found"
            }
            binding.textNameAssortment.text = tableName
        }

        fun loadCount(count: Double) {
            binding.textCount.text = HelperFormatter.formatDouble(count, false)
        }

        fun setClicks(item: ItemKitLine) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.assortmentId)
            }
        }
    }
}
