package md.edi.mobilewaiter.data.remote.models.bill

data class OrderItem(
    var assortimentUid: String,
    var uId: String? = "00000000-0000-0000-0000-000000000000",
    var internUid: String = "00000000-0000-0000-0000-000000000000",
    var priceLineUid: String,
    var kitUid : String? = "00000000-0000-0000-0000-000000000000",
    var comments: List<String> = emptyList(),
    var numberPrepare: Int = 0,
    var count: Double,
    var price: Double,
    var sumAfterDiscount: Double,
    var sum: Double

)