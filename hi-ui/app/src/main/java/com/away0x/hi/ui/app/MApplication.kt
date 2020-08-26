package com.away0x.hi.ui.app

import android.app.Application
import com.away0x.hi.library.log.HiLogConfig
import com.away0x.hi.library.log.HiLogManager
import com.away0x.hi.library.log.printer.HiConsolePrinter
import com.away0x.hi.library.log.printer.HiLogPrinter
import org.json.JSONObject

class MApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        HiLogManager.init(object : HiLogConfig() {
            override fun getGlobalTag(): String {
                return "MApplication"
            }

            override fun enable(): Boolean {
                return true
            }

            override fun includeThread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 5
            }
        }, HiConsolePrinter())
    }

}