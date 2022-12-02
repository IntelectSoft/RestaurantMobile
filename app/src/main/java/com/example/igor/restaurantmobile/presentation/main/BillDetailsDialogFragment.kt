package com.example.igor.restaurantmobile.presentation.main

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.data.listeners.ActionOnBillListener
import com.example.igor.restaurantmobile.databinding.FragmentBillLineDialogBinding
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLine
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLineBinder
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillLineDelegate
import com.example.igor.restaurantmobile.presentation.main.viewmodel.MainViewModel
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.igor.restaurantmobile.common.delegates.CompositeAdapter
import com.example.igor.restaurantmobile.data.remote.response.assortment.PrinterItem
import com.example.igor.restaurantmobile.presentation.add_client.AddClientActivity

const val ARG_ITEM_ID = "item_bill"

@AndroidEntryPoint
class BillDetailsDialogFragment : BottomSheetDialogFragment() {

    val viewModel by viewModels<MainViewModel>()

    val binding by lazy {
        FragmentBillLineDialogBinding.inflate(LayoutInflater.from(context))
    }

    val progressDialog by lazy { ProgressDialog(context) }
    var onDismissListener: ActionOnBillListener? = null
    private var billId: String? = null
    private var printerId: String? = null
    private var closedPrinterId: String? = null
    private var closureId: String = ""

