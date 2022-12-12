package com.example.igor.restaurantmobile.presentation.table

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.App
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.databinding.FragmentTableListBinding
import com.example.igor.restaurantmobile.presentation.table.items.ItemTableDelegate
import dagger.hilt.android.AndroidEntryPoint
import com.example.igor.restaurantmobile.common.decor.GridSpanDecoration
import com.example.igor.restaurantmobile.common.delegates.CompositeAdapter
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.dialog.DialogActionInputText

@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemTableDelegate { id, name, guests, sum ->
                if (guests == 0 && sum == 0.0) {
                    dialogShowEnterGuest("Introduceti numarul de oaspeti pentru masa $name", tableId = id)
                } else {
                    dialogShowAlertTableBusy(
                        "Pe masa $name exista conturi!",
                        "Sunteti sigur ca doriti sa continuati?",
                        id,
                        name
                    )
                }
            })
            .build()
    }

    val binding by lazy {
        FragmentTableListBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val tables = AssortmentController.getTablesDelegate()
        if (tables.isEmpty()) {
            App.instance.navigateToAnimatedPopBackStack(
                findNavController(),
                TableFragmentDirections.tableToAssortment(),
                R.id.tableToAssortment,
                true
            )
        } else {
            initList(tables)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar

        toolbar.setTitle("Alege masa clientului")
        toolbar.showBottomLine(true)

        toolbar.showLeftBtn(true)
        toolbar.setLeftClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initList(items: List<DelegateAdapterItem>) {
        binding.viewTable.adapter = compositeAdapter

        layoutManager = GridLayoutManager(context, 3)
        binding.viewTable.layoutManager = layoutManager

        val deco = GridSpanDecoration(
            binding.root.context.resources.getDimensionPixelSize(
                com.intuit.sdp.R.dimen._4sdp
            ),
            true
        )
        while (binding.viewTable.itemDecorationCount > 0) {
            binding.viewTable.removeItemDecorationAt(0)
        }
        binding.viewTable.addItemDecoration(deco)

        compositeAdapter.submitList(items)
    }

    private fun dialogShowAlertTableBusy(
        title: String,
        description: String,
        tableId: String,
        name: String
    ) {
        requireContext().let {
            DialogAction(it, title, description, "Da", "Renunta", {
                it.dismiss()
                dialogShowEnterGuest("Introduceti numarul de oaspeti pentru masa $name", tableId = tableId)
            }, {
                it.dismiss()
            }).show()
        }
    }

    private fun dialogShowEnterGuest(title: String, description: String? = null, tableId: String) {
        requireContext().let {
            DialogActionInputText(it, title, description, "Continua", "Renunta", { dialog, text ->
                dialog.dismiss()

                CreateBillController.setTableId(tableId)
                CreateBillController.setTableGuests(if(text.toInt() == 0) 1 else text.toInt())

                App.instance.navigateToAnimatedPopBackStack(
                    findNavController(),
                    TableFragmentDirections.tableToAssortment(),
                    R.id.tableToAssortment,
                    true
                )
            }, {
                it.dismiss()
            }).show()
        }
    }

}