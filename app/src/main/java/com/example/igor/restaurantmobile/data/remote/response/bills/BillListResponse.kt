package com.example.igor.restaurantmobile.data.remote.response.bills

data class BillListResponse(
    var BillsList: List<BillItem>,
    var Result: Int,
    var ResultMessage: String?
)
