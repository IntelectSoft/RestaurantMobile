package com.example.igor.restaurantmobile.data.remote.models.bill

data class AddOrdersModel(
    var DeviceId: String? = null,
    var BillUid: String = "00000000-0000-0000-0000-000000000000",
    var TableUid: String = "00000000-0000-0000-0000-000000000000",
    var Orders: List<OrderItemModel> = emptyList(),
    var Guests: Int? = 0
)
