package com.away0x.`as`.proj.main.http.api

import com.away0x.hi.library.restful.HiCall
import com.away0x.hi.library.restful.annotation.Filed
import com.away0x.hi.library.restful.annotation.GET
import org.json.JSONObject

interface TestApi {
    @GET("cities")
    fun listCities(@Filed("name") name: String): HiCall<JSONObject>
}