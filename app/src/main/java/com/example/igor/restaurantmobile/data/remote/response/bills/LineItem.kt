package com.example.igor.restaurantmobile.data.remote.response.bills

data class LineItem(
    val AssortimentUid: String,
    val Comments: List<String>,
    val Count: Double,
    val KitUid: String,
    val PriceLineUid: String,
    val Sum: Double,
    val SumAfterDiscount: Double,
    val Uid: String
)