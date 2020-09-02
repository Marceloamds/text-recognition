package com.jera.vision.data.util.request

import com.jera.vision.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalUrl = chain.request().url()
        val queriedUrl = originalUrl
            .newBuilder()
            .addQueryParameter("access_key", BuildConfig.ACCESS_KEY)
            .build()
        val queriedBuilder = chain.request().newBuilder().url(queriedUrl).build()
        return chain.proceed(queriedBuilder)
    }
}