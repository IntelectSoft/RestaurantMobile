package md.edi.mobilewaiter.utils.enums

enum class EnumRemoteErrors(val code: Int) {
    TimeOut(-1),
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
