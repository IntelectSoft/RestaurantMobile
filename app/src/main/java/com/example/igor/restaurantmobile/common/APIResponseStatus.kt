package com.example.igor.restaurantmobile.common


// A generic class that contains data and status about loading this data.
sealed class APIResponseStatus(val e: Throwable?, val errorString: String?) {

    sealed class Error(e: Throwable, errorString : String? = null) : APIResponseStatus(e, errorString){
        class NoInternetConnectionError(e : Throwable) : Error(e)
        class CertificateExpirationError(e : Throwable ) : Error(e)
        sealed class HttpError(e : Throwable, errorString : String?) : Error(e,errorString){
            class RequestHttpUnauthorizedError(e : Throwable, errorString : String?) : HttpError(e,errorString)
            class RequestHttpNotFoundError(e : Throwable, errorString : String?) : HttpError(e,errorString)
            class RequestHttpError(e : Throwable, errorString : String?) : HttpError(e,errorString)
        }
        class RequestGeneralServerError(e : Throwable) : Error(e)
    }
}
