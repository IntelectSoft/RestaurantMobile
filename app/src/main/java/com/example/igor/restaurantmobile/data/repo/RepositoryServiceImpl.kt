package com.example.igor.restaurantmobile.data.repo

import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrdersModels
import com.example.igor.restaurantmobile.data.remote.response.assortment.AssortmentListResponse
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import com.example.igor.restaurantmobile.data.remote.response.terminal.RegisterTerminalResponse
import retrofit2.Response
import javax.inject.Inject

class RepositoryServiceImpl @Inject constructor(private val apiInterface: RemoteApiInterface) :
    RepositoryService {

    override suspend fun ping() = apiInterface.ping()

    override suspend fun registerTerminal(deviceId: String): Response<RegisterTerminalResponse> {
        return apiInterface.registerTerminal(deviceId)
    }

    override suspend fun syncAssortment(deviceId: String, withImage: Boolean): Response<AssortmentListResponse> {
        return apiInterface.getAssortmentList(deviceId, withImage)
    }

    override suspend fun getMyBills(deviceId: String): Response<BillListResponse> {
        return apiInterface.getBillList(deviceId, true)
    }

    override suspend fun addNewBill(billModel: List<AddOrdersModels>): Response<BillListResponse> {
        return apiInterface.addOrders(billModel)
    }
}