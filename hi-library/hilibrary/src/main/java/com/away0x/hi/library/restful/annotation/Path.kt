package com.away0x.hi.library.restful.annotation

/**
 * @GET("/cities/{province}")
 * fun test(@Path("province") int provinceId)
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(val value: String)