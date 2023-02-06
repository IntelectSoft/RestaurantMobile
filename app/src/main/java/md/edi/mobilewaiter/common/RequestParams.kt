package md.edi.mobilewaiter.common

class RequestParams {
    var isShowLoading: Boolean
        private set
    var isShowHttpError: Boolean
        private set
    var isShowUnauthorizedError: Boolean
        private set
    private var showNotFoundError: Boolean
    var isShowServerError: Boolean
        private set
    var isShowNoConnection: Boolean
        private set

    constructor() {
        isShowLoading = true
        isShowHttpError = true
        isShowUnauthorizedError = true
        isShowServerError = true
        isShowNoConnection = true
        showNotFoundError = true
    }

    private constructor(
        showLoadingStart: Boolean,
        showHttpError: Boolean,
        showUnauthorizedError: Boolean,
        showServerError: Boolean,
        showNoConnection: Boolean,
        showNotFoundError: Boolean
    ) {
        isShowLoading = showLoadingStart
        isShowHttpError = showHttpError
        isShowUnauthorizedError = showUnauthorizedError
        isShowServerError = showServerError
        isShowNoConnection = showNoConnection
        this.showNotFoundError = showNotFoundError
    }

    companion object {
        fun onlyLoading(): RequestParams {
            return RequestParams(
                true,
                false,
                false,
                false,
                false, true
            )
        }

        fun nothing(): RequestParams {
            return RequestParams(
                false,
                false,
                false,
                false,
                false, true
            )
        }

        fun noLoading(): RequestParams {
            return RequestParams(
                false,
                true,
                true,
                true,
                true, true
            )
        }

        fun noHttpNoUnauthorized(): RequestParams {
            return RequestParams(
                true,
                false,
                false,
                true,
                true, true
            )
        }

        fun noHttp(): RequestParams {
            return RequestParams(
                true,
                false,
                true,
                true,
                true, true
            )
        }

        fun noNotFound(): RequestParams {
            return RequestParams(
                true,
                true,
                true,
                true,
                true, false
            )
        }

        fun noError(): RequestParams {
            return RequestParams(
                true,
                false,
                false,
                false,
                true,
                false
            )
        }
    }
}