package com.demo.net.bean

/**
 * 服务端返回的json
 */
data class Result<out T>(
    val code: Int,
    val message: String,
    val result: T?
)
