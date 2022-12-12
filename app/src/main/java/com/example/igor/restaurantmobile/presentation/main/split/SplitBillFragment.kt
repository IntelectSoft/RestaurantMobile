package com.example.igor.restaurantmobile.presentation.main.split

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
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.databinding.FragmentSplitBillBinding
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.main.split.items.ItemLinesSplit
import com.example.igor.restaurantmobile.presentation.main.split.items.ItemLinesSplitBinder
import com.example.igor.restaurantmobile.presentation.main.split.items.ItemLinesSplitDelegate
import com.example.igor.restaurantmobile.presentation.main.split.items.LineItemModel
import com.example.igor.restaurantmobile.presentation.preview_order.viewmodel.NewOrderViewModel
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.igor.restaurantmobile.common.delegates.CompositeAdapter

@AndroidEntryPoint
class SplitBillFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager

    val viewModel by viewModels<NewOrderViewModel>()
    val progressDialog by lazy { ProgressDialog(requireContext()) }

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemLinesSplitDelegate { item ->
//                listOfMappedItems.find { it.Uid == item.Uid }?.let {
//                    Log.e("TAG", "Item changed: OLD -> $it ; NEW -> $item", )
//                    it.isChecked = item.isChecked
//                    it.Count = item.Count
//                }
            })

            .build()
    }

    val binding by lazy {
        FragmentSplitBillBinding.inflate(LayoutInflater.from(context))
    }

    private val listOfMappedItems = mutableListOf<LineItemModel>()

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
                        price = AssortmentController.getAssortmentById(it.AssortimentUid)?.Price ?: 0.0
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
                    progressDialog.setMessage("Va rugam asteptati")
                    progressDialog.show()
                    bill?.let {
                        viewModel.splitBill(bill, listFiltered)
                    }
                }

            }else{
                Toast.makeText(context, "Selectati cel putin un produs!", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch(Dispatchers.Main){
            viewModel.splitBillResult.collectLatest {
                progressDialog.dismiss()
                when (it.Result) {
                    0 -> {
                        Toast.makeText(requireContext(),"Contul a fost salvat!",Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    -9 -> {
                        dialogShow("Eroare salvare contului", it.ResultMessage)
                    }
                    else -> {
                        dialogShow("Eroare salvare contului",ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(it.Result)))
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

        toolbar.setTitle("Impartirea contului")
        toolbar.setSubTitle("Selectati produse care urmeaza adaugate in cont nou")
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
            DialogAction(it, title, description, "OK", "Renunta", {
                it.dismiss()
            }, {

            }).show()
        }
    }
}