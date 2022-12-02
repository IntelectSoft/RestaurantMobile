package com.example.igor.restaurantmobile.utils

object Urls {
    var baseUrl = "http://edi.md:4444//MobileCash/"

    const val RegisterUrl = "https://dev.edi.md/ISLicenseService/json/RegisterApplication"
    const val GetUri = "https://dev.edi.md/ISLicenseService/json/GetURI"

    val Ping = "${baseUrl}json/Ping"
    val RegisterDevice = "${baseUrl}json/RegisterDevice"
    val GetAssortimentList = "${baseUrl}json/GetAssortimentList"
    val GetBillsList = "${baseUrl}json/GetBillsList"
    val GetBill = "${baseUrl}json/GetBill"
    val CloseBill = "${baseUrl}json/CloseBill"
    val SaveBill = "${baseUrl}json/SaveBill"
    val SplitBill = "${baseUrl}json/SplitBill"
    val PrintBill = "${baseUrl}json/PrintBill"
    val ApplyCard = "${baseUrl}json/ApplyCard"
}

