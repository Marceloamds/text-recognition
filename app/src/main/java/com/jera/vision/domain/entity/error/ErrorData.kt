package com.jera.vision.domain.entity.error

sealed class ErrorData(val message: String, val tryAgainAction: (() -> Unit)?) {

    class TimeOutErrorData(message: String, tryAgainAction: (() -> Unit)?) :
        ErrorData(message, tryAgainAction)

    class HttpErrorData(message: String, tryAgainAction: (() -> Unit)?) :
        ErrorData(message, tryAgainAction)

    class NetworkErrorData(message: String, tryAgainAction: (() -> Unit)?) :
        ErrorData(message, tryAgainAction)

    class UnexpectedErrorData(message: String, tryAgainAction: (() -> Unit)?) :
        ErrorData(message, tryAgainAction)

    class ClientEndHttpErrorData(message: String) : ErrorData(message, null)
}