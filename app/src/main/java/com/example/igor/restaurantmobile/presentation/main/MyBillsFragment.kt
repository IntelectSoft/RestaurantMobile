package com.example.igor.restaurantmobile.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.itemButton.ItemButton
import com.example.igor.restaurantmobile.common.itemButton.ItemButtonBinder
import com.example.igor.restaurantmobile.common.itemButton.ItemButtonDelegate
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.databinding.FragmentMyBillsBinding
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillDelegate
import com.example.igor.restaurantmobile.presentation.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import ro.lensa.common.decor.GridSpanDecoration
import ro.lensa.common.recycler_view.delegates.CompositeAdapter

@AndroidEntryPoint
class MyBillsFragment : Fragment() {

    val mainViewModel by viewModels<MainViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    private var spanCount = 2

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(
                ItemBillDelegate(
                    { bill ->
                        BillDetailsDialogFragment.newInstance(billId = bill.Uid)
                            .show(parentFragmentManager, "")
                    }, { longItemBillClick ->
                        BillActionDialogFragment.newInstance(billId = longItemBillClick.Uid)
                            .show(parentFragmentManager, "")
                    })
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

        lifecycleScope.launch {
            mainViewModel.getMyBills()
            mainViewModel.billListResponse.collectLatest {
                initList(it)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        toolbar.setRightIcon(R.drawable.ic_notification_bell) {
//            openNotifications()
        }

        toolbar.setSecondRightIcon(R.drawable.ic_plus) {
            App.instance.navigateToAnimated(
                findNavController(),
                MyBillsFragmentDirections.actionMyBillsToTableList()
            )
        }

        toolbar.setTitle("Conturile mele")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(false)
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.recycler.adapter = compositeAdapter

        layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (compositeAdapter.currentList[position]) {
                    is ItemButtonBinder -> 2
                    else -> 1
                }
            }
        }
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

}