package com.away0x.hi.library.restful.annotation

/**
 * @BaseUrl("https://api.devio.org/as/")
 * fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl(val value: String)