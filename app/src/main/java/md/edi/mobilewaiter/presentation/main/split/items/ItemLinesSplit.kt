package md.edi.mobilewaiter.presentation.main.split.items

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.databinding.ItemLineSplitBinding
import md.edi.mobilewaiter.presentation.main.split.SplitBillFragment
import md.edi.mobilewaiter.utils.ContextManager
import java.text.DecimalFormat

data class ItemLinesSplit(
    val tag: String,
    var line: LineItemModel,
    val name: String,
    val allowNonInteger: Boolean,
    val price: Double,
    var isChecked: Boolean
) : Item

class ItemLinesSplitBinder (val item: ItemLinesSplit) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemLinesSplitBinder) {
            payloads.apply {
                if (item.line.Count != other.item.line.Count)
                    add(Payloads.OnCountChanged(other.item, other.item.line.Count))
                if (item.isChecked != other.item.isChecked)
                    add(Payloads.OnCheckedChanged(other.item, other.item.isChecked))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnCountChanged(val item: ItemLinesSplit, val count: Double?) : Payloads()
        data class OnCheckedChanged(val item: ItemLinesSplit, val checkedChanged: Boolean) : Payloads()
    }
}

class ItemLinesSplitDelegate(private val onItemChecked: (item: ItemLinesSplit) -> Unit) :
    DelegateBinder<ItemLinesSplitBinder, ItemLinesSplitDelegate.ItemLinesSplitViewHolder>(
        ItemLinesSplitBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemLineSplitBinding.inflate(inflater, parent, false)

        return ItemLinesSplitViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemLinesSplitBinder,
        viewHolder: ItemLinesSplitViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemLinesSplitBinder.Payloads.OnCountChanged -> {
                        viewHolder.loadCount(it.item, it.item.line.Count)
                    }
                    is ItemLinesSplitBinder.Payloads.OnCheckedChanged -> {
                        viewHolder.setCheckedItem(it.item)
                    }
                }
            }
        }
    }

    inner class ItemLinesSplitViewHolder(
        private val binding: ItemLineSplitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemLinesSplit) {
            loadName(item.name)
            loadCount(item, item.line.Count)
            setCheckedListener(item)
            setCheckedItem(item)
        }

        fun loadCount(item: ItemLinesSplit, count: Double) {
            if (item.allowNonInteger){
                binding.editTextCountSelected.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                binding.editTextCountSelected.setText(DecimalFormat(".0#").format(count))
                binding.textCurrentCount.text = DecimalFormat(".0#").format(count) + " >"
            }
            else{
                binding.editTextCountSelected.inputType = InputType.TYPE_CLASS_NUMBER
                binding.editTextCountSelected.setText(count.toInt().toString())
                binding.textCurrentCount.text = count.toInt().toString()+ " >"
            }

            binding.editTextCountSelected.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (charSequence.isNotBlank()) {
                        val selectedValue = charSequence.toString().toDouble()
                        Log.e("TAG", "on count split Changed: $selectedValue and assortment ${AssortmentController.getAssortmentById(item.line.AssortimentUid)?.Name}")
                        if(selectedValue > item.line.Count){
                            binding.editTextCountSelected.error = "Introduceti cantitate mai mica!"
                        }
                        else{
                            item.line.Count = selectedValue
                            item.line.Sum = item.price * selectedValue
                            item.line.SumAfterDiscount = item.price * selectedValue
//                            onItemClick.invoke(item)
                        }
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })

        }

        fun loadName(name: String) {
            binding.textNameAssortment.text = name
        }

        fun setCheckedItem(item: ItemLinesSplit) {
            binding.checkBoxSelected.isChecked = item.isChecked
        }
        private fun setCheckedListener(item: ItemLinesSplit) {
            binding.checkBoxSelected.setOnCheckedChangeListener { compoundButton, b ->
                item.isChecked = b
                onItemChecked(item)
                if(!SplitBillFragment.canCheckedIt()){
                    binding.checkBoxSelected.isChecked = false
                    item.isChecked = false
                    onItemChecked(item)
                    Toast.makeText(ContextManager.retrieveContext(), "Nu puteti selecta toate pozitiile!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }


}
