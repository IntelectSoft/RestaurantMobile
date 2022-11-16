package com.example.igor.restaurantmobile.utils

import com.example.igor.restaurantmobile.utils.enums.EnumRemoteErrors

class ErrorHandler {

    fun getErrorMessage(errorCode: EnumRemoteErrors): String{
        when (errorCode){
            EnumRemoteErrors.UnknownError -> return "Eroare necunoscută!"
            EnumRemoteErrors.DeviceNotRegistered -> return "Dispozitiv neînregistrat!"
            EnumRemoteErrors.ShiftIsNotValid -> return "Tura la casă nu este validă!"
            EnumRemoteErrors.BillNotFound -> return "Contul nu a fost găsit!"
            EnumRemoteErrors.ClientNotFound -> return "Clientul nu a fost găsit!"
            EnumRemoteErrors.SecurityException -> return "Excepție de securitate!"
            EnumRemoteErrors.ServerNotAvailable -> return "Serverul nu este disponibil!"
            else -> return  "Efectuare nereusita, incercati mai tirziu."
        }
    }
}