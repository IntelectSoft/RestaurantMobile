package md.edi.mobilewaiter.data.remote.response.terminal

data class RegisterTerminalResponse(
    val DeviceId: String? = null,
    val DeviceNumber: Int = -1,
    val Result: Int = -9,
    val ResultMessage: String? = null
)
