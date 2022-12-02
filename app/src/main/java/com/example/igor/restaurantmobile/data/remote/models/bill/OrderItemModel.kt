package com.example.igor.restaurantmobile.data.remote.models.bill

data class OrderItemModel(
    var AssortimentUid: String,
    var Uid: String? = "00000000-0000-0000-0000-000000000000",
    var PriceLineUid: String,
    var KitUid : String? = "00000000-0000-0000-0000-000000000000",
    var Comments: List<String>? = emptyList(),
    var Count: Double,
    var QueueNumber: Int? = null,
    var SumAfterDiscount: Double,
    var Sum: Double
)