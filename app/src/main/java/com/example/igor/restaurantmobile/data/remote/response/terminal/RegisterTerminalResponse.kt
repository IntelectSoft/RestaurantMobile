package com.example.igor.restaurantmobile.data.remote.response.terminal

data class RegisterTerminalResponse(
    val DeviceId: String? = null,
    val DeviceNumber: Int = -1,
    val Result: Int = -1,
    val ResultMessage: String? = null
)
