package com.away0x.hi.library.restful

import com.away0x.hi.library.restful.model.HiResponse
import java.lang.reflect.Type

/**
 * 格式转换
 */
interface HiConvert {
    fun <T> convert(rawData: String, dataType: Type): HiResponse<T>
}