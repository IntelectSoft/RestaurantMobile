package com.example.igor.restaurantmobile.presentation.add_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.databinding.FragmentNewOrderBinding
import com.example.igor.restaurantmobile.databinding.FragmentTableListBinding
import com.example.igor.restaurantmobile.presentation.add_order.items.ItemOrderDelegate
import com.example.igor.restaurantmobile.presentation.add_order.viewmodel.NewOrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ro.lensa.common.decor.GridSpanDecoration
import ro.lensa.common.recycler_view.delegates.CompositeAdapter

@AndroidEntryPoint
class NewOrderFragment : Fragment() {
    val viewModel by viewModels<NewOrderViewModel>()
    private lateinit var layoutManager: GridLayoutManager

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemOrderDelegate { order ->

            })
            .build()
    }

    val binding by lazy {
        FragmentNewOrderBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lines = viewModel.getOrderLinesAssortment()
        initList(lines)

        binding.button5.setOnClickListener {
            lifecycleScope.launch {
                viewModel.saveNewOrder()
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

        toolbar.setTitle("Previzualizare cont")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.listPreview.adapter = compositeAdapter

        layoutManager = GridLayoutManager(context,1)
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return spanCount
////                when (compositeAdapter.currentList[position]) {
////                    is ItemButtonBinder -> 1
////                    else -> 1
////                }
//            }
//        }
        binding.listPreview.layoutManager = layoutManager

        val deco = GridSpanDecoration(
            binding.root.context.resources.getDimensionPixelSize(
                com.intuit.sdp.R.dimen._4sdp
            ),
            true
        )

        while (binding.listPreview.itemDecorationCount > 0) {
            binding.listPreview.removeItemDecorationAt(0)
        }

        binding.listPreview.addItemDecoration(deco)

        compositeAdapter.submitList(items)
    }
}