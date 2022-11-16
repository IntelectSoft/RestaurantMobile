package com.example.igor.restaurantmobile.utils.enums

enum class EnumRemoteErrors(val code: Int) {
    UnknownError(1),
    DeviceNotRegistered(2),
    ShiftIsNotValid (3),
    BillNotFound(4),
    ClientNotFound(5),
    SecurityException (6),
    ServerNotAvailable (7);

    companion object {
        fun getByValue(value: Int) = values().first { it.code == value }
    }
}
