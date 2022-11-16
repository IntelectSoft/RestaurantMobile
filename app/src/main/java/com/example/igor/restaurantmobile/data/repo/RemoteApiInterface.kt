package com.example.igor.restaurantmobile.data.repo

import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrdersModels
import com.example.igor.restaurantmobile.data.remote.response.terminal.RegisterTerminalResponse
import com.example.igor.restaurantmobile.data.remote.response.assortment.AssortmentListResponse
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteApiInterface {

    @GET("/MobileCash/json/Ping")
    suspend fun ping(): Boolean

    @GET("/MobileCash/json/RegisterTerminal")
    suspend fun registerTerminal(@Query("deviceId") deviceId: String): Response<RegisterTerminalResponse>

    @GET("/MobileCash/json/GetAssortimentList")
    suspend fun getAssortmentList(
        @Query("deviceId") deviceId: String,
        @Query("withImages") withImage: Boolean
    ): Response<AssortmentListResponse>

    @GET("/MobileCash/json/GetBillsList")
    suspend fun getBillList(
        @Query("deviceId") deviceId: String,
        @Query("includeLines") includeLines: Boolean
    ): Response<BillListResponse>

    @GET("/MobileCash/json/GetBill")
    suspend fun getBill(
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String
    ): BillListResponse

    @GET("/MobileCash/json/CloseBill")
    suspend fun closeBill(
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String,
        @Query("closeTypeUid") closeTypeUid: String
    ): Response<BillListResponse>

    @POST("/MobileCash/json/SaveBill")
    suspend fun addOrders(
        @Body orders: List<AddOrdersModels>
    ): Response<BillListResponse>

    @GET("/MobileCash/json/PrintBill")
    suspend fun printBill(
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String
    ): Response<BillListResponse>

    @GET("/MobileCash/json/ApplyCard")
    suspend fun applyCard(
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String,
        @Query("cardNumber") cardNumber: String
    ): Response<BillListResponse>

}