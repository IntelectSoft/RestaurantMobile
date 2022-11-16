package com.example.igor.restaurantmobile.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.igor.restaurantmobile.databinding.FragmentBillActionDialogBinding
import com.example.igor.restaurantmobile.databinding.FragmentBillLineDialogBinding
import com.example.igor.restaurantmobile.databinding.ItemBillLineBinding
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLineDelegate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ro.lensa.common.recycler_view.delegates.CompositeAdapter

const val ARG_ITEM_COUNT_AC = "item_bill"

class BillActionDialogFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        FragmentBillActionDialogBinding.inflate(LayoutInflater.from(context))
    }

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(
                ItemBillLineDelegate
                { bill ->

                }
            )
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.list.layoutManager = GridLayoutManager(context, 1)
        binding.list.adapter = ItemAdapter(30)

        return binding.root
    }

    private inner class ViewHolder constructor(binding: ItemBillLineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val text: TextView = binding.text
    }

    private inner class ItemAdapter constructor(private val mItemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                ItemBillLineBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = position.toString()
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {

        fun newInstance(billId: String): BillActionDialogFragment =
            BillActionDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_COUNT_AC, billId)
                }
            }
    }

}