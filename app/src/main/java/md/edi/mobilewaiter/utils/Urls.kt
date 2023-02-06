package md.edi.mobilewaiter.utils

import md.edi.mobilewaiter.BuildConfig

object Urls {
    var baseUrl = "http://edi.md:4444//MobileCash/"

    const val RegisterUrl = "${BuildConfig.license_api_url}json/RegisterApplication"
    const val GetUri = "${BuildConfig.license_api_url}json/GetURI"

    val Ping = "${baseUrl}json/Ping"
    val RegisterDevice = "${baseUrl}json/RegisterDevice"
    val GetAssortimentList = "${baseUrl}json/GetAssortimentList"
    val GetBillsList = "${baseUrl}json/GetBillsList"
    val CombineBills = "${baseUrl}json/CombineBills"
    val CloseBill = "${baseUrl}json/CloseBill"
    val SaveBill = "${baseUrl}json/SaveBill"
    val SplitBill = "${baseUrl}json/SplitBill"
    val PrintBill = "${baseUrl}json/PrintBill"
    val ApplyCard = "${baseUrl}json/ApplyCard"
}