    companion object {
        fun newInstance(billId: String): BillDetailsDialogFragment =
            BillDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, billId)
                }
            }
    }

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(
                ItemBillLineDelegate { line ->
                    Log.e("TAG", "Line clicked: ${line} ")
                    dialogShowLineClicked(
                        "Eliberare ${AssortmentController.getAssortmentNameById(line.AssortimentUid)}",
                        "Sunteti sigur ca doriti sa eliberati acest produs?"
                    )
                }
            )
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.list.layoutManager = GridLayoutManager(context, 1)

        billId = arguments?.getString(ARG_ITEM_ID)

        val bill = billId?.let {
            BillsController.getBillById(it)
        }

        binding.textBillNumber.text = bill?.Number.toString()

        val adapter = mutableListOf<DelegateAdapterItem>()

        bill?.Lines?.forEach {
            adapter.add(
                ItemBillLineBinder(
                    ItemBillLine(
                        tag = "line",
                        line = it
                    )
                )
            )

        }

        binding.list.adapter = compositeAdapter
        compositeAdapter.submitList(adapter)

        binding.buttonClose.setOnClickListener {
            val printerList = AssortmentController.getPrinters()
            if (printerList.isEmpty()) {
                showClosureTypes()
            } else {
                showPrinters(printerList, "Alegeti unde sa imprimi bonul final!", true)
            }
        }

        binding.buttonPrint.setOnClickListener {
            val printerList = AssortmentController.getPrinters()
            if (printerList.isEmpty()) {
                printBill()
            } else {
                showPrinters(printerList, "Alegeti unde sa imprimi!", false)
            }
        }

        binding.buttonEdit.setOnClickListener {
            bill?.let {
                CreateBillController.editBill(bill)
                App.instance.navigateToAnimated(
                    findNavController(),
                    MyBillsFragmentDirections.actionMyBillsToAssortmentList()
                )
                dismiss()
            }
        }

        binding.buttonSplit.setOnClickListener {
            billId?.let {
                App.instance.navigateToAnimated(
                    findNavController(),
                    MyBillsFragmentDirections.actionMyBillsToFragmentSplit(it)
                )
                dismiss()
            }
        }

        binding.buttonAddClient.setOnClickListener {
            val addClientActivity = Intent(requireContext(), AddClientActivity::class.java)
            addClientActivity.putExtra("billId", billId)
            startActivity(addClientActivity)
            dismiss()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.billPrintResult.collectLatest {
                progressDialog.dismiss()
                if (it.Result == 0) {
                    Toast.makeText(context, "Contul a fost imprimat!", Toast.LENGTH_SHORT).show()
                } else if (it.Result == -9) {
                    dialogShow(
                        "Contul nu a fost printat!",
                        it.ResultMessage
                    )
                } else {
                    dialogShow(
                        "Contul nu a fost printat!",
                        ErrorHandler().getErrorMessage(
                            EnumRemoteErrors.getByValue(it.Result)
                        )
                    )
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.closeBillResult.collectLatest {
                progressDialog.dismiss()
                if (it.Result == 0) {
                    onDismissListener?.onCloseBill(this@BillDetailsDialogFragment)
                    this@BillDetailsDialogFragment.dismiss()
                    Toast.makeText(context, "Contul a fost inchis cu succes!", Toast.LENGTH_SHORT)
                        .show()
                } else if (it.Result == -9) {
                    dialogShowCloseBill(
                        "Contul nu a fost inchis!",
                        it.ResultMessage
                    )
                } else {
                    dialogShowCloseBill(
                        "Contul nu a fost inchis!",
                        if (it.Result == 6) {
                            ErrorHandler().getErrorMessage(
                                EnumRemoteErrors.getByValue(it.Result)
                            ) + "\n" + it.ResultMessage
                        } else {
                            ErrorHandler().getErrorMessage(
                                EnumRemoteErrors.getByValue(it.Result)
                            )
                        }

                    )
                }
            }
        }


        return binding.root
    }

    private fun printBill() {
        progressDialog.setMessage("Va rugam asteptati...")
        progressDialog.show()
        billId?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.printBill(it, printerId)
            }
        }
    }

    private fun closeBill() {
        billId?.let {
            lifecycleScope.launch(Dispatchers.Main) {
                progressDialog.setMessage("Va rugam asteptati...")
                progressDialog.show()
                viewModel.closeBill(it, closureId, closedPrinterId)
            }
        }
    }


    private fun showClosureTypes() {
        val closureTypeList = AssortmentController.getClosureTypes()
        val closureItemsMapList = mutableListOf<HashMap<String, String>>()

        for (closureTypeItem in closureTypeList) {
            val itemMap: HashMap<String, String> = HashMap()

            itemMap["Name"] = closureTypeItem.Name
            itemMap["Guid"] = closureTypeItem.Uid

            closureItemsMapList.add(itemMap)
        }

        val adapterComments = SimpleAdapter(
            requireContext(),
            closureItemsMapList,
            android.R.layout.simple_list_item_1,
            arrayOf("Name"),
            intArrayOf(
                android.R.id.text1
            )
        )
        val dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialog.setTitle("Alegeti tipul de plata")
        dialog.setCancelable(false)
        dialog.setAdapter(adapterComments) { dialog, which ->
            closureId = closureItemsMapList[which]["Guid"] as String
            closeBill()
        }
        dialog.setNegativeButton("Renunta") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun dialogShowLineClicked(title: String, description: String) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, "DA", "Renunta", {
                it.dismiss()

            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun showPrinters(
        printersList: List<PrinterItem>,
        title: String,
        actionCloseBill: Boolean
    ) {
        val itemsMapList = mutableListOf<HashMap<String, String>>()

        for (printerItem in printersList) {
            val itemMap: HashMap<String, String> = HashMap()

            itemMap["Name"] = printerItem.Name
            itemMap["Guid"] = printerItem.Uid

            itemsMapList.add(itemMap)
        }

        val adapterComments = SimpleAdapter(
            requireContext(),
            itemsMapList,
            android.R.layout.simple_list_item_1,
            arrayOf("Name"),
            intArrayOf(
                android.R.id.text1
            )
        )
        val dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setAdapter(adapterComments) { dialog, which ->
            if (actionCloseBill) {
                closedPrinterId = itemsMapList[which]["Guid"] as String
                showClosureTypes()
            } else {
                printerId = itemsMapList[which]["Guid"] as String
                printBill()
            }
        }
        dialog.setNegativeButton("Renunta") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        dialog.setPositiveButton("Imprima implicit") { dialogInterface, i ->
            if (actionCloseBill) {
                closedPrinterId = null
                showClosureTypes()
            } else {
                printerId = null
                printBill()
            }
        }

        dialog.show()
    }

    private fun dialogShow(title: String, description: String?) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, "Reincearca", "Renunta", {
                it.dismiss()
                printBill()
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun dialogShowCloseBill(title: String, description: String?) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, "Reincearca", "Renunta", {
                it.dismiss()
                closeBill()
            }, {
                it.dismiss()
            }).show()
        }
    }

    fun setListener(listeners: ActionOnBillListener) {
        onDismissListener = listeners
    }

}