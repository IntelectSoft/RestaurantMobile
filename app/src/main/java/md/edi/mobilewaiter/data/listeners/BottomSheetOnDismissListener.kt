package md.edi.mobilewaiter.data.listeners

import md.edi.mobilewaiter.presentation.preview_order.BillLineDetailsDialogFragment

interface BottomSheetOnDismissListener {
    fun onDialogDismiss(dialogs: BillLineDetailsDialogFragment, toString: String)

}