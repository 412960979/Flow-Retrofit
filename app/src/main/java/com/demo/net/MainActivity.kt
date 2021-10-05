package com.demo.net

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.demo.net.viewmodel.TestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val testVM: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getData()
        findViewById<TextView>(R.id.tv).setOnClickListener {
            getData()
        }
    }

    private fun getData(){
        testVM.getSentences { errMsg, bean ->
            // 所有的网络请求都要提示错误信息，这里可以用字节码插桩来实现
            if (errMsg != null){
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            }

            bean?.let {
                Toast.makeText(this, "${it.name}\n${it.from}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}