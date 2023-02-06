package md.edi.mobilewaiter.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.data.remote.models.bill.CombineBillsModel
import md.edi.mobilewaiter.data.remote.response.bills.BillItem
import md.edi.mobilewaiter.data.remote.response.bills.BillListResponse
import md.edi.mobilewaiter.data.repo.RepositoryServiceImpl
import md.edi.mobilewaiter.presentation.main.items.ItemBill
import md.edi.mobilewaiter.presentation.main.items.ItemBillBinder
import md.edi.mobilewaiter.utils.DeviceInfo
import md.edi.mobilewaiter.utils.Urls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.data.remote.models.bill.AddOrdersModel
import md.edi.mobilewaiter.data.remote.models.bill.OrderItemModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val billListResponse = MutableStateFlow<List<DelegateAdapterItem>>(emptyList())
    val getBillListResult = MutableSharedFlow<BillListResponse>()
    val closeBillResult = MutableSharedFlow<BillListResponse>()
    val billPrintResult = MutableSharedFlow<BillListResponse>()
    val applyCardResult = MutableSharedFlow<BillListResponse>()
    val combineBillsResult = MutableSharedFlow<BillListResponse>()
    val pingFlow = MutableSharedFlow<Boolean>()
    val changeTableResult = MutableSharedFlow<BillListResponse>()

    val arrayCombine = arrayListOf<String>()

    suspend fun ping(){
        try {
            val response = serviceRepo.ping(Urls.Ping)
            if (response.isSuccessful) {
                response.body()?.let {
                   pingFlow.emit(it)
                }
            } else {
                pingFlow.emit(false)
            }
        } catch (e: Exception) {
            pingFlow.emit(false)
        }
    }


    suspend fun getMyBills() {
        try {
            val response = serviceRepo.getMyBills(Urls.GetBillsList, DeviceInfo.deviceId)
            if (response.isSuccessful) {
                response.body()?.let {
                    getBillListResult.emit(it)
                }
            } else {
                getBillListResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )
            }
        } catch (e: Exception) {
            getBillListResult.emit(
                BillListResponse(
                    Result = -9, ResultMessage = e.message, BillsList = emptyList()
                )
            )
        }

    }

    suspend fun printBill(billId: String, printerId: String?) {
        try {
            val response = serviceRepo.printBill(Urls.PrintBill, DeviceInfo.deviceId, billId, printerId)
            if (response.isSuccessful) {
                response.body()?.let { billPrintResult.emit(it) }
            } else {
                billPrintResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )
            }
        } catch (e: Exception) {
            billPrintResult.emit(
                BillListResponse(
                    Result = -9, ResultMessage = e.message, BillsList = emptyList()
                )
            )
        }

    }

    suspend fun closeBill(billId: String, closureUid: String, printerId: String?) {
        try {
            val response = serviceRepo.closeBill(
                Urls.CloseBill, DeviceInfo.deviceId, billId, closureUid, printerId
            )
            if (response.isSuccessful) {
                response.body()?.let { closeBillResult.emit(it) }
            } else {
                closeBillResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )

            }
        } catch (e: Exception) {
            closeBillResult.emit(
                BillListResponse(
                    Result = -9, ResultMessage = e.message, BillsList = emptyList()
                )
            )
        }
    }

    suspend fun combineBill() {
        val combineModel = CombineBillsModel(
            DeviceId = DeviceInfo.deviceId,
            MainBillUid = arrayCombine[1],
            AttachedBillUid = arrayCombine[0]
        )
        try {
            val response = serviceRepo.combineBills(
                Urls.CombineBills, combineModel
            )
            if (response.isSuccessful) {
                response.body()?.let { combineBillsResult.emit(it) }
            } else {
                combineBillsResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )

            }
        } catch (e: Exception) {
            combineBillsResult.emit(
                BillListResponse(
                    Result = -9, ResultMessage = e.message, BillsList = emptyList()
                )
            )
        }
    }

    suspend fun applyCard(billId: String, cardNumber: String) {
        try {
            val response = serviceRepo.applyCard(
                Urls.ApplyCard, DeviceInfo.deviceId, billId, cardNumber
            )
            if (response.isSuccessful) {
                response.body()?.let { applyCardResult.emit(it) }
            } else {
                applyCardResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )

            }
        } catch (e: Exception) {
            applyCardResult.emit(
                BillListResponse(
                    Result = -9, ResultMessage = e.message, BillsList = emptyList()
                )
            )
        }
    }

    suspend fun initData(billsList: List<BillItem>?, excludeUid: String? = null) {
        val bills = mutableListOf<DelegateAdapterItem>()
        if(excludeUid != null){
            billsList?.filter { it.Uid != excludeUid }?.map {
                bills.add(
                    ItemBillBinder(
                        ItemBill(
                            tag = "",
                            bill = it
                        )
                    )
                )
            }
        }
        else{
            billsList?.map {
                bills.add(
                    ItemBillBinder(
                        ItemBill(
                            tag = "",
                            bill = it
                        )
                    )
                )
            }
        }



        billListResponse.emit(bills)
    }

    suspend fun changeTableOrder(tableId: String) {
        val localOrder = CreateBillController.orderModel
        val remoteOrder = AddOrdersModel()
        remoteOrder.DeviceId = DeviceInfo.deviceId
        remoteOrder.TableUid = tableId
        remoteOrder.BillUid = localOrder.BillUid
        remoteOrder.Guests = localOrder.Guests

        remoteOrder.Orders = localOrder.Orders.filter { it.internUid != "00000000-0000-0000-0000-000000000000" }.map {
            OrderItemModel(
                AssortimentUid = it.assortimentUid,
                Count = it.count,
                QueueNumber = it.numberPrepare ?: 1,
                PriceLineUid = it.priceLineUid,
                Uid = it.uId,
                KitUid = it.kitUid,
                Comments = it.comments,
                Sum = it.sum,
                SumAfterDiscount = it.sumAfterDiscount,
            )
        }

        try {
            val response = serviceRepo.addNewBill(Urls.SaveBill, remoteOrder)
            if (response.isSuccessful) {
                response.body()?.let { changeTableResult.emit(it) }
            } else {
                changeTableResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )

            }
        } catch (e: Exception) {
            changeTableResult.emit(
                BillListResponse(
                    Result = -9,
                    ResultMessage = e.message,
                    BillsList = emptyList()
                )
            )
        }
    }

}