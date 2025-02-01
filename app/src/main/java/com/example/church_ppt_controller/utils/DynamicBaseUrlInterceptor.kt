package com.example.church_ppt_controller.utils

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class DynamicBaseUrlInterceptor : Interceptor {
    @Volatile
    private var baseUrl: String? = null

    fun updateBaseUrl(url: String) {
        baseUrl = url
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        baseUrl?.let { url ->
            val newUrl = request.url.newBuilder()
                .scheme(url.toHttpUrlOrNull()!!.scheme)
                .host(url.toHttpUrlOrNull()!!.host)
                .port(url.toHttpUrlOrNull()!!.port)
                .build()

            request = request.newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(request)
    }
}