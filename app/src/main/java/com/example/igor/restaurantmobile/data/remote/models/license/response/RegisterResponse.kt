package com.example.igor.restaurantmobile.data.remote.models.license.response

data class RegisterResponse(
    var ErrorCode: Int? = -1,
    var ErrorMessage: String? = null,
    var AppData: ApplicationInfo? = null,
)
