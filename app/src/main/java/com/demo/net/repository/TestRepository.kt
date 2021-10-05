package com.demo.net.repository

import com.demo.net.bean.TestBean
import com.demo.net.service.TestService
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

open class TestRepository @Inject constructor(private val testService: TestService): BaseRepository(testService) {

    suspend fun getSentences(result: (errMsg: String?, bean: TestBean?) -> Unit) {
        doRequest{
            testService.getSentences()
        }.collect {
            if (it.code == 200) result(null, it.result) else result(it.message, null)
        }
    }
}