package com.away0x.`as`.proj.main

import com.alibaba.android.arouter.launcher.ARouter
import com.away0x.`as`.proj.common.ui.component.HiBaseApplication

class HiApplication : HiBaseApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        ARouter.init(this)
    }

}