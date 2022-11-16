package com.example.igor.restaurantmobile.presentation.table

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
import androidx.recyclerview.widget.GridLayoutManager.DEFAULT_SPAN_COUNT
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.itemButton.ItemButton
import com.example.igor.restaurantmobile.common.itemButton.ItemButtonBinder
import com.example.igor.restaurantmobile.common.itemButton.ItemButtonDelegate
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.databinding.FragmentMyBillsBinding
import com.example.igor.restaurantmobile.databinding.FragmentTableListBinding
import com.example.igor.restaurantmobile.presentation.main.MyBillsFragmentDirections
import com.example.igor.restaurantmobile.presentation.main.items.ItemBill
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillBinder
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillDelegate
import com.example.igor.restaurantmobile.presentation.main.viewmodel.MainViewModel
import com.example.igor.restaurantmobile.presentation.table.items.ItemTable
import com.example.igor.restaurantmobile.presentation.table.items.ItemTableBinder
import com.example.igor.restaurantmobile.presentation.table.items.ItemTableDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import ro.lensa.common.decor.GridSpanDecoration
import ro.lensa.common.recycler_view.delegates.CompositeAdapter

@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemTableDelegate { id ->
                CreateBillController.setTableId(id)
                App.instance.navigateToAnimatedPopBackStack(findNavController(),TableFragmentDirections.tableToAssortment(), R.id.action_tableList_to_myBills,true)
            })

            .build()
    }

    val binding by lazy {
        FragmentTableListBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val tables = AssortmentController.getTablesDelegate()
        if(tables.isEmpty())
            App.instance.navigateToAnimatedPopBackStack(findNavController(),TableFragmentDirections.tableToAssortment(), R.id.action_tableList_to_myBills,true)
        else
            initList(tables)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar

        toolbar.setTitle("Alege masa clientului")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.viewTable.adapter = compositeAdapter

        layoutManager = GridLayoutManager(context,4)
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return spanCount
////                when (compositeAdapter.currentList[position]) {
////                    is ItemButtonBinder -> 1
////                    else -> 1
////                }
//            }
//        }
        binding.viewTable.layoutManager = layoutManager

        val deco = GridSpanDecoration(
            binding.root.context.resources.getDimensionPixelSize(
                com.intuit.sdp.R.dimen._4sdp
            ),
            true
        )

        while (binding.viewTable.itemDecorationCount > 0) {
            binding.viewTable.removeItemDecorationAt(0)
        }

        binding.viewTable.addItemDecoration(deco)

        compositeAdapter.submitList(items)
    }

    private fun getNavBuilder(): NavOptions.Builder {
        return NavOptions.Builder()

    }

    private fun getNavOptionsRightToLeft(builder: NavOptions.Builder): NavOptions? {
        return builder
            .setEnterAnim(R.anim.h_fragment_enter)
            .setExitAnim(R.anim.h_fragment_pop_exit)
            .setPopEnterAnim(R.anim.h_fragment_pop_enter)
            .setPopExitAnim(R.anim.h_fragment_exit)
            .build()
    }

}