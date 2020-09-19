package com.away0x.hi.library.restful

import com.away0x.hi.library.restful.model.HiResponse

/**
 * 拦截器
 */
interface HiInterceptor {
    fun intercept(chain: Chain): Boolean

    /**
     * Chain 对象会在我们派发拦截器的时候 创建
     */
    interface Chain {
        val isRequestPeriod: Boolean get() = false

        fun request(): HiRequest

        /**
         * 这个 response 对象 在请求发起之前 ，是为空的
         */
        fun response(): HiResponse<*>?

    }
}