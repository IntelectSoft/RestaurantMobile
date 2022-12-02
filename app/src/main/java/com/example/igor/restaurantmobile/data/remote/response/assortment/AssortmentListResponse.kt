package com.example.igor.restaurantmobile.data.remote.response.assortment

data class AssortmentListResponse(
    var AssortimentList: List<AssortmentItem>? = emptyList(),
    var ClosureTypeList: List<ClosureTypeItem>? = emptyList(),
    val CommentsList: List<CommentItem>? = emptyList(),
    val TableList: List<TableItem>? = emptyList(),
    val PrintersList: List<PrinterItem>? = emptyList(),
    val Result: Int,
    var errorMessage: String? = null
)
