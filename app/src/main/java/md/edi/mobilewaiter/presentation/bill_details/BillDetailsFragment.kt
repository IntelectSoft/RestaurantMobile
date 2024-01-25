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
import md.edi.mobilewaiter.data.remote.response.bills.BillItem
import md.edi.mobilewaiter.databinding.FragmentBillDetailsBinding
import md.edi.mobilewaiter.presentation.add_client.AddClientActivity
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.dialog.DialogActionInputText
import md.edi.mobilewaiter.presentation.main.items.*
import md.edi.mobilewaiter.presentation.main.viewmodel.MainViewModel
import md.edi.mobilewaiter.utils.ContextManager
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.HelperFormatter
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors

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
    private var bill: BillItem? = null
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
                        getString(R.string.eliberare) + AssortmentController.getAssortmentNameById(line.AssortimentUid),
                        getString(R.string.sunteti_sigur_ca_doriti_sa_eliberati_acest_produs)
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

        if (billId== null){
            dialogShowCloseFragment("Atentie!","Nu a fost obtinut identificatorul contului!")
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getBillDetailResult.collectLatest {
                progressDialog.dismiss()
                if(it != null){
                    when (it.Result) {
                        0 -> {
                            bill = it.BillsList.first()

                            if(bill == null){
                                dialogShowCloseFragment("Atentie!","Nu am putut obtine detalii despre cont!")
                            }
                            else{
                                displayBillDetail(bill)
                            }
                        }

                        -9 -> {
                            dialogGetBillsShow(
                                getString(R.string.eroare_la_obtinerea_contului),
                                it.ResultMessage.toString()
                            )
                        }

                        else -> {
                            dialogGetBillsShow(
                                getString(R.string.eroare_la_obtinerea_contului),
                                ErrorHandler().getErrorMessage(
                                    EnumRemoteErrors.getByValue(it.Result)
                                )
                            )
                        }
                    }
                }
                else{
                    dialogShowCloseFragment("Atentie!","Nu a fost obtinut raspuns de la serviciu!")
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
                showPrinters(printerList, getString(R.string.alegeti_unde_sa_imprimi), false)
            }
        }

        binding.buttonEdit.setOnClickListener {
            bill?.let {
                CreateBillController.editBill(it)
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
                when (it.Result) {
                    0 -> {
                        Toast.makeText(context, getString(R.string.contul_a_fost_imprimat), Toast.LENGTH_SHORT)
                            .show()
                    }

                    -9 -> {
                        dialogShow(
                            getString(R.string.contul_nu_a_fost_printat),
                            it.ResultMessage
                        )
                    }

                    else -> {
                        dialogShow(
                            getString(R.string.contul_nu_a_fost_printat),
                            ErrorHandler().getErrorMessage(
                                EnumRemoteErrors.getByValue(it.Result)
                            )
                        )
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.closeBillResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        onDismissListener?.onCloseBill(this@BillDetailsFragment)
                        findNavController().popBackStack()
                        Toast.makeText(
                            context,
                            getString(R.string.contul_a_fost_inchis_cu_succes),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    -9 -> {
                        dialogShowCloseBill(
                            getString(R.string.contul_nu_a_fost_inchis),
                            it.ResultMessage
                        )
                    }

                    else -> {
                        dialogShowCloseBill(
                            getString(R.string.contul_nu_a_fost_inchis),
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
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.changeTableResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        CreateBillController.clearAllData()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.contul_a_fost_modificat),
                            Toast.LENGTH_SHORT
                        ).show()
                        BillsController.changeTableForBill(
                            it.BillsList[0].Uid,
                            it.BillsList[0].TableUid
                        )
                        initToolbar(
                            getString(R.string.contul) + " ${it.BillsList[0].Number}",
                            getString(R.string.oaspeti) + " ${it.BillsList[0].Guests}"
                        )
                    }

                    -9 -> {
                        dialogShow(getString(R.string.eroare_modificare_cont), it.ResultMessage)
                    }

                    else -> {
                        dialogShow(
                            getString(R.string.eroare_modificare_cont),
                            ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result))
                        )
                    }
                }
            }
        }

        setFragmentResultListener("tableResult") { requestKey, bundle ->
            val result = bundle.getString("tableId")
            if (result != null && result != "") {
                bill?.let {
                    CreateBillController.changeTableBill(it, result)
                    binding.textBillNumber.text = AssortmentController.getTableNumberById(result)
                    progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
                    progressDialog.show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.changeOrder()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        billId?.let {
            progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
            progressDialog.show()
            viewModel.getBillDetail(it)
        } ?: dialogGetBillsShow(
            getString(R.string.imposibil_de_obtinut_detalii_contului),
            getString(R.string.nu_s_a_transmis_identificatorul_unic_al_contului),
            true
        )
    }

    private fun displayBillDetail(bill: BillItem?) {
        bill?.let {

            initToolbar(
                 getString(R.string.contul) + " ${it.Number}",
                getString(R.string.oaspeti) + " ${it.Guests}"
            )

            it.TableUid.let {
                binding.textBillNumber.text = AssortmentController.getTableNumberById(it)
            }

            if (it.ClientName != null)
                binding.textClient.text = it.ClientName
            if (it.Sum != it.SumAfterDiscount) {
                val sumText = "${
                    HelperFormatter.formatDouble(
                        it.Sum,
                        false
                    )
                } " + getString(R.string.cu_red) + " ${HelperFormatter.formatDouble(it.SumAfterDiscount, true)}"
                binding.textSum.text = sumText
            } else {
                if (it.Sum == 0.0) {
                    binding.textSum.text = HelperFormatter.formatDouble(0.0, false)
                } else {
                    binding.textSum.text = HelperFormatter.formatDouble(it.SumAfterDiscount, true)
                }
            }

            binding.buttonChangeTable.setOnClickListener { view_ ->
                if (AssortmentController.getTablesDelegate().isEmpty()) {
                    Toast.makeText(context, getString(R.string.mesele_nu_sunt_setate), Toast.LENGTH_SHORT).show()
                } else {
                    App.instance.navigateToAnimated(
                        findNavController(),
                        BillDetailsFragmentDirections.actionBillsDetailsToTableList(it.Uid)
                    )
                }

            }

            val adapter = mutableListOf<DelegateAdapterItem>()
            val listLines = it.Lines
            val filteredList =
                it.Lines.filter { it.KitUid == "00000000-0000-0000-0000-000000000000" }

            filteredList.forEach { line ->
                val kitLines = listLines.filter { it.KitUid == line.Uid }
                adapter.add(
                    ItemBillLineBinder(
                        ItemBillLine(
                            tag = "line",
                            line = line,
                            kitLines = kitLines
                        )
                    )
                )

                kitLines.let {
                    it.forEachIndexed { index, kitLine ->
                        adapter.add(
                            ItemKitLineBinder(
                                ItemKitLine(
                                    tag = "kitLine",
                                    assortmentId = kitLine.AssortimentUid,
                                    count = kitLine.Count,
                                    price = kitLine.SumAfterDiscount,
                                    queueNumber = kitLine.QueueNumber,
                                    isLast = (index + 1) == it.size
                                )
                            )
                        )
                    }
                }

            }

            binding.list.adapter = compositeAdapter
            compositeAdapter.submitList(adapter)
        } ?: dialogGetBillsShow(
            getString(R.string.imposibil_de_obtinut_detalii_contului),
            getString(R.string.lista_de_conturi_goala),
            true
        )
    }

    private fun initToolbar(title: String, description: String) {
        val toolbar = binding.toolbar

        toolbar.setTitle(title)
        toolbar.setSubTitle(description)
        toolbar.showBottomLine(true)
        toolbar.showLeftBtn(true)

        toolbar.setRightIcon(R.drawable.ic_change_guest_number) {
            dialogShowChangeGuest(getString(R.string.introduceti_noul_numar_de_oaspeti))
        }

        toolbar.setLeftClickListener {
            CreateBillController.clearAllData()
            findNavController().popBackStack()
        }
    }

    private fun printBill() {
        progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
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
                progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
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
        dialog.setTitle(getString(R.string.alegeti_tipul_de_plata))
        dialog.setCancelable(false)
        dialog.setAdapter(adapterComments) { _, which ->
            closureId = closureItemsMapList[which]["Guid"] as String
            val printerList = AssortmentController.getPrinters()
            if (printerList.isEmpty()) {
                closeBill()
            } else {
                showPrinters(printerList, getString(R.string.alegeti_unde_sa_imprimi_bonul_final), true)
            }
        }
        dialog.setNegativeButton(getString(R.string.renun)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun dialogShowLineClicked(title: String, description: String) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, getString(R.string.da), getString(R.string.renun), {
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
        dialog.setNegativeButton(getString(R.string.renun)) { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        dialog.setPositiveButton(getString(R.string.imprima_implicit)) { dialogInterface, i ->
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
            DialogAction(it, title, description, getString(R.string.reincearca), getString(R.string.renun), {
                it.dismiss()
                printBill()
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun dialogShowCloseBill(title: String, description: String?) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, getString(R.string.reincearca), getString(R.string.renun), {
                it.dismiss()
                closeBill()
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun dialogShowCloseFragment(title: String, description: String?) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, getString(R.string.ok), getString(R.string.renun), {
                findNavController().popBackStack()
            }, {
                findNavController().popBackStack()
            }).show()
        }
    }

    private fun dialogShowChangeGuest(title: String, description: String? = null) {
        requireContext().let {
            DialogActionInputText(it, title, description, getString(R.string.salveaza), getString(R.string.renun), { dialog, text ->
                dialog.dismiss()
                bill?.let {
                    CreateBillController.changeTableBill(it, "", text.toInt())
                    progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
                    progressDialog.show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.changeOrder()
                    }
                }
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun dialogGetBillsShow(
        title: String?,
        description: String?,
        isTerminated: Boolean = false
    ) {
        DialogAction(
            requireActivity(),
            title,
            description,
            if (isTerminated) getString(R.string.ok) else getString(R.string.reincearca),
            getString(R.string.renun),
            {
                it.dismiss()
                if (isTerminated) {
                    findNavController().popBackStack()
                } else {
                    progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
                    progressDialog.show()
                    billId?.let {
                        viewModel.getBillDetail(it)
                    }
                }
            },
            {
                findNavController().popBackStack()
            }).show()
    }

}