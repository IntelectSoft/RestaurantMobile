package com.example.igor.restaurantmobile.data.remote.response.bills

data class BillListResponse(
    val BillsList: List<BillItem>,
    val Result: Int,
    val ResultMessage: String
)
