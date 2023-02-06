package md.edi.mobilewaiter.presentation.main.split.items

data class LineItemModel(
    var AssortimentUid: String,
    var Comments: List<String>? = emptyList(),
    var Count: Double,
    var QueueNumber: Int,
    var KitUid: String? = null,
    var PriceLineUid: String,
    var Sum: Double,
    var SumAfterDiscount: Double,
    var Uid: String,
    var isChecked: Boolean
)