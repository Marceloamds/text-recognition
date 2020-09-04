package com.jera.vision.domain.util.resource

import android.content.Context
import androidx.annotation.StringRes
import com.jera.vision.R

class

Strings constructor(private val context: Context) {

    val errorTitle: String get() = res(R.string.error_title)
    val errorUnknown: String get() = res(R.string.error_unknown)
    val errorNetwork: String get() = res(R.string.error_network)
    val errorNotFound: String get() = res(R.string.error_not_found)
    val errorSocketTimeout: String get() = res(R.string.error_socket_timeout)
    val errorInternalServer: String get() = res(R.string.error_internal_server)
    val errorBadRequest: String get() = res(R.string.error_bad_request)
    val errorConflict: String get() = res(R.string.error_conflict)
    val errorForbidden: String get() = res(R.string.error_forbidden)
    val errorUnauthorized: String get() = res(R.string.error_unauthorized)
    val errorUnexpected: String get() = res(R.string.error_unexpected)
    val errorUnprocessableEntity: String get() = res(R.string.error_unprocessable_entity)
    val globalTryAgain: String get() = res(R.string.global_try_again)
    val globalOk: String get() = res(R.string.global_ok)

    private fun res(@StringRes stringId: Int) = context.getString(stringId)
}