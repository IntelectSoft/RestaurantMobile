package md.edi.mobilewaiter.data.remote.response.bills

data class BillItem(
    val ClientCode: String? = null,
    val ClientName: String? = null,
    val ClientUid: String? = null,
    val Lines: List<LineItem>,
    val Number: Int,
    val Sum: Double,
    val SumAfterDiscount: Double,
    var TableUid: String,
    val Uid: String,
    val Guests: Int
)
