package com.example.igor.restaurantmobile.common.itemButton

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.delegates.DelegateBinder
import com.example.igor.restaurantmobile.common.delegates.Item
import com.example.igor.restaurantmobile.common.styles.*
import com.example.igor.restaurantmobile.databinding.ItemButtonBinding


data class ItemButton(
    val tag: String,
    val text: Text,
    @DrawableRes val buttonBackground: Int? = null,
    @DrawableRes val iconLeft: Int? = null,
    @DrawableRes val iconCenter: Int? = null,
    val textColor: TextColor,
    val margin: IMargin = IMargin.None,
    val isSelected: Boolean? = false,
    val isActive: Boolean? = false
) : Item


class ItemButtonBinder(val item: ItemButton) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemButtonBinder) {
            payloads.apply {
                if (item.text != other.item.text)
                    add(ButtonPayload.ButtonTextChanged(other.item.text))
                if (item.buttonBackground != other.item.buttonBackground)
                    add(ButtonPayload.BackgroundChanged(other.item.buttonBackground))
                if (item.textColor != other.item.textColor)
                    add(ButtonPayload.TextColorChanged(other.item.textColor))
                if (item.iconLeft != other.item.iconLeft)
                    add(ButtonPayload.IconLeftChanged(other.item.iconLeft))
                if (item.iconCenter != other.item.iconCenter)
                    add(ButtonPayload.IconCenterChanged(other.item.iconCenter))
                if (item.margin != other.item.margin)
                    add(ButtonPayload.MarginChanged(other.item.margin))
            }
        }
        return payloads
    }

    sealed class ButtonPayload : Payloadable {
        class ButtonTextChanged(var text: Text) : ButtonPayload()
        class BackgroundChanged(@DrawableRes val buttonBackground: Int?) : ButtonPayload()
        class TextColorChanged(val textColor: TextColor) : ButtonPayload()
        class IconLeftChanged(@DrawableRes val iconLeft: Int?) : ButtonPayload()
        class IconCenterChanged(@DrawableRes val iconRight: Int?) : ButtonPayload()
        data class MarginChanged(val margin: IMargin): ButtonPayload()
    }
}


class ItemButtonDelegate(
    private val onButtonClick: (item: ItemButton) -> Unit,
    private val onHandleBottom: ((isShow: Boolean) -> Unit)? = null
) :
    DelegateBinder<ItemButtonBinder, ItemButtonDelegate.ButtonViewHolder>(ItemButtonBinder::class.java) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return ButtonViewHolder(
            ItemButtonBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(
        model: ItemButtonBinder,
        viewHolder: ButtonViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isNullOrEmpty())
            viewHolder.bind(model.item)
        else
            payloads.forEach {
                when (it) {
                    is ItemButtonBinder.ButtonPayload.ButtonTextChanged -> viewHolder.setButtonText(
                        it.text
                    )
                    is ItemButtonBinder.ButtonPayload.BackgroundChanged -> viewHolder.setBackground(
                        it.buttonBackground
                    )
                    is ItemButtonBinder.ButtonPayload.TextColorChanged -> viewHolder.setTextColor(it.textColor)
                    is ItemButtonBinder.ButtonPayload.IconLeftChanged -> viewHolder.setIconLeft(it.iconLeft)
                    is ItemButtonBinder.ButtonPayload.IconCenterChanged -> viewHolder.setIconCenter(
                        it.iconRight
                    )
                    is ItemButtonBinder.ButtonPayload.MarginChanged -> viewHolder.setMargin(it.margin)
                }
            }
    }

    override fun onViewDetachedFromWindow(viewHolder: ButtonViewHolder) {
        super.onViewDetachedFromWindow(viewHolder)
        onHandleBottom?.invoke(true)
    }

    override fun onViewAttachedToWindow(viewHolder: ButtonViewHolder) {
        super.onViewAttachedToWindow(viewHolder)
        onHandleBottom?.invoke(false)
    }

    inner class ButtonViewHolder(
        private val binding: ItemButtonBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemButton) {
            binding.button.setOnClickListener {
                onButtonClick.invoke(item)
            }
            setIconLeft(item.iconLeft)
            setIconCenter(item.iconCenter)
            setButtonText(item.text)
            setBackground(item.buttonBackground)
            setTextColor(item.textColor)
            setMargin(item.margin)
        }

        fun setMargin(margin: IMargin) {
            binding.button.setMargin(margin)
        }

        fun setButtonText(text: Text) {
            binding.buttonLabel.text = text.getStringText(itemView.context)
        }

        fun setBackground(@DrawableRes buttonBackground: Int?) {
            if (buttonBackground != null) {
                binding.button.setBackgroundResource(buttonBackground)
            }
        }

        fun setTextColor(textColor: TextColor) {
            binding.buttonLabel.setTextColor(textColor, itemView.context)
        }

        fun setIconLeft(@DrawableRes iconLeft: Int?) {
            val icon = if (iconLeft != null)
                AppCompatResources.getDrawable(itemView.context, iconLeft)
            else
                null
            binding.iconLeft.setImageDrawable(icon)
        }

        fun setIconCenter(@DrawableRes iconLeft: Int?) {
            val icon = if (iconLeft != null)
                AppCompatResources.getDrawable(itemView.context, iconLeft)
            else
                null
            binding.iconCenter.setImageDrawable(icon)
        }
    }
}


