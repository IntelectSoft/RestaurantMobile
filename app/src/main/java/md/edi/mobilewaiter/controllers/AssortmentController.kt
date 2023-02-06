package md.edi.mobilewaiter.controllers

import android.util.Log
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.data.remote.response.assortment.AssortmentItem
import md.edi.mobilewaiter.data.remote.response.assortment.AssortmentListResponse
import md.edi.mobilewaiter.data.remote.response.assortment.ClosureTypeItem
import md.edi.mobilewaiter.data.remote.response.assortment.PrinterItem
import md.edi.mobilewaiter.presentation.table.items.ItemTable
import md.edi.mobilewaiter.presentation.table.items.ItemTableBinder


object AssortmentController {
    private var assortmentBody: AssortmentListResponse? = null

    fun getClosureTypes(): List<ClosureTypeItem> = assortmentBody?.ClosureTypeList ?: emptyList()
    fun getPrinters(): List<PrinterItem> = assortmentBody?.PrintersList ?: emptyList()

    fun setAssortmentResponse(response: AssortmentListResponse) {
        val assortment = response.AssortimentList?.toMutableList()

        assortment?.sortWith { x, y ->
            val compareResult = y.IsFolder.compareTo(x.IsFolder)
            if (compareResult != 0) {
                compareResult
            } else {
                x.Name.compareTo(y.Name)
            }
        }
        response.AssortimentList = assortment?.toList()

        assortmentBody = response
    }

    fun getAssortmentNameById(id: String) =
        assortmentBody?.AssortimentList?.find { it.Uid == id }?.Name ?: "Not found"

    fun getAssortmentById(id: String) =
        assortmentBody?.AssortimentList?.find { it.Uid == id }

    fun getParentsDefault(): List<AssortmentItem> {
        val mutableList = assortmentBody?.AssortimentList?.let {
            it.toMutableList().filter { item ->
                item.ParentUid == "00000000-0000-0000-0000-000000000000"
            }
        }
        return mutableList ?: emptyList()
    }

    fun getChildrenByParentId(parentId: String): List<AssortmentItem> {
        val mutableList = assortmentBody?.AssortimentList?.let {
            it.toMutableList().filter { item ->
                item.ParentUid == parentId
            }
        }
        return mutableList ?: emptyList()
    }

    fun searchAssortmentByName(searchText: String): List<AssortmentItem> {
        val mutableList = assortmentBody?.AssortimentList?.let {
            it.toMutableList().filter { item ->
                item.Name.lowercase().contains(searchText.lowercase())
            }
        }
        return mutableList ?: emptyList()
    }

    //function for tables
    fun getTableNumberById(tableId: String): String {
        return assortmentBody?.TableList?.let {
            it.toMutableList().find {
                it.Uid == tableId
            }
        }?.Name ?: "-"
    }

    fun getTablesDelegate(): List<DelegateAdapterItem> {
        val tables = mutableListOf<DelegateAdapterItem>()
        val listOccupiedTables = BillsController.getOccupiedTablesOfBills()
        assortmentBody?.TableList?.toMutableList()?.forEach { table ->
            val listBillsFromTable = BillsController.getBillsFromTable(table.Uid)
            val findedTable = listOccupiedTables.find { it == table.Uid }
            if (listBillsFromTable.isNullOrEmpty()) {
                tables.add(
                    ItemTableBinder(
                        ItemTable(
                            tag = "table",
                            id = table.Uid,
                            name = table.Name,
                            sum = 0.0,
                            guests = 0,
                            isOccupied = findedTable != null
                        )
                    )
                )
            } else {
                var sumTotal = 0.0
                var guests = 0
                listBillsFromTable.forEach { bill ->
                    sumTotal += bill.SumAfterDiscount
                    guests += bill.Guests
                }
                tables.add(
                    ItemTableBinder(
                        ItemTable(
                            tag = "table",
                            id = table.Uid,
                            name = table.Name,
                            sum = sumTotal,
                            guests = guests,
                            isOccupied = true
                        )
                    )
                )
            }
        }

        return tables
    }

    //Function for comments
    fun getCommentById(id: String) = assortmentBody?.CommentsList?.find { it.Uid == id }

}