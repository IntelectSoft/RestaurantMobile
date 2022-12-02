package com.example.igor.restaurantmobile.presentation.main.split.items

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.delegates.DelegateBinder
import com.example.igor.restaurantmobile.common.delegates.Item
import com.example.igor.restaurantmobile.databinding.ItemLineSplitBinding
import com.example.igor.restaurantmobile.databinding.ItemNewOrderLineBinding
import java.text.DecimalFormat

data class ItemLinesSplit(
    val tag: String,
    var line: LineItemModel,
    val name: String,
    val allowNonInteger: Boolean,
    val price: Double
) : Item

class ItemLinesSplitBinder (val item: ItemLinesSplit) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemLinesSplitBinder) {
            payloads.apply {
                if (item.line.Count != other.item.line.Count)
                    add(Payloads.OnCountChanged(other.item, other.item.line.Count))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnCountChanged(val item: ItemLinesSplit, val count: Double?) : Payloads()
    }
}

class ItemLinesSplitDelegate(private val onItemClick: (item: LineItemModel) -> Unit) :
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
                }
            }
            viewHolder.setClicks(model.item.line)
        }
    }

    inner class ItemLinesSplitViewHolder(
        private val binding: ItemLineSplitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemLinesSplit) {
            loadName(item.name)
            loadCount(item, item.line.Count)
            setClicks(item.line)
        }

        fun loadCount(item: ItemLinesSplit, count: Double) {
            if (item.allowNonInteger){
                binding.editTextCountSelected.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                binding.editTextCountSelected.setText(DecimalFormat(".0#").format(count))
            }
            else{
                binding.editTextCountSelected.inputType = InputType.TYPE_CLASS_NUMBER
                binding.editTextCountSelected.setText(count.toInt().toString())
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
                        Log.e("TAG", "on count split Changed: $selectedValue")
                        if(selectedValue > item.line.Count){
                            binding.editTextCountSelected.error = "Introduceti cantitate mai mica!"
                        }
                        else{
                            item.line.Count = selectedValue
                            item.line.Sum = item.price * selectedValue
                            item.line.SumAfterDiscount = item.price * selectedValue
                            onItemClick.invoke(item.line)
                        }
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })

        }

        fun loadName(name: String) {
            binding.textNameAssortment.text = name
        }

        fun setClicks(item: LineItemModel) {
            binding.checkBoxSelected.setOnCheckedChangeListener { compoundButton, b ->
                item.isChecked = b
                onItemClick.invoke(item)
            }
        }

    }


}
