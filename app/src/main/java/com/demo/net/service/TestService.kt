package com.demo.net.service

import com.demo.net.bean.Result
import com.demo.net.bean.TestBean
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface TestService : BaseService{
    @GET("sentences")
    fun getSentences(
    ): Flow<Result<TestBean>>
}