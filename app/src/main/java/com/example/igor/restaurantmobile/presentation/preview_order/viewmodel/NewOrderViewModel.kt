package com.example.igor.restaurantmobile.presentation.preview_order.viewmodel

import androidx.lifecycle.ViewModel
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrdersModel
import com.example.igor.restaurantmobile.data.remote.models.bill.OrderItemModel
import com.example.igor.restaurantmobile.data.remote.models.bill.SplitBillModel
import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.data.remote.response.bills.BillListResponse
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.presentation.preview_order.items.ItemOrder
import com.example.igor.restaurantmobile.presentation.preview_order.items.ItemOrderBinder
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.presentation.main.split.items.LineItemModel
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.DeviceInfo
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.Urls
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val splitBillResult = MutableSharedFlow<BillListResponse>()
    val addBillResult = MutableSharedFlow<BillListResponse>()

    fun getOrderLinesAssortment(): MutableList<DelegateAdapterItem> {
        val lines = mutableListOf<DelegateAdapterItem>()
        CreateBillController.orderModel.Orders.toMutableList()
            .forEach {
                lines.add(
                    ItemOrderBinder(
                        ItemOrder(
                            tag = "order_line",
                            id = it.assortimentUid ?: "",
                            name = AssortmentController.getAssortmentNameById(
                                it.assortimentUid ?: ""
                            ),
                            line = it
                        )
                    )
                )
            }
        return lines
    }

    suspend fun saveNewOrder() {
        val localOrder = CreateBillController.orderModel
        val remoteOrder = AddOrdersModel()
        remoteOrder.DeviceId = DeviceInfo.deviceId
        remoteOrder.TableUid = localOrder.TableUid
        remoteOrder.BillUid = localOrder.BillUid
        remoteOrder.Guests = localOrder.Guests

        remoteOrder.Orders = localOrder.Orders.map {
            OrderItemModel(
                AssortimentUid = it.assortimentUid,
                Count = it.count,
                QueueNumber = it.numberPrepare,
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
                response.body()?.let { addBillResult.emit(it) }
            } else {
                addBillResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )

            }
        } catch (e: Exception) {
            addBillResult.emit(
                BillListResponse(
                    Result = -9,
                    ResultMessage = e.message,
                    BillsList = emptyList()
                )
            )
        }
    }

    suspend fun splitBill(localOrder: BillItem, list: List<LineItemModel>) {
        val remoteOrder = SplitBillModel()
        remoteOrder.DeviceId = DeviceInfo.deviceId
        remoteOrder.TableUid = localOrder.TableUid
        remoteOrder.BillUid = localOrder.Uid

        remoteOrder.Orders = list.map {
            OrderItemModel(
                AssortimentUid = it.AssortimentUid,
                Count = it.Count,
                QueueNumber = it.QueueNumber,
                PriceLineUid = it.PriceLineUid,
                Uid = it.Uid,
                KitUid = it.KitUid,
                Comments = it.Comments,
                Sum = it.Sum,
                SumAfterDiscount = it.SumAfterDiscount,
            )
        }


        try {
            val response = serviceRepo.splitBill(Urls.SplitBill, remoteOrder)
            if (response.isSuccessful) {
                response.body()?.let { splitBillResult.emit(it) }
            } else {
                splitBillResult.emit(
                    BillListResponse(
                        Result = -9,
                        ResultMessage = response.errorBody()?.string(),
                        BillsList = emptyList()
                    )
                )

            }
        } catch (e: Exception) {
            splitBillResult.emit(
                BillListResponse(
                    Result = -9,
                    ResultMessage = e.message,
                    BillsList = emptyList()
                )
            )
        }
    }
}