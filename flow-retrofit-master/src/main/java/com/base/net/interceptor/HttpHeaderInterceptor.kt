package com.base.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HttpHeaderInterceptor(private val headerMap: Map<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        for((key, value) in headerMap){
            builder.header(key, value)
        }

        return chain.proceed(builder.build())
    }
}