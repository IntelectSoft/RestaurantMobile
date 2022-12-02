package com.example.igor.restaurantmobile.utils.enums

enum class EnumLicenseErrors(val code: Int) {
    InternalError(-1),
    CompanyNotFound(1),
    PlatformNotExist(3),
    LicenseNotExist(124);


    companion object {
        fun getByValue(value: Int) = values().first { it.code == value }
    }
}
