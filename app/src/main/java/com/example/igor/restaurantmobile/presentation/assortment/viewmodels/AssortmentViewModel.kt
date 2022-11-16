package com.example.igor.restaurantmobile.presentation.assortment.viewmodels

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.common.itemButton.ItemButton
import com.example.igor.restaurantmobile.common.itemButton.ItemButtonBinder
import com.example.igor.restaurantmobile.common.styles.Margin
import com.example.igor.restaurantmobile.common.styles.Text
import com.example.igor.restaurantmobile.common.styles.TextColor
import com.example.igor.restaurantmobile.controllers.AssortmentController
import com.example.igor.restaurantmobile.controllers.BillsController
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.remote.response.bills.BillItem
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.presentation.assortment.items.ItemAssortment
import com.example.igor.restaurantmobile.presentation.assortment.items.ItemAssortmentBinder
import com.example.igor.restaurantmobile.presentation.main.items.ItemBill
import com.example.igor.restaurantmobile.presentation.main.items.ItemBillBinder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class AssortmentViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val assortmentList = MutableStateFlow<List<DelegateAdapterItem>>(emptyList())
    val assortmentChildList = MutableStateFlow<List<DelegateAdapterItem>>(emptyList())

    suspend fun getDefaultAssortment() {
        val assortment = mutableListOf<DelegateAdapterItem>()
        AssortmentController.getParentsDefault().toMutableList()
            .forEach {
                assortment.add(
                    ItemAssortmentBinder(
                        ItemAssortment(
                            tag = "assortment",
                            assortment = it
                        )
                    )
                )
            }

        assortmentList.emit(assortment)
    }

    suspend fun getChildAssortment(parentId: String) {
        val assortment = mutableListOf<DelegateAdapterItem>()
        AssortmentController.getChildrenByParentId(parentId).toMutableList()
            .forEach {
                assortment.add(
                    ItemAssortmentBinder(
                        ItemAssortment(
                            tag = "assortment",
                            assortment = it
                        )
                    )
                )
            }

        assortmentChildList.emit(assortment)
    }

}