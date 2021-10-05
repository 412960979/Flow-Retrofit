package com.base.net.interceptor

import android.content.Context
import com.base.net.utils.getCacheDir
import com.base.net.utils.isAvailable
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.lang.RuntimeException

/** 默认100M  */
private const val MAX_CACHE = 100 * 1024 * 1024

class HttpCacheInterceptor(context: Context, cacheDir: String? = null) : Interceptor {
    private val context = context
    private var cacheFile: File = if (cacheDir == null) {
        File(getCacheDir(context), "net")
    } else {
        File(cacheDir, "net")
    }
    private val cache: Cache

    init {
        if (!cacheFile.exists()) {
            val result = cacheFile.mkdirs()
            if (!result) {
                throw RuntimeException("请检查缓存目录是否正确！")
            }
        }
        cache = Cache(cacheFile, MAX_CACHE.toLong())
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (!isAvailable(context)) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        val originalResponse = chain.proceed(request)
        return if (isAvailable(context)) {
            //有网络的时候读取接口里面的配置，在这里进行统一配置
            val cacheControl = request.cacheControl.toString()
            originalResponse.newBuilder()
                .header("Cache-Control", cacheControl)
                .removeHeader("Pragma")
                .build()
        } else {
            originalResponse.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                .removeHeader("Pragma")
                .build()
        }
    }

}