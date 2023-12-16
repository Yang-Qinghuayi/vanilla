package com.example.m5

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // 启动一个协程
        GlobalScope.launch(Dispatchers.IO) {
            // 在 IO 线程中执行异步任务，比如网络请求
            val result = performNetworkRequest()

            // 切换回主线程更新 UI
            launch(Dispatchers.Main) {
                updateUI(result)
            }
        }
    }

    suspend fun performNetworkRequest(): String {
        // 模拟一个网络请求，实际中会使用网络库
        kotlinx.coroutines.delay(1000) // 延迟 1 秒，模拟网络请求
        return "Network Response Data"
    }

    fun updateUI(data: String) {
        // 更新 UI 的操作，可以在这里将网络请求结果显示在界面上
        println("Received data: $data")
    }
}