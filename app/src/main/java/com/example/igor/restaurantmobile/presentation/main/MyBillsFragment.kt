package com.example.igor.restaurantmobile.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.data.listeners.ActionOnBillListener
import com.example.igor.restaurantmobile.databinding.FragmentMyBillsBinding
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillDelegate
import com.example.igor.restaurantmobile.presentation.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.example.igor.restaurantmobile.common.decor.GridSpanDecoration
import com.example.igor.restaurantmobile.common.delegates.CompositeAdapter

@AndroidEntryPoint
class MyBillsFragment : Fragment(), ActionOnBillListener {

    val mainViewModel by viewModels<MainViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    private var spanCount = 2

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(
                ItemBillDelegate { bill ->
                    val dialog = BillDetailsDialogFragment.newInstance(billId = bill.Uid)
                    dialog.setListener(this)
                    dialog.show(
                        parentFragmentManager,
                        BillDetailsDialogFragment::class.simpleName
                    )
                }
            )
            .build()
    }

    val binding by lazy {
        FragmentMyBillsBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launchWhenResumed {
            mainViewModel.getMyBills()
            mainViewModel.billListResponse.collectLatest {
                initList(it)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            billFlow.distinctUntilChanged().collectLatest {
                Log.e("TAG", "distinctUntilChanged: $it ")
                val billFind = BillsController.getBillById(it)
                if (billFind != null) {
                    val dialog = BillDetailsDialogFragment.newInstance(billId = billFind.Uid)
                    dialog.setListener(this@MyBillsFragment)
                    dialog.show(parentFragmentManager, BillDetailsDialogFragment::class.simpleName)
                }
                else{
                    Toast.makeText(requireContext(), "Contul nu a fost gasit!", Toast.LENGTH_SHORT).show()
                }
            }
        }


        initToolbar()

        return binding.root
    }

    companion object {
        val billFlow = MutableSharedFlow<String>()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        toolbar.setRightIcon(R.drawable.ic_notification_bell) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToFragmentNotification()
            )
        }

        toolbar.setSecondRightIcon(R.drawable.ic_plus) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToTableList()
            )
        }

        toolbar.setTitle("Conturile mele")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftIcon(R.drawable.ic_settings) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToFragmentSettings()
            )
        }
    }

    private fun notifyAdapter(delegateAdapterItems: List<DelegateAdapterItem>) {
        compositeAdapter.submitList(delegateAdapterItems)
        lifecycleScope.launch(Dispatchers.Main) {
            delay(250)
            binding.recycler.smoothScrollToPosition(0)
        }

    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.recycler.adapter = compositeAdapter

        layoutManager = GridLayoutManager(context, 3)
        binding.recycler.layoutManager = layoutManager

        binding.recycler.smoothScrollToPosition(compositeAdapter.itemCount)


        val deco = GridSpanDecoration(
            binding.root.context.resources.getDimensionPixelSize(
                com.intuit.sdp.R.dimen._4sdp
            ),
            true
        )

        while (binding.recycler.itemDecorationCount > 0) {
            binding.recycler.removeItemDecorationAt(0)
        }

        binding.recycler.addItemDecoration(deco)

        compositeAdapter.submitList(items)
    }

    private fun selectSpanCount(spanCount: Int, init: Boolean) {
        if (spanCount != this.spanCount || init) {
            layoutManager.spanCount = spanCount
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == compositeAdapter.itemCount) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
            binding.recycler.adapter?.let {
                it.notifyItemRangeChanged(0, it.itemCount)
            }
            this.spanCount = spanCount
        }
    }

    override fun onCloseBill(dialogs: BillDetailsDialogFragment) {
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.getMyBills()
            mainViewModel.billListResponse.collectLatest {
                notifyAdapter(it)
            }
        }
    }
}