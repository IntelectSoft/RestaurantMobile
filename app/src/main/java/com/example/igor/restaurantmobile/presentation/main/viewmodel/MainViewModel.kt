package com.example.igor.restaurantmobile.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.presentation.main.items.ItemBill
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillBinder
import com.example.igor.restaurantmobile.utils.DeviceInfo
import com.example.igor.restaurantmobile.utils.Urls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    val pingFlow = MutableSharedFlow<Boolean>()

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
                    BillsController.setBillResponse(it)
                    initData(BillsController.billsBody)
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

    private suspend fun initData(billsList: List<BillItem>?) {
        val bills = mutableListOf<DelegateAdapterItem>()

        billsList?.map {
            bills.add(
                ItemBillBinder(
                    ItemBill(
                        tag = "", bill = it
                    )
                )
            )
        }

        billListResponse.emit(bills)
    }

}