package com.base.net.exception

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

fun handlerException(exception: Throwable, errLog: (code: Int, errMsg: String) -> Unit){
    when (exception){
        is HttpException -> {// 400,500,403...
            errLog(exception.code(), exception.message())
        }
        is ConnectException ->{
            errLog(-1, "服务器连接失败")
        }
        is SocketTimeoutException ->{
            errLog(-2, "网络请求超时，请稍后重试!")
        }
        is TimeoutException ->{
            errLog(-2, "网络请求超时，请稍后重试!")
        }
        is JsonSyntaxException -> {
            errLog(-3, "JsonSyntaxException, json解析异常!")
        }
        else -> {
            errLog(-4, "未知错误${exception}")
        }
    }
}