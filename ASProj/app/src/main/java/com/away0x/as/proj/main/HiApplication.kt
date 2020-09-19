package com.away0x.`as`.proj.main

import com.alibaba.android.arouter.launcher.ARouter
import com.away0x.`as`.proj.common.ui.component.HiBaseApplication
import com.away0x.hi.library.log.HiLogConfig
import com.away0x.hi.library.log.HiLogManager
import com.away0x.hi.library.log.printer.HiConsolePrinter
import com.google.gson.Gson

class HiApplication : HiBaseApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        ARouter.init(this)

        HiLogManager.init(object : HiLogConfig() {
            override fun injectJsonParser() = JsonParser { Gson().toJson(it) }
            override fun getGlobalTag() = "HiApplication"
            override fun enable() = true
            override fun includeThread() = false
            override fun stackTraceDepth() = 0
        }, HiConsolePrinter())
    }

}