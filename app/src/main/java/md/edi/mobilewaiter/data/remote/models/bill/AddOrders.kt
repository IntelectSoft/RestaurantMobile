package md.edi.mobilewaiter.data.remote.models.bill

data class AddOrders(
    var DeviceId: String? = null,
    var BillUid: String = "00000000-0000-0000-0000-000000000000",
    var TableUid: String = "00000000-0000-0000-0000-000000000000",
    var Orders: List<OrderItem> = emptyList(),
    var Guests: Int = 0
)
