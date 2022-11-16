package com.example.igor.restaurantmobile.presentation.assortment.counts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.databinding.FragmentCountAssortmentDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val ARG_ITEM_NAME = "item_name"
const val ARG_ITEM_ID = "item_id"
const val ARG_ITEM_PRICE_ID = "item_price_id"

class CountAssortmentDialogFragment : BottomSheetDialogFragment() {

    var selectedValue = 1

    val binding by lazy {
        FragmentCountAssortmentDialogBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.textView3.text = arguments?.getString(ARG_ITEM_NAME)

        binding.numberPicker.minValue = 0
        binding.numberPicker.maxValue = 50
        binding.numberPicker.value = 1
        binding.numberPicker.wrapSelectorWheel = false

        binding.numberPicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            selectedValue = newValue
        }

        binding.buttonAddCounts.setOnClickListener {
            CreateBillController.addAssortment(
                arguments?.getString(ARG_ITEM_PRICE_ID)!!,
                arguments?.getString(ARG_ITEM_ID)!!,
                selectedValue
            )
            CreateBillController.setCartCount(selectedValue)
            Log.e("TAG", "buttonAddCounts: ${CreateBillController.orderModel}" )

            dismiss()
        }

        return binding.root
    }

    companion object {

        fun newInstance(name: String, id: String, priceId: String): CountAssortmentDialogFragment =
            CountAssortmentDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_NAME, name)
                    putString(ARG_ITEM_ID, id)
                    putString(ARG_ITEM_PRICE_ID, priceId)
                }
            }
    }

}