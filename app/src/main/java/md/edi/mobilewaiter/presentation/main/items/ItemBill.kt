package md.edi.mobilewaiter.presentation.main.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.data.remote.response.bills.BillItem
import md.edi.mobilewaiter.databinding.ItemBillBinding
import java.text.DecimalFormat

data class ItemBill(
    val tag: String,
    val bill: BillItem
) : Item

class ItemBillBinder(val item: ItemBill) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemBillBinder) {
            payloads.apply {
                if (item.bill.SumAfterDiscount != other.item.bill.SumAfterDiscount)
                    add(Payloads.OnSumChanged(other.item.bill.Number, other.item.bill.SumAfterDiscount))
                if (item.bill.TableUid != other.item.bill.TableUid)
                    add(Payloads.OnTableChanged(other.item.bill.TableUid))
                if (item.bill.Number != other.item.bill.Number)
                    add(Payloads.OnNumberChanged(other.item.bill.Number))
                if (item.bill.Guests != other.item.bill.Guests)
                    add(Payloads.OnGuestChanged(other.item.bill.Guests))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnSumChanged(val number: Int, val sum: Double) : Payloads()
        data class OnTableChanged(val tableId: String) : Payloads()
        data class OnNumberChanged(val number: Int) : Payloads()
        data class OnGuestChanged(val guests: Int) : Payloads()
    }
}

class ItemBillDelegate(
    private val onItemClick: (item: BillItem) -> Unit,
    private val onLongItemClick: (item: BillItem) -> Unit
) :
    DelegateBinder<ItemBillBinder, ItemBillDelegate.ItemBillViewHolder>(
        ItemBillBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemBillBinding.inflate(inflater, parent, false)

        return ItemBillViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemBillBinder,
        viewHolder: ItemBillViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemBillBinder.Payloads.OnSumChanged -> {
                        viewHolder.loadSum(it.sum)
                    }
                    is ItemBillBinder.Payloads.OnTableChanged -> {
                        viewHolder.loadTable(it.tableId)
                    }
                    is ItemBillBinder.Payloads.OnNumberChanged -> {
                        viewHolder.loadNumber(it.number)
                    }
                    is ItemBillBinder.Payloads.OnGuestChanged -> {
                        viewHolder.loadGuests(it.guests)
                    }
                }
            }
            viewHolder.setClicks(model.item)
            viewHolder.setOnLongClickListener(model.item)
        }
    }

    inner class ItemBillViewHolder(
        private val binding: ItemBillBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemBill) {
            loadNumber(item.bill.Number)
            loadSum(item.bill.SumAfterDiscount)
            loadTable(item.bill.TableUid)
            loadGuests(item.bill.Guests)

            setClicks(item)
            setOnLongClickListener(item)
        }

        fun loadTable(tableUid: String) {
            val tableName = AssortmentController.getTableNumberById(tableUid)
            binding.textBillNumber.text = tableName
        }

        fun loadSum(sumAfterDiscount: Double) {
            if(sumAfterDiscount == 0.0){
                binding.textBillSum.text = "0 MDL"
            }
            else{
                binding.textBillSum.text = DecimalFormat(".0#").format(sumAfterDiscount) + " MDL"
            }
        }

        fun loadNumber(num: Int) {
            binding.textTable.text = num.toString()
        }

        fun loadGuests(num: Int) {
            binding.textCountPerson.text = num.toString()
        }

        fun setClicks(item: ItemBill) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.bill)
            }
        }

        fun setOnLongClickListener(item: ItemBill) {
            binding.root.setOnLongClickListener {
                onLongItemClick.invoke(item.bill)
                return@setOnLongClickListener true
            }
        }
    }
}
