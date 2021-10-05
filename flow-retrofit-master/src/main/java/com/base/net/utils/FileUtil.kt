package com.base.net.utils

import android.content.Context
import android.os.Environment

/**
 * 获取缓存目录，先获取/sdcard/Android/data/package_name/cache，失败才获取/data/data/com.android.framework/cache
 *
 * @param context
 * @return 返回缓存路径
 */
fun getCacheDir(context: Context): String {
    // /data/data/com.android.framework/cache
    val cacheDir = context.cacheDir
    // /sdcard/Android/data/package_name/cache
    val externalCacheDir = context.externalCacheDir
    val cacheDirStr: String = if (externalCacheDir == null) {
        cacheDir.absolutePath
    } else {
        if (checkSdCard()) externalCacheDir.absolutePath else cacheDir.absolutePath
    }
    return cacheDirStr
}

fun checkSdCard(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}
