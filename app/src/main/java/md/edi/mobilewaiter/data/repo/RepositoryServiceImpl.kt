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
import javax.inject.Inject

class RepositoryServiceImpl @Inject constructor(private val apiInterface: RemoteApiInterface) :
    RepositoryService {

    override suspend fun ping(url: String) = apiInterface.ping(url)

    override suspend fun registerTerminal(
        url: String,
        device: RegisterDeviceModel
    ): Response<RegisterTerminalResponse> {
        return apiInterface.registerTerminal(url, device)
    }

    override suspend fun syncAssortment(
        url: String,
        deviceId: String,
        withImage: Boolean
    ): Response<AssortmentListResponse> {
        return apiInterface.getAssortmentList(url, deviceId, withImage)
    }

    override suspend fun getMyBills(url: String, deviceId: String): Response<BillListResponse> {
        return apiInterface.getBillList(url, deviceId, true)
    }

    override suspend fun getBill(
        url: String,
        deviceId: String,
        billId: String
    ): Response<BillListResponse> {
        return apiInterface.getBill(url, deviceId, billId)
    }

    override suspend fun printBill(
        url: String,
        deviceId: String,
        billUid: String,
        printerId: String?
    ): Response<BillListResponse> {
        return apiInterface.printBill(url, deviceId, billUid, printerId)
    }

    override suspend fun applyCard(
        url: String,
        deviceId: String,
        billUid: String,
        cardNumber: String
    ): Response<BillListResponse> {
        return apiInterface.applyCard(url, deviceId, billUid, cardNumber)
    }

    override suspend fun closeBill(
        url: String,
        deviceId: String,
        billUid: String,
        closeTypeUid: String,
        printerId: String?
    ): Response<BillListResponse> {
        return apiInterface.closeBill(url, deviceId, billUid, closeTypeUid, printerId)
    }

    override suspend fun addNewBill(
        url: String,
        billModel: AddOrdersModel
    ): Response<BillListResponse> {
        return apiInterface.addOrders(url, billModel)
    }

    override suspend fun splitBill(url: String, bill: SplitBillModel): Response<BillListResponse> {
        return apiInterface.splitBill(url, bill)
    }

    override suspend fun combineBills(
        url: String,
        bill: CombineBillsModel
    ): Response<BillListResponse> {
        return apiInterface.combineBills(url, bill)
    }

    //ISLicense service methods
    override suspend fun registerApplication(
        url: String,
        bodyRegisterApp: RegisterModel
    ): Response<RegisterResponse> {
        return apiInterface.registerApplication(url, bodyRegisterApp)
    }

    override suspend fun getUri(url: String, sendGetURI: GetUriModel): Response<RegisterResponse> {
        return apiInterface.getURICall(url, sendGetURI)
    }
}