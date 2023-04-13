package md.edi.mobilewaiter.presentation.preview_order.viewmodel

import androidx.lifecycle.ViewModel
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.controllers.CreateBillController
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.data.remote.models.bill.AddOrdersModel
import md.edi.mobilewaiter.data.remote.models.bill.OrderItemModel
import md.edi.mobilewaiter.data.remote.models.bill.SplitBillModel
import md.edi.mobilewaiter.data.remote.response.bills.BillItem
import md.edi.mobilewaiter.data.remote.response.bills.BillListResponse
import md.edi.mobilewaiter.data.repo.RepositoryServiceImpl
import md.edi.mobilewaiter.presentation.preview_order.items.ItemOrder
import md.edi.mobilewaiter.presentation.preview_order.items.ItemOrderBinder
import md.edi.mobilewaiter.presentation.main.split.items.LineItemModel
import md.edi.mobilewaiter.utils.DeviceInfo
import md.edi.mobilewaiter.utils.Urls
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
        val adapter = mutableListOf<DelegateAdapterItem>()
        val lines = CreateBillController.orderModel.Orders.toMutableList()

        lines.map {
            adapter.add(
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

//        val filteredList = lines.filter { it.kitUid == "00000000-0000-0000-0000-000000000000" }
//
//        filteredList.forEach { line ->
//            val kitLines = lines?.filter { it.kitUid == line.Uid }
//            adapter.add(
//                ItemOrderBinder(
//                    ItemOrder(
//                        tag = "order_line",
//                        id = it.assortimentUid ?: "",
//                        name = AssortmentController.getAssortmentNameById(
//                            it.assortimentUid ?: ""
//                        ),
//                        line = it
//                    )
//                )
//            )
//
//            kitLines?.let {
//                it.forEachIndexed { index, kitLine ->
//                    adapter.add(
//                        ItemKitLineBinder(
//                            ItemKitLine(
//                                tag = "kitLine",
//                                line = kitLine,
//                                isLast = (index + 1) == it.size
//                            )
//                        )
//                    )
//                }
//            }
//        }

        return adapter
    }

    suspend fun saveNewOrder() {
        val localOrder = CreateBillController.orderModel
        val remoteOrder = AddOrdersModel()
        remoteOrder.DeviceId = DeviceInfo.deviceId
        remoteOrder.TableUid = localOrder.TableUid
        remoteOrder.BillUid = localOrder.BillUid
        remoteOrder.Guests = localOrder.Guests

        remoteOrder.Orders =
            localOrder.Orders.filter { it.internUid != "00000000-0000-0000-0000-000000000000" }
                .map {
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