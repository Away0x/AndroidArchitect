package com.away0x.hi.library.restful

import com.away0x.hi.library.restful.model.HiResponse
import java.lang.reflect.Type

interface HiConvert {
    fun <T> convert(rawData: String, dataType: Type): HiResponse<T>
}