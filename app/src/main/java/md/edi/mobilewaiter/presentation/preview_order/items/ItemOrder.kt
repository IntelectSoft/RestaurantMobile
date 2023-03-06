package md.edi.mobilewaiter.presentation.preview_order.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.data.remote.models.bill.OrderItem
import md.edi.mobilewaiter.databinding.ItemNewOrderLineBinding
import md.edi.mobilewaiter.utils.HelperFormatter
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
                if (item.line.price != other.item.line.price)
                    add(Payloads.OnPriceChanged(other.item.line.price))
                if (item.name != other.item.name)
                    add(Payloads.OnNameChanged(other.item.name))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnCountChanged(val count: Double) : Payloads()
        data class OnNameChanged(val name: String) : Payloads()
        data class OnPriceChanged(val price: Double) : Payloads()
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
                    is ItemOrderBinder.Payloads.OnNameChanged -> {
                        viewHolder.loadName(it.name)
                    }
                    is ItemOrderBinder.Payloads.OnPriceChanged -> {
                        viewHolder.loadPrice(it.price)
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

            itemView.isEnabled = item.line.internUid != "00000000-0000-0000-0000-000000000000"
        }

        fun loadCount(count: Double) {
            binding.textCount.text = HelperFormatter.formatDouble(count, false)
        }

        fun loadName(name: String) {
            binding.textNameAssortment.text = name
        }

        fun setClicks(item: ItemOrder) {
            binding.root.setOnClickListener {
                onItemClick(item.line)
            }
        }
        fun loadPrice(sumAfterDiscount: Double) {
            binding.textPrice.text = HelperFormatter.formatDouble(sumAfterDiscount, false)
        }
    }


}
