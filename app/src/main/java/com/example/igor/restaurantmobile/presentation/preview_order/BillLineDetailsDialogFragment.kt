package com.example.igor.restaurantmobile.presentation.preview_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.data.listeners.BottomSheetOnDismissListener
import com.example.igor.restaurantmobile.databinding.FragmentLineDetailsDialogBinding
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.utils.ContextManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

const val ARG_LINE_ID = "internLineId"

@AndroidEntryPoint
class BillLineDetailsDialogFragment : BottomSheetDialogFragment() {

    val binding by lazy {
        FragmentLineDetailsDialogBinding.inflate(LayoutInflater.from(context))
    }
    var onDismissListener: BottomSheetOnDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val billId = arguments?.getString(ARG_LINE_ID) ?: ""
        CreateBillController.getInternLineById(billId)?.let {
            binding.textLineAssortmentName.text =
                AssortmentController.getAssortmentNameById(it.assortimentUid)

            if (it.comments.isEmpty()) {
                binding.textComment.text = "-"
                binding.textKitNames.text = "-"
                binding.textCustomComment.text = "-"
            } else {
                it.comments.toMutableList().forEach { guid ->
                    if (isGuid(guid)) {
                        val tryComment = AssortmentController.getCommentById(guid)
                        val tryKit = AssortmentController.getAssortmentNameById(guid)
                        if (tryComment != null) {
                            if (binding.textComment.text.isBlank()) {
                                binding.textComment.text = tryComment.Comment
                            } else {
                                binding.textComment.append(" | " + tryComment.Comment)
                            }
                        }
                        if (tryKit != "Not found") {
                            if (binding.textKitNames.text.isBlank()) {
                                binding.textKitNames.text = tryKit
                            } else {
                                binding.textKitNames.append(" | $tryKit")
                            }
                        }
                    } else {
                        binding.textCustomComment.append(guid)
                    }
                }
            }

            binding.textCount.text = if(it.count == 0.0){
               "0"
            }
            else{
                DecimalFormat(".0#").format(it.count)
            }
            binding.textPrice.text = if(it.price == 0.0){
                "0"
            }
            else{
                DecimalFormat(".0#").format(it.price)
            }
            binding.textSum.text = if(it.count == 0.0){
                "0"
            }
            else{
                DecimalFormat(".0#").format(it.sum)
            }


            binding.buttonEditLine.isEnabled = true
            binding.buttonEditLine.setOnClickListener { view1 ->
                App.instance.navigateToAnimated(findNavController(),
                    NewOrderFragmentDirections.actionPreviewCartToPreviewLineEdit(it.internUid))
                dismiss()
            }

            binding.buttonRemoveLine.setOnClickListener {  view2 ->
                dialogShow("Eliminare pozitiei!", "Sunteti sigur ca doriti sa eliminati ${AssortmentController.getAssortmentNameById(it.assortimentUid)}?", it.internUid)
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance(lineId: String): BillLineDetailsDialogFragment =
        BillLineDetailsDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_LINE_ID, lineId)
            }
        }
    }

    fun setListener(listeners: BottomSheetOnDismissListener) {
        onDismissListener = listeners
    }

    private fun dialogShow(title: String, description: String, internUid: String) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, "Elimina", "Renunta", {
                it.dismiss()
                onDismissListener?.onDialogDismiss(this@BillLineDetailsDialogFragment, internUid)
            }, {

            }).show()
        }
    }

    private fun isGuid(str: String): Boolean {
        val regex = ("^[{(]?[0-9A-Fa-f]{8}[-]?(?:[0-9A-Fa-f]{4}[-]?){3}[0-9A-Fa-f]{12}[)}]?$")
        val p1: Pattern = Pattern.compile(regex)
        val m1: Matcher = p1.matcher(str)
        return m1.matches()
    }

}