package com.away0x.`as`.proj.main.biz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.away0x.`as`.proj.main.R
import com.away0x.`as`.proj.main.route.RouteFlag

@Route(path = "/profile/vip", extras = RouteFlag.FLAG_VIP)
class VipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip)
    }
}