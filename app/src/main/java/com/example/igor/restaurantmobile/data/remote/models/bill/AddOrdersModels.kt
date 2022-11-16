package com.example.igor.restaurantmobile.data.remote.models.bill

data class AddOrdersModels(
    var DeviceId: String? = null,
    var BillUid: String? = null,
    var TableUid: String? = null,
    var Orders: List<OrderItem> = emptyList()
)
