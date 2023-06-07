package md.edi.mobilewaiter.presentation.main.split

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
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.controllers.BillsController
import md.edi.mobilewaiter.databinding.FragmentSplitBillBinding
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.main.split.items.ItemLinesSplit
import md.edi.mobilewaiter.presentation.main.split.items.ItemLinesSplitBinder
import md.edi.mobilewaiter.presentation.main.split.items.ItemLinesSplitDelegate
import md.edi.mobilewaiter.presentation.main.split.items.LineItemModel
import md.edi.mobilewaiter.presentation.preview_order.viewmodel.NewOrderViewModel
import md.edi.mobilewaiter.utils.ContextManager
import md.edi.mobilewaiter.utils.ErrorHandler
import md.edi.mobilewaiter.utils.enums.EnumRemoteErrors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.CompositeAdapter

@AndroidEntryPoint
class SplitBillFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager

    val viewModel by viewModels<NewOrderViewModel>()
    val progressDialog by lazy { ProgressDialog(requireContext()) }


    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemLinesSplitDelegate { item ->
                listOfMappedItems.find { item.line.Uid == it.Uid }?.let {
                    it.isChecked = item.isChecked
                }

                Log.e("TAG", "Filtered selected: ${listOfMappedItems.filter { !it.isChecked }.size}")

            })
            .build()
    }

    companion object {
        val listOfMappedItems = mutableListOf<LineItemModel>()

        fun canCheckedIt(): Boolean {
            val list = listOfMappedItems.filter { !it.isChecked }
            return list.isNotEmpty()
        }
    }

    val binding by lazy {
        FragmentSplitBillBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val billId = arguments?.getString("billId")

        val bill = billId?.let {
            BillsController.getBillById(it)
        }

        val adapter = mutableListOf<DelegateAdapterItem>()

        bill?.Lines?.filter { it.KitUid == "00000000-0000-0000-0000-000000000000" }?.map {
            val mappedItem = LineItemModel(
                Uid = it.Uid,
                AssortimentUid = it.AssortimentUid,
                Comments = it.Comments,
                Count= it.Count,
                QueueNumber = it.QueueNumber,
                KitUid = it.KitUid,
                PriceLineUid = it.PriceLineUid,
                Sum = it.Sum,
                SumAfterDiscount = it.SumAfterDiscount,
                isChecked = false
            )
            listOfMappedItems.add(mappedItem)
            adapter.add(
                ItemLinesSplitBinder(
                    ItemLinesSplit(
                        tag = "line",
                        line = mappedItem,
                        name = AssortmentController.getAssortmentNameById(it.AssortimentUid),
                        allowNonInteger = AssortmentController.getAssortmentById(it.AssortimentUid)?.AllowNonIntegerSale ?: false,
                        price = AssortmentController.getAssortmentById(it.AssortimentUid)?.Price ?: 0.0,
                        isChecked = false
                    )
                )
            )
        }

        initList(adapter)

        binding.buttonSplitBill.setOnClickListener {
            val listFiltered = listOfMappedItems.filter { it.isChecked }
            Log.e("TAG", "Filtered selected: $listFiltered")

            if (listFiltered.isNotEmpty()){
                lifecycleScope.launch(Dispatchers.Main) {
                    progressDialog.setMessage(getString(R.string.va_rugam_asteptati))
                    progressDialog.show()
                    bill?.let {
                        viewModel.splitBill(bill, listFiltered)
                    }
                }

            }else{
                Toast.makeText(context, getString(R.string.selectati_cel_putin_un_produs), Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch(Dispatchers.Main){
            viewModel.splitBillResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        BillsController.splitBill(it.BillsList[0])
                        Toast.makeText(requireContext(),getString(R.string.contul_a_fost_salvat),Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    -9 -> {
                        dialogShow(getString(R.string.eroare_salvare_contului), it.ResultMessage)
                    }
                    else -> {
                        dialogShow(getString(R.string.eroare_salvare_contului),ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)))
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar

        toolbar.setTitle(getString(R.string.impartirea_contului))
        toolbar.setSubTitle(getString(R.string.selectati_produse_care_urmeaza_adaugate_in_cont_nou))
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.viewTable.adapter = compositeAdapter
        layoutManager = GridLayoutManager(context,1)
        binding.viewTable.layoutManager = layoutManager
        compositeAdapter.submitList(items)
    }

    private fun dialogShow(title: String, description: String?) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, getString(R.string.ok), getString(R.string.renun), {
                it.dismiss()
            }, {

            }).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listOfMappedItems.clear()
    }

}

