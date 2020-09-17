package com.away0x.hi.library.app.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.away0x.hi.library.app.R
import com.away0x.hi.library.executor.HiExecutor

class HiExecutorDemoActivity : AppCompatActivity(), View.OnClickListener {

    private var paused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_executor_demo)
    }

    override fun onClick(v: View?) {
        if (v == null) return

        when (v.id) {
            // 按任务优先级执行
            R.id.textView1 -> {
                for (priority in 0 until 10) {
                    val p = priority
                    HiExecutor.execute(priority, Runnable {
                        Thread.sleep((1000 - p * 100).toLong())
                    })
                }
            }
            // 暂停/恢复线程池所有任务
            R.id.textView2 -> {
                if (paused) {
                    HiExecutor.resume()
                } else {
                    HiExecutor.pause()
                }
                paused = !paused
            }
            // 异步任务结果回调主线程
            R.id.textView3 -> {
                HiExecutor.execute(runnable = object : HiExecutor.Callable<String>() {
                    override fun onBackground(): String {
                        Log.e("HiExecutor", "onBackground-当前线程是: ${Thread.currentThread().name}")
                        return "我是异步任务的结果"
                    }

                    override fun onCompleted(result: String) {
                        Log.e("HiExecutor", "onCompleted-当前线程是: ${Thread.currentThread().name}")
                        Log.e("HiExecutor", "onCompleted-任务结果是: $result")
                    }
                })
            }
        }
    }
}