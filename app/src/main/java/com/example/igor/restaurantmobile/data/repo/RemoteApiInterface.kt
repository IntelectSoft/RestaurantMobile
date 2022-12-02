package com.example.igor.restaurantmobile.data.repo

import com.example.igor.restaurantmobile.data.remote.models.RegisterDeviceModel
import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrders
import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrdersModel
import com.example.igor.restaurantmobile.data.remote.models.bill.SplitBillModel
import com.example.igor.restaurantmobile.data.remote.models.license.GetUriModel
import com.example.igor.restaurantmobile.data.remote.models.license.RegisterModel
import com.example.igor.restaurantmobile.data.remote.models.license.response.RegisterResponse
import com.example.igor.restaurantmobile.data.remote.response.assortment.AssortmentListResponse
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import com.example.igor.restaurantmobile.data.remote.response.terminal.RegisterTerminalResponse
import retrofit2.Response
import retrofit2.http.*

interface RemoteApiInterface {

    @GET("")
    suspend fun ping(@Url url: String): Response<Boolean>

    @POST("")
    suspend fun registerTerminal(
        @Url url: String,
        @Body deviceInfo : RegisterDeviceModel): Response<RegisterTerminalResponse>

    @GET("")
    suspend fun getAssortmentList(
        @Url url: String,
        @Query("deviceId") deviceId: String,
        @Query("withImages") withImage: Boolean
    ): Response<AssortmentListResponse>

    @GET("")
    suspend fun getBillList(
        @Url url: String,
        @Query("deviceId") deviceId: String,
        @Query("includeLines") includeLines: Boolean
    ): Response<BillListResponse>

    @GET("")
    suspend fun getBill(
        @Url url: String,
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String
    ): BillListResponse

    @GET("")
    suspend fun closeBill(
        @Url url: String,
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String,
        @Query("closeTypeUid") closeTypeUid: String,
        @Query("printerUid") printerId: String?
    ): Response<BillListResponse>

    @POST("")
    suspend fun addOrders(
        @Url url: String,
        @Body orders: AddOrdersModel
    ): Response<BillListResponse>

    @POST("")
    suspend fun splitBill(
        @Url url: String,
        @Body order: SplitBillModel
    ): Response<BillListResponse>

    @GET("")
    suspend fun printBill(
        @Url url: String,
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String,
        @Query("printerUid") printerId: String?
    ): Response<BillListResponse>

    @GET("")
    suspend fun applyCard(
        @Url url: String,
        @Query("deviceId") deviceId: String,
        @Query("billUid") billUid: String,
        @Query("cardNumber") cardNumber: String
    ): Response<BillListResponse>

    @POST("") //https://dev.edi.md/ISLicenseService/json/RegisterApplication
    suspend fun registerApplication(@Url url: String, @Body bodyRegisterApp: RegisterModel): Response<RegisterResponse>

    @POST("") //https://dev.edi.md/ISLicenseService/json/GetURI
    suspend fun getURICall(@Url url: String,@Body sendGetURI: GetUriModel): Response<RegisterResponse>

//    @POST("") //https://dev.edi.md/ISLicenseService/json/UpdateDiagnosticInformation
//    suspend fun updateDiagnosticInfo(@Url url: String,@Body informationData: InformationData): Response<ErrorMessage>
}