package md.edi.mobilewaiter.presentation.assortment.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.data.remote.response.assortment.AssortmentItem
import md.edi.mobilewaiter.databinding.ItemAssortmentBinding
import java.text.DecimalFormat

data class ItemAssortment(
    val tag: String,
    val assortment: AssortmentItem,
) : Item

class ItemAssortmentBinder(val item: ItemAssortment) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemAssortmentBinder) {
            payloads.apply {
                if (item.assortment.Name != other.item.assortment.Name)
                    add(Payloads.OnNameChanged(other.item.assortment.Name))
                if (item.assortment.Price != other.item.assortment.Price)
                    add(Payloads.OnPriceChanged(other.item.assortment.Price))
                if (item.assortment != other.item.assortment)
                    add(Payloads.OnAssortmentChanged(other.item.assortment))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnNameChanged(val name: String) : Payloads()
        data class OnPriceChanged(val price: Double) : Payloads()
        data class OnAssortmentChanged( val item: AssortmentItem) : Payloads()
    }
}

class ItemAssortmentDelegate(private val onItemClick: (item: AssortmentItem) -> Unit) :
    DelegateBinder<ItemAssortmentBinder, ItemAssortmentDelegate.ItemAssortmentViewHolder>(
        ItemAssortmentBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemAssortmentBinding.inflate(inflater, parent, false)
//        val padding = parent.context.resources.getDimensionPixelSize(R.dimen._16sdp)
//        //val height: Int = (parent.measuredHeight - padding) / 3
//        val height = (parent.measuredWidth - padding) / 2
//        val layoutParams: ViewGroup.LayoutParams = view.promotionCard.layoutParams
//        layoutParams.height = height
//        layoutParams.width = height
//        view.promotionCard.layoutParams = layoutParams
        return ItemAssortmentViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemAssortmentBinder,
        viewHolder: ItemAssortmentViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemAssortmentBinder.Payloads.OnNameChanged -> {
                        viewHolder.loadName(it.name)
                    }
                    is ItemAssortmentBinder.Payloads.OnPriceChanged -> {
                        viewHolder.loadPrice(it.price)
                    }
                    is ItemAssortmentBinder.Payloads.OnAssortmentChanged ->{
                        viewHolder.bind(model.item)
                    }
                }
            }
            viewHolder.setClicks(model.item.assortment)
        }
    }

    inner class ItemAssortmentViewHolder(
        private val binding: ItemAssortmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemAssortment) {
            loadName(item.assortment.Name)

            if (item.assortment.IsFolder) {
                binding.priceAssortment.isVisible = false
            }else {
                binding.priceAssortment.isVisible = true
                loadPrice(item.assortment.Price)
            }
            loadImage(item.assortment)

            setClicks(item.assortment)
        }

        fun loadName(name: String) {
            binding.nameAssortment.text = name
        }

        fun loadPrice(price: Double) {
            if(price == 0.0){
                binding.priceAssortment.text = "0"
            }
            else{
                binding.priceAssortment.text = DecimalFormat(".0#").format(price)
            }
        }

        fun loadImage(item: AssortmentItem){
            if(!item.IsFolder)
                binding.imageAssortment.setImageResource(0)
            else{
                binding.imageAssortment.setImageResource(R.drawable.icon_folder)
            }
        }

        fun setClicks(item: AssortmentItem) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }
}
