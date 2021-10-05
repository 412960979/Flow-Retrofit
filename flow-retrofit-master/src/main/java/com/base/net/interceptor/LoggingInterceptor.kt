package com.base.net.interceptor

import com.base.net.log.NetworkPrinter
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import kotlin.Throws

class LoggingInterceptor : Interceptor {
    private val networkPrinter: NetworkPrinter = NetworkPrinter()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        //打印请求信息
        networkPrinter.printRequest(request)

        //打印响应信息
        val response: Response
        try {
            response = chain.proceed(request)
            networkPrinter.printResponse(response)
        } catch (e: Exception) {
            throw e
        }
        return response
    }

}