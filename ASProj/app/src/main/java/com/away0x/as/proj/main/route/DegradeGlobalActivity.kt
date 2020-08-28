package com.away0x.`as`.proj.main.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.away0x.`as`.proj.main.R
import kotlinx.android.synthetic.main.activity_degrade_global.*

/**
 * 全局统一错误页
 */
@Route(path = "/degrade/global/activity")
class DegradeGlobalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_degrade_global)

        empty_view.setIcon(R.string.if_unexpected1)
        empty_view.setIcon(R.string.degrade_tips)
    }
}