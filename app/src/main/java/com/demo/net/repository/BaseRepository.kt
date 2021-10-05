package com.demo.net.repository

import android.util.Log
import com.demo.net.bean.Result
import com.base.net.exception.handlerException
import com.demo.net.BuildConfig
import com.demo.net.bean.MatchListBean
import com.demo.net.service.BaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

open class BaseRepository @Inject constructor(private val service: BaseService) {

//    /**
//     * 在这里可以处理一些map数据转换的操作,最终返回ViewModel需要的数据
//     */
//    suspend fun test(result: (errMsg: String?, bean: MatchListBean?) -> Unit) {
//        doRequest{
//            service.getPopularMovies(1)
//        }.collect {
//            if (it.code == 200) result(null, it.result) else result(it.message, null)
//        }
//    }

    /**
     * 执行网络请求，并捕获处理服务器异常。
     */
    protected suspend fun <T> doRequest(
        request: suspend () -> Flow<Result<T>>
    ): Flow<Result<T>> {
        return try {
            request()
                .flowOn(Dispatchers.IO)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG){
                Log.e("Network-Exception", "$e.message")
            }
            var code = 0
            var msg = ""
            handlerException(e) { errCode: Int, errMsg: String ->
                code = errCode
                msg = errMsg
            }
            flow {
                emit(Result(code, msg, null))
            }
        }
    }
}