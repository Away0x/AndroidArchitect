package com.away0x.hi.library.restful.annotation

/**
 * @POST("/cities/{province}")
 * fun test(@Path("province") int provinceId)
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class POST(val value: String, val formPost: Boolean = true)