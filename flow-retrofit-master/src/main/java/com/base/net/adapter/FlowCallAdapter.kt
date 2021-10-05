package com.base.net.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resumeWithException

internal  class ResponseCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<Response<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Flow<Response<T>> {
        return flow {
            emit(
                // 将回调函数转成协程作用域
                suspendCancellableCoroutine { continuation ->
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            continuation.resume(response){
                                call.cancel()
                            }
                        }
                    })
                }
            )
        }
    }
}

internal class BodyCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<T>> {
    override fun responseType() = responseType

    override fun adapt(call: Call<T>): Flow<T> {
        return flow {
            emit(
                // 将回调函数转成协程作用域
                suspendCancellableCoroutine { continuation ->
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            try {
                                continuation.resume(response.body()!!){
                                    call.cancel()
                                }
                            } catch (e: Exception) {
                                continuation.resumeWithException(e)
                            }
                        }
                    })
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
        }
    }
}