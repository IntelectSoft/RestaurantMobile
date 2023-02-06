package md.edi.mobilewaiter.presentation.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.App
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.databinding.FragmentTableListBinding
import md.edi.mobilewaiter.presentation.table.items.ItemTableDelegate
import dagger.hilt.android.AndroidEntryPoint
import md.edi.mobilewaiter.common.decor.GridSpanDecoration
import md.edi.mobilewaiter.common.delegates.CompositeAdapter
import md.edi.mobilewaiter.presentation.dialog.DialogAction
import md.edi.mobilewaiter.presentation.dialog.DialogActionInputText

@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager
    var billId: String? = null

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemTableDelegate { id, name, guests, sum, isOccupied ->
                if (billId == null || billId == "") {
                    if (!isOccupied) {
                        dialogShowEnterGuest(
                            "Introduceti numarul de oaspeti pentru masa $name",
                            tableId = id
                        )
                    } else {
                        dialogShowAlertTableBusy(
                            "Pe masa $name exista conturi!",
                            "Sunteti sigur ca doriti sa continuati?",
                            id,
                            name
                        )
                    }
                } else {
                    setFragmentResult("tableResult", bundleOf("tableId" to id))
                    findNavController().popBackStack()
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

        billId = arguments?.getString("billId")

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
                dialogShowEnterGuest(
                    "Introduceti numarul de oaspeti pentru masa $name",
                    tableId = tableId
                )
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
                CreateBillController.setTableGuests(if (text == "") 1 else if (text.toInt() == 0) 1 else text.toInt())

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