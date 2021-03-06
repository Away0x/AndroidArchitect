package com.away0x.hi.library.restful

import android.text.TextUtils
import androidx.annotation.IntDef
import com.away0x.hi.library.restful.annotation.CacheStrategy
import java.lang.reflect.Type
import java.net.URLEncoder

class HiRequest {

    @IntDef(value = [METHOD.GET, METHOD.POST])
    annotation class METHOD {
        companion object {
            const val GET = 0
            const val POST = 1
        }
    }

    private var cacheStrategyKey: String = ""
    var cacheStrategy: Int = CacheStrategy.NET_ONLY

    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var domainUrl: String? = null // 请求域名
    var relativeUrl: String? = null // 相对路径
    var returnType: Type? = null // 响应类型
    var formPost: Boolean = true // 是否表单提交

    /**
     * 处理 url (请求的完整的url)
     *   https://api.devio.org/v1/: relativeUrl = user/login
     *     >>> https://api.devio.org/v1/user/login
     *
     * 可能存在别的域名的场景，如 https://api.devio.org/v2/ :
     *   https://api.devio.org/v1/: relativeUrl = /v2/user/login
     *     >>> https://api.devio.org/v2/user/login
     */
    fun endPointUrl(): String {
        if (relativeUrl == null) {
            throw IllegalStateException("relative url must bot be null ")
        }
        if (!relativeUrl!!.startsWith("/")) {
            return domainUrl + relativeUrl
        }

        val indexOf = domainUrl!!.indexOf("/")
        return domainUrl!!.substring(0, indexOf) + relativeUrl
    }

    fun addHeader(name: String, value: String) {
        if (headers == null) {
            headers = mutableMapOf()
        }
        headers!![name] = value
    }

    fun getCacheKey(): String {
        if (!TextUtils.isEmpty(cacheStrategyKey)) {
            return cacheStrategyKey
        }
        val builder = StringBuilder()
        val endUrl = endPointUrl()
        builder.append(endUrl)
        if (endUrl.indexOf("?") > 0 || endUrl.indexOf("&") > 0) {
            builder.append("&")
        } else {
            builder.append("?")
        }

        if (parameters != null) {
            for ((key, value) in parameters!!) {
                try {
                    val encodeValue = URLEncoder.encode(value, "UTF-8")
                    builder.append(key).append("=").append(encodeValue).append("&")
                } catch (e: Exception) {
                    //ignore
                }
            }
            builder.deleteCharAt(builder.length - 1)
            cacheStrategyKey = builder.toString()
        } else {
            cacheStrategyKey = endUrl
        }

        return cacheStrategyKey
    }

}