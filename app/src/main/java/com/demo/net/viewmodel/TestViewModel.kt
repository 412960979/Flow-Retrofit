package com.demo.net.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.demo.net.bean.TestBean
import com.demo.net.repository.TestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(private val repository: TestRepository, state: SavedStateHandle) : BaseViewModel(repository, state){

    fun getSentences(result: (errMsg: String?, bean: TestBean?) -> Unit){
        viewModelScope.launch {
            repository.getSentences { errMsg, bean ->
                result(errMsg, bean)
            }
        }
    }
}