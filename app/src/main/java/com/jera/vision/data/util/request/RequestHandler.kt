package com.jera.vision.data.util.request

import com.jera.vision.domain.entity.error.RequestException
import com.google.gson.Gson
import com.jera.vision.data.entity.ApiErrors
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class RequestHandler {
    protected suspend fun <T : Any> makeRequest(block: Response<T>): T? {
        return coroutineScope {
            try {
                block.run {
                    if (isSuccessful) {
                        body()
                    } else {
                        throw RequestException.HttpError(
                            code(),
                            extractErrorBody(errorBody())
                        )
                    }
                }
            } catch (t: Exception) {
                throw when (t) {
                    is RequestException -> t
                    is SocketTimeoutException -> RequestException.TimeoutError()
                    is UnknownHostException -> RequestException.UnexpectedError(t)
                    is IOException -> RequestException.NetworkError()
                    else -> RequestException.UnexpectedError(t)
                }
            }
        }
    }

    private fun extractErrorBody(errorBody: ResponseBody? = null): String? {
        return Gson().fromJson(errorBody?.string(), ApiErrors::class.java).let {
            if (it.errors != null) {
                it.errors.joinToString("\n")
            } else {
                it.errorMessage
            }
        }
    }
}