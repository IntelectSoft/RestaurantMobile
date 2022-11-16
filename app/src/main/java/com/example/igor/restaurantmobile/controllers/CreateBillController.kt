package com.example.igor.restaurantmobile.controllers

import com.example.igor.restaurantmobile.data.remote.models.bill.AddOrdersModels
import com.example.igor.restaurantmobile.data.remote.models.bill.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow


object CreateBillController {
    var orderModel = AddOrdersModels()
    var countCart = 0
    val countCartFlow = MutableStateFlow(0)

    fun clearAllData() {
        countCart = 0
        countCartFlow.value = 0
        orderModel = AddOrdersModels()
    }

    fun setTableId(id: String) {
        orderModel.TableUid = id
    }

    fun addAssortment(priceId: String, id: String, count: Int) {
        val orderItem = OrderItem(
            AssortimentUid = id,
            Count = count.toDouble(),
            PriceLineUid = priceId
        )
        val ordersList = orderModel.Orders.toMutableList()
        ordersList.add(orderItem)
        orderModel.Orders = ordersList
    }

     fun setCartCount(count: Int) {
        countCart += count
        countCartFlow.value = countCart
    }
}