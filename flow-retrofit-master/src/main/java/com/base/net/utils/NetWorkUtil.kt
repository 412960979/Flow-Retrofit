package com.base.net.utils

import android.net.NetworkInfo
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

/**
 * 判断网络是否可用
 */
fun isAvailable(context: Context): Boolean {
    val info = getActiveNetworkInfo(context)
    return info != null && info.isAvailable
}

/**
 * 获取活动网络信息
 */
@SuppressLint("MissingPermission")
fun getActiveNetworkInfo(context: Context): NetworkInfo? {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo
}