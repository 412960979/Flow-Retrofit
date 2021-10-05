package com.demo.net.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.net.bean.MatchListBean
import com.demo.net.repository.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(private val baseRepository: BaseRepository, state: SavedStateHandle): ViewModel() {
    companion object {
        private const val USER_KEY = "userId"
    }

    private val savedStateHandle = state

    fun saveCurrentUser(userId: String) {
        // 存储 userId 对应的数据
        savedStateHandle.set(USER_KEY, userId)
    }

    fun getCurrentUser(): String {
        // 从 saveStateHandle 中取出当前 userId
        return savedStateHandle.get(USER_KEY)?: ""
    }

//    fun test(result: (errMsg: String?, bean: MatchListBean?) -> Unit){
//        viewModelScope.launch {
//            baseRepository.test { errMsg, bean ->
//                result(errMsg, bean)
//            }
//        }
//    }
}