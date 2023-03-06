package md.edi.mobilewaiter.presentation.bill_details

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.CompositeAdapter
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.App
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.controllers.BillsController
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.data.listeners.ActionOnBillListener
import md.edi.mobilewaiter.data.remote.response.assortment.PrinterItem
import md.edi.mobilewaiter.databinding.FragmentBillDetailsBinding
import md.edi.mobilewaiter.presentation.add_client.AddClientActivity
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.dialog.DialogActionInputText
import md.edi.mobilewaiter.presentation.main.items.*
import md.edi.mobilewaiter.presentation.main.viewmodel.MainViewModel
import md.edi.mobilewaiter.utils.ContextManager
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors
import java.text.DecimalFormat

const val ARG_ITEM_ID = "item_bill"

@AndroidEntryPoint
class BillDetailsFragment : Fragment() {

    val viewModel by viewModels<MainViewModel>()

    val binding by lazy {
        FragmentBillDetailsBinding.inflate(LayoutInflater.from(context))
    }

    val progressDialog by lazy { ProgressDialog(context) }
    var onDismissListener: ActionOnBillListener? = null
    private var billId: String? = null
    private var printerId: String? = null
    private var closedPrinterId: String? = null
    private var closureId: String = ""

    companion object {
        fun newInstance(billId: String): BillDetailsFragment =
            BillDetailsFragment().apply {
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
            ).add(
                ItemKitLineDelegate { line ->

                }
            )
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.list.layoutManager = GridLayoutManager(context, 1)

        billId = arguments?.getString("billId")

        val bill = billId?.let {
            BillsController.getBillById(it)
        }

        if(bill == null){
            findNavController().popBackStack()
        }
        else{
            initToolbar("Contul: ${bill.Number}", "Oaspeti: ${bill.Guests}")
        }

        bill?.let {
            it.TableUid?.let {
                binding.textBillNumber.text = AssortmentController.getTableNumberById(it)
            }

            if (it.ClientName != null)
                binding.textClient.text = it.ClientName
            if (it.Sum != it.SumAfterDiscount) {
                binding.textSum.text =
                    DecimalFormat(".0#").format(it.Sum) + "/cu red: " + DecimalFormat(".0#").format(it.SumAfterDiscount) + " MDL"
            } else {
                if(it.Sum == 0.0){
                    binding.textSum.text = "0 MDL"
                }else{
                    binding.textSum.text = DecimalFormat(".0#").format(it.Sum) + " MDL"
                }
            }

            binding.buttonChangeTable.setOnClickListener {
                if(AssortmentController.getTablesDelegate().isEmpty()){
                    Toast.makeText(context, "Mesele nu sunt setate!", Toast.LENGTH_SHORT).show()
                }else{
                    App.instance.navigateToAnimated(
                        findNavController(),
                        BillDetailsFragmentDirections.actionBillsDetailsToTableList(bill.Uid)
                    )
                }

            }
        }

        binding.buttonClose.setOnClickListener {
            showClosureTypes()
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
                    BillDetailsFragmentDirections.actionBillsDetailsToAssortmentList()
                )
            }
        }

        binding.buttonSplit.setOnClickListener {
            billId?.let {
                App.instance.navigateToAnimated(
                    findNavController(),
                    BillDetailsFragmentDirections.actionBillsDetailsToFragmentSplit(it)
                )
            }
        }

        binding.buttonAddClient.setOnClickListener {
            val addClientActivity = Intent(requireContext(), AddClientActivity::class.java)
            addClientActivity.putExtra("billId", billId)
            startActivity(addClientActivity)
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
                    onDismissListener?.onCloseBill(this@BillDetailsFragment)
                    findNavController().popBackStack()
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
        setFragmentResultListener("tableResult") { requestKey, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val result = bundle.getString("tableId")
            if(result != null && result != ""){
                bill?.let{
                    CreateBillController.changeTableBill(it, result)
                    binding.textBillNumber.text = AssortmentController.getTableNumberById(result)
                    progressDialog.setMessage("Va rugam asteptati...")
                    progressDialog.show()
                    lifecycleScope.launch(Dispatchers.IO){
                        viewModel.changeOrder()
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.changeTableResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        CreateBillController.clearAllData()
                        Toast.makeText(
                            requireContext(),
                            "Contul a fost modificat!",
                            Toast.LENGTH_SHORT
                        ).show()
                        BillsController.changeTableForBill(it.BillsList[0].Uid, it.BillsList[0].TableUid)
                        initToolbar("Contul: ${it.BillsList[0].Number}", "Oaspeti: ${it.BillsList[0].Guests}")
                    }
                    -9 -> {
                        dialogShow("Eroare modificare cont!", it.ResultMessage)
                    }
                    else -> {
                        dialogShow(
                            "Eroare modificare cont!",
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
        val bill = billId?.let {
            BillsController.getBillById(it)
        }

        val adapter = mutableListOf<DelegateAdapterItem>()
        val listLines = bill?.Lines
        val filteredList =
            bill?.Lines?.filter { it.KitUid == "00000000-0000-0000-0000-000000000000" }

        filteredList?.forEach { line ->
            val kitLines = listLines?.filter { it.KitUid == line.Uid }
            adapter.add(
                ItemBillLineBinder(
                    ItemBillLine(
                        tag = "line",
                        line = line,
                        kitLines = kitLines
                    )
                )
            )

            kitLines?.let {
                it.forEachIndexed { index, kitLine ->
                    adapter.add(
                        ItemKitLineBinder(
                            ItemKitLine(
                                tag = "kitLine",
                                line = kitLine,
                                isLast = (index + 1) == it.size
                            )
                        )
                    )
                }
            }

        }

        binding.list.adapter = compositeAdapter
        compositeAdapter.submitList(adapter)
    }

    private fun initToolbar(title: String, description: String) {
        val toolbar = binding.toolbar

        toolbar.setTitle(title)
        toolbar.setSubTitle(description)
        toolbar.showBottomLine(true)
        toolbar.showLeftBtn(true)

        toolbar.setRightIcon(R.drawable.ic_change_guest_number) {
            dialogShowChangeGuest("Introduceti noul numar de oaspeti!")
        }

        toolbar.setLeftClickListener {
            CreateBillController.clearAllData()
            findNavController().popBackStack()
        }
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
            val printerList = AssortmentController.getPrinters()
            if (printerList.isEmpty()) {
                closeBill()
            } else {
                showPrinters(printerList, "Alegeti unde sa imprimi bonul final!", true)
            }
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
                closeBill()
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
                closeBill()
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

    private fun dialogShowChangeGuest(title: String, description: String? = null) {
        requireContext().let {
            DialogActionInputText(it, title, description, "Salveaza", "Renunta", { dialog, text ->
                dialog.dismiss()
                val bill = billId?.let {
                    BillsController.getBillById(it)
                }
                bill?.let{
                    CreateBillController.changeTableBill(it, "", text.toInt())
                    progressDialog.setMessage("Va rugam asteptati...")
                    progressDialog.show()
                    lifecycleScope.launch(Dispatchers.IO){
                        viewModel.changeOrder()
                    }
                }

            }, {
                it.dismiss()
            }).show()
        }
    }

}