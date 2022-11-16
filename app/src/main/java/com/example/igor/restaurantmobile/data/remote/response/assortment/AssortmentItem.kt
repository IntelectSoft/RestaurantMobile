package com.example.igor.restaurantmobile.data.remote.response.assortment

data class AssortmentItem(
    val AllowNonIntegerSale: Boolean,
    val AllowSaleOnlyAsKitMember: Boolean,
    val Comments: List<String>,
    val Image: List<Byte>,
    val IsFolder: Boolean,
    val KitMembers: List<KitMemberItem>,
    val MandatoryComment: Boolean,
    val Name: String,
    val ParentUid: String,
    val Price: Double,
    val PricelineUid: String,
    val Uid: String
)
