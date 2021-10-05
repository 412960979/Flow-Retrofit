package com.base.net

import android.content.Context
import com.base.net.adapter.FlowCallAdapterFactory
import com.base.net.interceptor.HttpCacheInterceptor
import com.base.net.interceptor.HttpHeaderInterceptor
import com.base.net.interceptor.LoggingInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 提供一个service实例
 */
inline fun <reified T> provideService(retrofit: Retrofit): T = retrofit.create(T::class.java)

/**
 * 构造一个默认的retrofit对象
 */
fun defaultBuildRetrofit(
    context: Context,
    baseUrl: String,
    debug: Boolean,
    okHttpClient: (() -> OkHttpClient)? = null
): Retrofit = buildRetrofit {
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create()

    client(
        if (okHttpClient == null) defaultBuildOkhttpClient(
            context = context,
            debug = debug
        ) else okHttpClient()
    )
    addConverterFactory(GsonConverterFactory.create(gson))
    addCallAdapterFactory(FlowCallAdapterFactory.create())
    baseUrl(baseUrl)
}

/**
 * 构造一个默认的okhttp对象
 */
fun defaultBuildOkhttpClient(
    context: Context,
    debug: Boolean,
    readTimeOut: Int = 10,
    writeTimeOut: Int = 10,
    connectTimeOut: Int = 10,
    callTimeOut: Int = 10,
    needCache: Boolean = false,
    cacheDir: String? = null,
    httpHeaderMap: Map<String, String>? = null,
    interceptors: List<Interceptor>? = null,
    netWorkInterceptors: List<Interceptor>? = null
): OkHttpClient = buildOkhttpClient {
    readTimeout(readTimeOut.toLong(), TimeUnit.SECONDS)
    writeTimeout(writeTimeOut.toLong(), TimeUnit.SECONDS)
    connectTimeout(connectTimeOut.toLong(), TimeUnit.SECONDS)
    callTimeout(callTimeOut.toLong(), TimeUnit.SECONDS)
    if (debug) addNetworkInterceptor(LoggingInterceptor())
    if (needCache) addNetworkInterceptor(HttpCacheInterceptor(context, cacheDir))
    httpHeaderMap?.let { addInterceptor(HttpHeaderInterceptor(httpHeaderMap)) }
    interceptors?.let { interceptors() += interceptors }
    netWorkInterceptors?.let { networkInterceptors() += netWorkInterceptors }
}

/**
 * 可自行定制retrofit
 */
inline fun buildRetrofit(buildRetrofit: Retrofit.Builder.() -> Unit): Retrofit {
    val builder = Retrofit.Builder()
    builder.buildRetrofit()
    return builder.build()
}

/**
 * 可自定定制OkhttpClient
 */
inline fun buildOkhttpClient(buildOkhttp: OkHttpClient.Builder.() -> Unit): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.buildOkhttp()
    return builder.build()
}