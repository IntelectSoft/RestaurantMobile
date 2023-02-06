package md.edi.mobilewaiter.data.remote.response.bills

data class LineItem(
    val AssortimentUid: String,
    val Comments: List<String>? = emptyList(),
    val Count: Double,
    val QueueNumber: Int,
    val KitUid: String?,
    val PriceLineUid: String,
    val Sum: Double,
    val SumAfterDiscount: Double,
    val Uid: String
)