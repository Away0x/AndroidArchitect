package com.away0x.hi.library.restful.annotation

/**
 * @GET("/cities/all")
 * fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class GET(val value: String)