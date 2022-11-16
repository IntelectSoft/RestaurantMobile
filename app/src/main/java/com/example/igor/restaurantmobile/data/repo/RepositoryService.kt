package com.example.igor.restaurantmobile.data.repo

import android.media.Image
import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrdersModels
import com.example.igor.restaurantmobile.data.remote.models.bill.OrderItem
import com.example.igor.restaurantmobile.data.remote.response.assortment.AssortmentListResponse
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import com.example.igor.restaurantmobile.data.remote.response.terminal.RegisterTerminalResponse
import retrofit2.Response


interface RepositoryService {
    suspend fun ping(): Boolean
    suspend fun registerTerminal(deviceId: String): Response<RegisterTerminalResponse>
    suspend fun syncAssortment(deviceId: String, withImage: Boolean): Response<AssortmentListResponse>
    suspend fun getMyBills(deviceId: String) : Response<BillListResponse>
    suspend fun addNewBill(billModel : List<AddOrdersModels>): Response<BillListResponse>
}