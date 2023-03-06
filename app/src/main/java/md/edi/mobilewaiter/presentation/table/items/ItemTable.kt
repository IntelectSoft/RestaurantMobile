package md.edi.mobilewaiter.presentation.table.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.databinding.ItemTableBinding
import md.edi.mobilewaiter.utils.HelperFormatter
import java.text.DecimalFormat

data class ItemTable(
    val tag: String,
    val id: String,
    val name: String,
    val sum: Double,
    val guests: Int,
    val isOccupied: Boolean
) : Item

class ItemTableBinder(val item: ItemTable) : DelegateAdapterItem(item) {
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

class ItemTableDelegate(private val onItemClick: (id: String, name: String, guest: Int, sum: Double,isOccupied: Boolean) -> Unit) :
    DelegateBinder<ItemTableBinder, ItemTableDelegate.ItemTableViewHolder>(
        ItemTableBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemTableBinding.inflate(inflater, parent, false)
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
            loadGuests(item.guests)
            loadSum(item.sum)
            loadSetImageState(item)

            setClicks(item)
        }

        fun loadName(name: String) {
            binding.tableList.text = name
        }

        fun loadGuests(guests: Int) {
            binding.textTableGuests.text = guests.toString()
        }

        fun loadSum(sum: Double) {
            binding.textTableSum.text = HelperFormatter.formatDouble(sum, true)
        }

        fun loadSetImageState(item: ItemTable) {
            if (!item.isOccupied) {
                binding.imageStateTable.setColorFilter(
                    ContextCompat.getColor(
                        binding.imageStateTable.context,
                        R.color.colorPrimary
                    )
                )
            } else {
                binding.imageStateTable.setColorFilter(
                    ContextCompat.getColor(
                        binding.imageStateTable.context,
                        R.color.red
                    )
                )
            }
        }


        fun setClicks(item: ItemTable) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.id, item.name, item.guests, item.sum, item.isOccupied)
            }
        }
    }
}
