package md.edi.mobilewaiter.presentation.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.data.database.repository.RepositoryNotification
import md.edi.mobilewaiter.databinding.FragmentNotificationsBinding
import md.edi.mobilewaiter.presentation.notifications.items.ItemNotification
import md.edi.mobilewaiter.presentation.notifications.items.ItemNotificationBinder
import md.edi.mobilewaiter.presentation.notifications.items.ItemNotificationDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.CompositeAdapter

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemNotificationDelegate { notify ->

            })

            .build()
    }

    val binding by lazy {
        FragmentNotificationsBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val notifyRepository = RepositoryNotification(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val items = notifyRepository.getAllNotification()
            val adapterItems = mutableListOf<DelegateAdapterItem>()
            items.forEach {
                adapterItems.add(
                    ItemNotificationBinder(
                        ItemNotification(
                            tag = "",
                            notification = it
                        )
                    )
                )
            }

            initList(adapterItems)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar

        toolbar.setTitle(getString(R.string.notificari))
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.viewTable.adapter = compositeAdapter
        val layoutManager = GridLayoutManager(context, 1)
        binding.viewTable.layoutManager = layoutManager
        compositeAdapter.submitList(items)
    }

}