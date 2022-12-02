package com.example.igor.restaurantmobile.data.remote.models.license


data class GetUriModel(
    var ApplicationVersion: String? = null,
    var DeviceID: String? = null,
    var DeviceName: String? = null,
    var DeviceModel: String? = null,
    var LicenseActivationCode: String? = null,
    var LicenseID: String? = null,
    var OSType: Int? = null,
    var OSVersion: String? = null,
    var ProductType: Int? = null,
)
