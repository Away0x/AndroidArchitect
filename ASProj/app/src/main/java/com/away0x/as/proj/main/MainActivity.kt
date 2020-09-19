package com.away0x.`as`.proj.main

import android.os.Bundle
import com.away0x.`as`.proj.common.ui.component.HiBaseActivity
import com.away0x.`as`.proj.main.R
import com.away0x.`as`.proj.main.http.ApiFactory
import com.away0x.`as`.proj.main.http.api.TestApi
import com.away0x.`as`.proj.main.logic.MainActivityLogic
import com.away0x.hi.library.restful.HiCallback
import com.away0x.hi.library.restful.model.HiResponse
import org.json.JSONObject

class MainActivity : HiBaseActivity(), MainActivityLogic.ActivityProvider {

    private lateinit var activityLogic: MainActivityLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityLogic = MainActivityLogic(this)

        ApiFactory.create(TestApi::class.java).listCities("wutong").enqueue(object : HiCallback<JSONObject> {
            override fun onSuccess(response: HiResponse<JSONObject>) {

            }
        })
    }

}