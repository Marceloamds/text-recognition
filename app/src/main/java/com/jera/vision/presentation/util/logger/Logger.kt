package com.jera.vision.presentation.util.logger

import android.content.Context
import android.util.Log
import com.jera.vision.BuildConfig
import com.jera.vision.R

class Logger constructor(context: Context) {

    private val tag = context.getString(R.string.app_name)

    fun e(tr: Throwable) {
        if (BuildConfig.DEBUG) Log.e(tag, tr.message, tr)
    }
}
