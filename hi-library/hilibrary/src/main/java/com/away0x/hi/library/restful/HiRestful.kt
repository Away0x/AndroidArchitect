package com.away0x.hi.library.restful

import com.away0x.hi.library.restful.utils.MethodParser
import com.away0x.hi.library.restful.utils.Scheduler
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class HiRestful(val baserUrl: String, callFactory: HiCall.Factory) {

    private var interceptors: MutableList<HiInterceptor> = mutableListOf()
    private var methodService: ConcurrentHashMap<Method, MethodParser> = ConcurrentHashMap();
    private var scheduler: Scheduler

    init {
        scheduler = Scheduler(callFactory, interceptors)
    }

    fun addInterceptor(interceptor: HiInterceptor) {
        interceptors.add(interceptor)
    }

    /**
     * 根据 interface 解析出具体参数
     * interface ApiService {
     *  @Headers("auth-token:token", "accountId:123456")
     *  @BaseUrl("https://api.devio.org/as/")
     *  @POST("/cities/{province}")
     *  @GET("/cities")
     * fun listCities(@Path("province") province: Int,@Filed("page") page: Int): HiCall<JsonObject>
     * }
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { proxy, method, args ->

            // bugFix: 此处需要考虑 空参数
            var methodParser = methodService[method]
            if (methodParser == null) {
                methodParser = MethodParser.parse(baserUrl, method)
                methodService[method] = methodParser
            }

            //bugFix：此处 应当考虑到 methodParser复用，每次调用都应当解析入参
            val request = methodParser.newRequest(method, args)
            scheduler.newCall(request)
        } as T
    }

}