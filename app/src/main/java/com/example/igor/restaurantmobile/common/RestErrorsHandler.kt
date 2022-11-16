package com.example.igor.restaurantmobile.common

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

fun Throwable.getRestError(): APIResponseStatus.Error {
    return when (this) {
        is UnknownHostException -> {
            APIResponseStatus.Error.NoInternetConnectionError(this);
        }
        is SSLHandshakeException -> {
            APIResponseStatus.Error.CertificateExpirationError(this);
        }

        is HttpException -> {
            when (this.code()) {
                401 -> APIResponseStatus.Error.HttpError.RequestHttpUnauthorizedError(
                    this,
                    this.response()?.errorBody()?.string()
                );
                404 -> APIResponseStatus.Error.HttpError.RequestHttpNotFoundError(
                    this,
                    this.response()?.errorBody()?.string()
                );
                else -> APIResponseStatus.Error.HttpError.RequestHttpError(
                    this,
                    this.response()?.errorBody()?.string()
                );
            }
        }

        else -> APIResponseStatus.Error.RequestGeneralServerError(this)
    }
}


inline fun <reified T> APIResponseStatus.Error.HttpError.mapApiErrorToModel(gson: Gson): T? {
    return try {
        val responseBody = this.errorString
        val errorModel: T = gson.fromJson(responseBody, T::class.java)
        errorModel
    } catch (e: Exception) {
        null
    }
}

fun APIResponseStatus.Error.mapApiErrorKeyToResourceString(gson: Gson, context: Context): String {
    var errorMessage = ""
    val responseBody = errorString
    if (responseBody != null) {
        try {

            val apiResponseHashMap: ArrayList<*> =
                try {
                    gson.fromJson(
                        this.errorString,
                        List::class.java
                    ) as ArrayList<*>
                } catch (ex: Exception) {
                    arrayListOf(
                        gson.fromJson<Map<String, Any>>(
                            errorString,
                            object : TypeToken<Map<String, Any>>() {}.type
                        ).let {
                            val first = it.entries.first().value
                            when (first) {
                                is String -> first
                                is List<*> -> first.first() as String
                                else -> throw Exception()
                            }
                        }

                    )
                }

            apiResponseHashMap.forEach {
                errorMessage += it.toString()
            }

        } catch (e: Exception) {
            errorMessage = "General error!"
            e.printStackTrace()
        }
    }

    return errorMessage
}
