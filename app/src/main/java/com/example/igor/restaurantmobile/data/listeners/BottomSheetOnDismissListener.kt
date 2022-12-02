package com.example.igor.restaurantmobile.data.listeners

import android.app.Dialog
import com.example.igor.restaurantmobile.presentation.preview_order.BillLineDetailsDialogFragment

interface BottomSheetOnDismissListener {
    fun onDialogDismiss(dialogs: BillLineDetailsDialogFragment, toString: String)

}