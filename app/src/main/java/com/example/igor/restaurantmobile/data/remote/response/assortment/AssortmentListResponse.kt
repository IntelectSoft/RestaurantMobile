package com.example.igor.restaurantmobile.data.remote.response.assortment

data class AssortmentListResponse(
    var AssortimentList: List<AssortmentItem>,
    val ClosureTypeList: List<ClosureTypeItem>,
    val CommentsList: List<CommentItem>,
    val Result: Int,
    val TableList: List<TableItem>
)
