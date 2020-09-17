package com.away0x.hi.library.restful

import com.away0x.hi.library.restful.model.HiResponse

/**
 * callbak 回调
 */
interface HiCallback<T> {
    fun onSuccess(response: HiResponse<T>)
    fun onFailed(throwable: Throwable){}
}