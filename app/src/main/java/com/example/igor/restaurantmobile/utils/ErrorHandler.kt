package com.example.igor.restaurantmobile.utils

import com.example.igor.restaurantmobile.utils.enums.EnumLicenseErrors
import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors

class ErrorHandler {

    fun getErrorMessage(errorCode: EnumRemoteErrors): String{
        return when (errorCode){
            EnumRemoteErrors.TimeOut -> "Raspunsul de la server prea lung."
            EnumRemoteErrors.UnknownError -> "Eroare necunoscută!"
            EnumRemoteErrors.DeviceNotRegistered -> "Dispozitiv neînregistrat!"
            EnumRemoteErrors.ShiftIsNotValid -> "Tura la casă nu este validă!"
            EnumRemoteErrors.BillNotFound -> "Contul nu a fost găsit!"
            EnumRemoteErrors.ClientNotFound -> "Clientul nu a fost găsit!"
            EnumRemoteErrors.SecurityException -> "Excepție de securitate!"
            EnumRemoteErrors.ServerNotAvailable -> "Serverul nu este disponibil!"
            else -> "Efectuare nereusita,apelati provider-ul aplicatiei sau incercati mai tirziu."
        }
    }

    fun getErrorLicenseMessage(errorCode: EnumLicenseErrors): String{
        return when (errorCode){
            EnumLicenseErrors.LicenseNotExist -> "Licenta data nu exista!"
            EnumLicenseErrors.CodeNotExist -> "Asa cod de activare nu exista!"
            EnumLicenseErrors.CompanyNotFound -> "Compania nu exista!"
            EnumLicenseErrors.PlatformNotExist -> "Platforma nu exista!"
            EnumLicenseErrors.InternalError -> "Eroare interna la serviciu!"
            else -> "Efectuare nereusita,apelati provider-ul aplicatiei sau incercati mai tirziu."
        }
    }
}