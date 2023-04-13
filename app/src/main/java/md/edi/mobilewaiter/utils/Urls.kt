package md.edi.mobilewaiter.utils

import md.edi.mobilewaiter.BuildConfig
import java.text.DecimalFormat

object Urls {
    var baseUrl = ""

    const val RegisterUrl = "${BuildConfig.license_api_url}json/RegisterApplication"
    const val GetUri = "${BuildConfig.license_api_url}json/GetURI"

    var Ping = "${baseUrl}json/Ping"
    var RegisterDevice = "${baseUrl}json/RegisterDevice"
    var GetAssortimentList = "${baseUrl}json/GetAssortimentList"
    var GetBillsList = "${baseUrl}json/GetBillsList"
    var GetBill = "${baseUrl}json/GetBill"
    var CombineBills = "${baseUrl}json/CombineBills"
    var CloseBill = "${baseUrl}json/CloseBill"
    var SaveBill = "${baseUrl}json/SaveBill"
    var SplitBill = "${baseUrl}json/SplitBill"
    var PrintBill = "${baseUrl}json/PrintBill"
    var ApplyCard = "${baseUrl}json/ApplyCard"

    fun init() {
        Ping = "${baseUrl}json/Ping"
        RegisterDevice = "${baseUrl}json/RegisterDevice"
        GetAssortimentList = "${baseUrl}json/GetAssortimentList"
        GetBillsList = "${baseUrl}json/GetBillsList"
        GetBill = "${baseUrl}json/GetBill"
        CombineBills = "${baseUrl}json/CombineBills"
        CloseBill = "${baseUrl}json/CloseBill"
        SaveBill = "${baseUrl}json/SaveBill"
        SplitBill = "${baseUrl}json/SplitBill"
        PrintBill = "${baseUrl}json/PrintBill"
        ApplyCard = "${baseUrl}json/ApplyCard"
    }
}

