package com.example.igor.restaurantmobile.presentation.main.viewmodel

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.itemButton.ItemButton
import com.example.igor.restaurantmobile.common.itemButton.ItemButtonBinder
import com.example.igor.restaurantmobile.common.styles.Margin
import com.example.igor.restaurantmobile.common.styles.Text
import com.example.igor.restaurantmobile.common.styles.TextColor
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.presentation.main.items.ItemBill
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillBinder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val billListResponse = MutableStateFlow<List<DelegateAdapterItem>>(emptyList())

    suspend fun getMyBills() {
        val deviceId = settingsRepository.getDeviceId().first()

        val response = serviceRepo.getMyBills(deviceId)
        if (response.isSuccessful) {
            response.body()?.let {
                BillsController.billsBody = it.BillsList
                initData(it.BillsList)
            }
        }
    }

    private suspend fun initData(billsList: List<BillItem>) {
        val bills = mutableListOf<DelegateAdapterItem>()

        billsList.map {
            bills.add(
                ItemBillBinder(
                    ItemBill(
                        tag = "",
                        bill = it
                    )
                )
            )
        }

        billListResponse.emit(bills)
    }

}