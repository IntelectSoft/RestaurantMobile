package md.edi.mobilewaiter.data.repo

import md.edi.mobilewaiter.data.remote.models.RegisterDeviceModel
import md.edi.mobilewaiter.data.remote.models.bill.AddOrdersModel
import md.edi.mobilewaiter.data.remote.models.bill.CombineBillsModel
import md.edi.mobilewaiter.data.remote.models.bill.SplitBillModel
import md.edi.mobilewaiter.data.remote.models.license.GetUriModel
import md.edi.mobilewaiter.data.remote.models.license.RegisterModel
import md.edi.mobilewaiter.data.remote.models.license.response.RegisterResponse
import md.edi.mobilewaiter.data.remote.response.assortment.AssortmentListResponse
import md.edi.mobilewaiter.data.remote.response.bills.BillListResponse
import md.edi.mobilewaiter.data.remote.response.terminal.RegisterTerminalResponse
import retrofit2.Response


interface RepositoryService {
    suspend fun ping(url: String): Response<Boolean>
    suspend fun registerTerminal(url: String, device: RegisterDeviceModel): Response<RegisterTerminalResponse>
    suspend fun syncAssortment(url: String, deviceId: String, withImage: Boolean): Response<AssortmentListResponse>
    suspend fun getMyBills(url: String, deviceId: String) : Response<BillListResponse>
    suspend fun addNewBill(url: String, billModel : AddOrdersModel): Response<BillListResponse>
    suspend fun printBill(url: String, deviceId: String, billUid: String, printerId: String?): Response<BillListResponse>
    suspend fun applyCard(url: String,deviceId: String,billUid: String, cardNumber: String): Response<BillListResponse>
    suspend fun closeBill(url: String,deviceId: String,billUid: String, closeTypeUid: String,printerId: String?): Response<BillListResponse>
    suspend fun splitBill(url: String, ordersModel: SplitBillModel): Response<BillListResponse>
    suspend fun combineBills(url: String, model: CombineBillsModel): Response<BillListResponse>
    suspend fun registerApplication(url: String,bodyRegisterApp: RegisterModel): Response<RegisterResponse>
    suspend fun getUri(url: String,sendGetURI: GetUriModel): Response<RegisterResponse>
}