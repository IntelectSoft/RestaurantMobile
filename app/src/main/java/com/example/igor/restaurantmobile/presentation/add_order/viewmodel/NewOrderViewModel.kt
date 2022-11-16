package com.example.igor.restaurantmobile.presentation.add_order.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.CreateBillController
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.presentation.add_order.NewOrderFragment
import com.example.igor.restaurantmobile.presentation.add_order.items.ItemOrder
import com.example.igor.restaurantmobile.presentation.add_order.items.ItemOrderBinder
import com.example.igor.restaurantmobile.presentation.dialog.DialogAction
import com.example.igor.restaurantmobile.utils.ContextManager
import com.example.igor.restaurantmobile.utils.ErrorHandler
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun getOrderLinesAssortment(): MutableList<DelegateAdapterItem> {
        val lines = mutableListOf<DelegateAdapterItem>()
        CreateBillController.orderModel.Orders.toMutableList()
            .forEach {
                lines.add(
                    ItemOrderBinder(
                        ItemOrder(
                            tag = "order_line",
                            id = it.AssortimentUid,
                            name = AssortmentController.getAssortmentNameById(it.AssortimentUid),
                            line = it
                        )
                    )
                )
            }
        return lines
    }

    suspend fun saveNewOrder() {
        val order = CreateBillController.orderModel
        val deviceId = settingsRepository.getDeviceId().first()
        order.DeviceId = deviceId

        try{
            val response = serviceRepo.addNewBill(listOf(order) )
            if (response.isSuccessful) {
                if(response.body()?.Result == 0){
                    CreateBillController.clearAllData()
                    dialogShow("Contul a fost salvat!","")
                }
                else{
                    dialogShow("Eroare salvare contului",ErrorHandler().getErrorMessage(EnumRemoteErrors.getByValue(response.body()?.Result!!)))
                }

            } else {
                dialogShow("Eroare salvare contului", response.message())
            }
        }catch (ex: Exception){
            ex.message?.let { dialogShow("Eroare salvarea contului", it) }
        }


    }

    private fun dialogShow(title: String, description: String) {
        ContextManager.retrieveContext()?.let {
            DialogAction(it, title, description, "Reincearca", "Renunta", {
                it.dismiss()

            }, {

            }).show()
        }
    }
}