package md.edi.mobilewaiter.presentation.settings.language.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.common.styles.Text
import md.edi.mobilewaiter.common.styles.getStringText
import md.edi.mobilewaiter.databinding.ItemRowCheckBinding

data class ItemRowCheck(
    var tag: String,
    var text: Text,
    var isChecked: Boolean = false
) : Item

data class ItemRowCheckBinder(val item: ItemRowCheck) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag
    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemRowCheckBinder) {
            payloads.apply {
                if (item.text != other.item.text) {
                    add(CheckPayloads.TextChanged(other.item.text))
                }
                if (item.isChecked != other.item.isChecked) {
                    add(CheckPayloads.ValueChanged(other.item.isChecked))
                }
            }
        }
        return payloads
    }

    sealed class CheckPayloads : Payloadable {
        data class TextChanged(val text: Text) : CheckPayloads()
        data class ValueChanged(val isCheck: Boolean) : CheckPayloads()
    }
}

class ItemRowCheckDelegate(private val onItemClick: (lang: String) -> Unit) :
    DelegateBinder<ItemRowCheckBinder, ItemRowCheckDelegate.ItemRowCheckViewHolder>(
        ItemRowCheckBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return ItemRowCheckViewHolder(ItemRowCheckBinding.inflate(inflater, parent, false))
    }

    override fun bindViewHolder(
        model: ItemRowCheckBinder,
        viewHolder: ItemRowCheckViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty()) {
            viewHolder.bind(model.item)
        } else {
            payloads.forEach {
                when (it) {
                    is ItemRowCheckBinder.CheckPayloads.TextChanged -> viewHolder.setText(it.text)
                    is ItemRowCheckBinder.CheckPayloads.ValueChanged -> viewHolder.setChecked(it.isCheck)
                }
            }
            viewHolder.setClicks(model.item)
        }
    }

    inner class ItemRowCheckViewHolder(
        private val binding: ItemRowCheckBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(item: ItemRowCheck) {
            setText(item.text)
            setChecked(item.isChecked)
            setClicks(item)
        }

        fun setClicks(item: ItemRowCheck) {
            binding.root.setOnClickListener {
                onItemClick(item.tag)
//                action(RowCheckAction.Pressed(item.tag, true))
            }
        }

        fun setText(text: Text) {
            binding.langName.text = text.getStringText(context)
        }

        fun setChecked(isEnable: Boolean) {
            binding.applied.isVisible = isEnable
        }
    }
}