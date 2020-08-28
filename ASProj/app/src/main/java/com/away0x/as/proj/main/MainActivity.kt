package com.away0x.`as`.proj.main

import android.os.Bundle
import com.away0x.`as`.proj.common.ui.component.HiBaseActivity
import com.away0x.`as`.proj.main.R
import com.away0x.`as`.proj.main.logic.MainActivityLogic

class MainActivity : HiBaseActivity(), MainActivityLogic.ActivityProvider {

    private lateinit var activityLogic: MainActivityLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityLogic = MainActivityLogic(this)
    }

}