package md.edi.mobilewaiter.controllers

import md.edi.mobilewaiter.data.remote.response.bills.BillItem
import md.edi.mobilewaiter.data.remote.response.bills.BillListResponse
import md.edi.mobilewaiter.data.remote.response.bills.LineItem
import okhttp3.internal.toImmutableList


object BillsController {
    var billsBody: List<BillItem>? = null
    var occupiedTables: List<String> = emptyList()

    fun setBillResponse(response: BillListResponse) {
        val bills = response.BillsList.toMutableList()

        bills.sortWith { x, y ->
            y.Number.compareTo(x.Number)
        }
        billsBody = bills.toList()
        occupiedTables = response.OccupiedTables.toMutableList()
    }

    fun changeTableForBill(bilId: String, tableId:String) {
       billsBody?.find {it.Uid == bilId }?.let {
           it.TableUid = tableId
       }

    }

    fun getBillById(id: String): BillItem? {
        val mutableList = billsBody?.let {
            it.toMutableList().find { item ->
                item.Uid == id
            }
        }
        return mutableList
    }

    fun getBillsFromTable(id: String): List<BillItem>? {
        val mutableList = billsBody?.let {
            it.toMutableList().filter { item ->
                item.TableUid == id
            }
        }
        return mutableList
    }

    fun getOccupiedTablesOfBills(): List<String> {
        return occupiedTables
    }
}