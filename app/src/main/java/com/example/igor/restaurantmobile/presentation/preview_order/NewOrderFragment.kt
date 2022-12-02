package com.example.igor.restaurantmobile.presentation.preview_order

import android.app.ProgressDialog
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
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.data.listeners.BottomSheetOnDismissListener
import com.example.igor.restaurantmobile.databinding.FragmentNewOrderBinding
import com.example.igor.restaurantmobile.presentation.preview_order.items.ItemOrderDelegate
import com.example.igor.restaurantmobile.presentation.preview_order.viewmodel.NewOrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.igor.restaurantmobile.common.delegates.CompositeAdapter
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.table.TableFragmentDirections
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class NewOrderFragment : Fragment(), BottomSheetOnDismissListener {
    val viewModel by viewModels<NewOrderViewModel>()
    private lateinit var layoutManager: GridLayoutManager
    val progressDialog by lazy { ProgressDialog(requireContext()) }

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemOrderDelegate { orderLine ->
                Log.e("TAG", "Order line clicked: ${orderLine.toString()}")
                val detailsLine = BillLineDetailsDialogFragment.newInstance(orderLine.internUid)
                detailsLine.setListener(this)
                detailsLine.show(
                    parentFragmentManager,
                    BillLineDetailsDialogFragment::class.simpleName
                )
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

        binding.button5.setOnClickListener {
            if (compositeAdapter.delegates.size() > 0) {
                lifecycleScope.launch(Dispatchers.Main) {
                    progressDialog.setMessage("Va rugam asteptati")
                    progressDialog.show()
                    viewModel.saveNewOrder()
                }
            } else {
                Toast.makeText(
                    context,
                    "Adaugati produse pentru a crea un cont!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.addBillResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        CreateBillController.clearAllData()
                        Toast.makeText(
                            requireContext(),
                            "Contul a fost salvat!",
                            Toast.LENGTH_SHORT
                        ).show()
                        App.instance.navigateToAnimatedPopBackStack(
                            findNavController(),
                            NewOrderFragmentDirections.actionPreviewCartToMyBills(),
                            R.id.action_preview_cart_to_myBills,
                            true
                        )
                    }
                    -9 -> {
                        dialogShow("Eroare salvare contului", it.ResultMessage)
                    }
                    else -> {
                        dialogShow(
                            "Eroare salvare contului",
                            ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result))
                        )
                    }
                }
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            val lines = viewModel.getOrderLinesAssortment()
            initList(lines)
        }

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

        layoutManager = GridLayoutManager(context, 1)
        binding.listPreview.layoutManager = layoutManager

        compositeAdapter.submitList(items)
    }

    override fun onResume() {
        super.onResume()

        showSumOfBill()
    }

    private fun showSumOfBill() {
        var sumBill = 0.0
        CreateBillController.orderModel.Orders.forEach {
            sumBill += it.sum
        }
        binding.textSumTotal.text = "$sumBill MDL"
    }

    override fun onDialogDismiss(dialogs: BillLineDetailsDialogFragment, toString: String) {
        CreateBillController.removeLineByInternId(toString)
        val lines = viewModel.getOrderLinesAssortment()
        compositeAdapter.submitList(lines)
        showSumOfBill()
        dialogs.dismiss()
    }

    private fun dialogShow(title: String?, description: String?) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, "Reincearca", "Renunta", {
                progressDialog.setMessage("Va rugam asteptati")
                progressDialog.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.saveNewOrder()
                }
            }, {

            }).show()
        }
    }
}