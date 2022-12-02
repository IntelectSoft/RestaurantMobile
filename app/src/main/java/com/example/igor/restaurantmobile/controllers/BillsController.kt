package com.example.igor.restaurantmobile.controllers

import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import com.example.igor.restaurantmobile.data.remote.response.bills.LineItem


object BillsController {
    var billsBody: List<BillItem>? = null

    fun setBillResponse(response: BillListResponse) {
        val bills = response.BillsList.toMutableList()

        bills.sortWith { x, y ->
            x.Number.compareTo(y.Number)
        }
        billsBody = bills.toList()
    }

    fun getBillLines(id: String): List<LineItem> {
        val mutableList = billsBody?.let {
            it.toMutableList().find { item ->
                item.Uid == id
            }
        }
        return mutableList?.Lines ?: emptyList()
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
}