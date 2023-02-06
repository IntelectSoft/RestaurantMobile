package md.edi.mobilewaiter.data.remote.response.bills

data class BillListResponse(
    var BillsList: List<BillItem>,
    var OccupiedTables: List<String> = emptyList(),
    var Result: Int,
    var ResultMessage: String?
)
