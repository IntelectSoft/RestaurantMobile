package com.example.igor.restaurantmobile.data.remote.models.bill

data class OrderItem(
    var AssortimentUid: String,
    var PriceLineUid: String,
    var Count: Double,
    var Comments: List<String>? = emptyList()
)
