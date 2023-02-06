package md.edi.mobilewaiter.presentation.notifications.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.delegates.DelegateBinder
import md.edi.mobilewaiter.common.delegates.Item
import md.edi.mobilewaiter.data.database.models.NotificationModel
import md.edi.mobilewaiter.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

data class ItemNotification(
    val tag: String,
    val notification: NotificationModel
) : Item

class ItemNotificationBinder(val item: ItemNotification) : DelegateAdapterItem(item) {
    override fun id(): Any = item.tag

    override fun payload(other: DelegateAdapterItem): List<Payloadable> {
        val payloads = mutableListOf<Payloadable>()
        if (other is ItemNotificationBinder) {
            payloads.apply {
                if (item.notification.body != other.item.notification.body)
                    add(Payloads.OnBodyChanged(other.item.notification.body))
                if (item.notification.title != other.item.notification.title)
                    add(Payloads.OnTitleChanged(other.item.notification.title))
            }
        }
        return payloads
    }

    sealed class Payloads : Payloadable {
        data class OnBodyChanged(val body: String) : Payloads()
        data class OnTitleChanged(val title: String) : Payloads()
    }
}

class ItemNotificationDelegate(
    private val onItemClick: (item: NotificationModel) -> Unit,
) :
    DelegateBinder<ItemNotificationBinder, ItemNotificationDelegate.ItemNotificationViewHolder>(
        ItemNotificationBinder::class.java
    ) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {

        val view = ItemNotificationBinding.inflate(inflater, parent, false)

        return ItemNotificationViewHolder(view)
    }

    override fun bindViewHolder(
        model: ItemNotificationBinder,
        viewHolder: ItemNotificationViewHolder,
        payloads: List<DelegateAdapterItem.Payloadable>
    ) {
        if (payloads.isEmpty())
            viewHolder.bind(model.item)
        else {
            payloads.forEach {
                when (it) {
                    is ItemNotificationBinder.Payloads.OnBodyChanged -> {
                        viewHolder.loadBody(it.body)
                    }
                    is ItemNotificationBinder.Payloads.OnTitleChanged -> {
                        viewHolder.loadTitle(it.title)
                    }
                }
            }
            viewHolder.setClicks(model.item)
        }
    }

    inner class ItemNotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemNotification) {
            loadTitle(item.notification.title)
            loadBody(item.notification.body)
            loadDate(item.notification.tag)
            loadViewed(item.notification.viewed)
            loadViewedDate(item.notification.viewedTime)

            setClicks(item)
        }

        fun loadTitle(title: String) {
            binding.textNotifyTitle.text = title
        }

        fun loadBody(body: String) {
            binding.textNotifyBody.text = body
        }

        private fun loadDate(date: String) {
            binding.textNotifyDate.text = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(Date(date.toLong()))
        }

        private fun loadViewed(isViewed: Int) {
            binding.imageNotifyState.setImageResource(if (isViewed == 0) R.drawable.icon_viewed else R.drawable.icon_not_viewed)
        }

        private fun loadViewedDate(date: String) {
            if(date.isNotBlank()){
                binding.textNotifyViewedDate.text = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(Date(date.toLong()))
            }
        }

        fun setClicks(item: ItemNotification) {
            binding.root.setOnClickListener {
                onItemClick.invoke(item.notification)
            }
        }
    }
}
