package com.example.igor.restaurantmobile.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.databinding.FragmentBillLineDialogBinding
import com.example.igor.restaurantmobile.databinding.ItemBillLineBinding
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLine
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLineBinder
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLineDelegate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ro.lensa.common.recycler_view.delegates.CompositeAdapter

const val ARG_ITEM_ID = "item_bill"

class BillDetailsDialogFragment : BottomSheetDialogFragment() {


    val binding by lazy {
        FragmentBillLineDialogBinding.inflate(LayoutInflater.from(context))
    }

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(
                ItemBillLineDelegate(
                    { bill ->

                    })
            )
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.list.layoutManager = GridLayoutManager(context,1)

        val billLine = arguments?.getString(ARG_ITEM_ID)?.let {
            BillsController.getBillById(it)?.Lines
        }

        val adapter = mutableListOf<DelegateAdapterItem>()

        billLine?.forEach{
            adapter.add(ItemBillLineBinder(
                ItemBillLine(
                    tag = "line",
                    line =  it
                )
            ))

        }

        binding.list.adapter = compositeAdapter
        compositeAdapter.submitList(adapter)

        binding.buttonClose.setOnClickListener {

        }

        binding.buttonPrint.setOnClickListener {

        }

        binding.buttonEdit.setOnClickListener {

        }

        return binding.root
    }

    companion object {

        fun newInstance(billId: String): BillDetailsDialogFragment =
            BillDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, billId)
                }
            }
    }

}