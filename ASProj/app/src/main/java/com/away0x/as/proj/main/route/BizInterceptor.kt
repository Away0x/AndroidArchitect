package com.away0x.`as`.proj.main.route

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import java.lang.RuntimeException

@Interceptor(name = "biz_interceptor", priority = 9)
class BizInterceptor : IInterceptor {

    private var context: Context? = null

    override fun init(context: Context?) {
        this.context = context
    }

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        val flag = postcard?.extra ?: return

        when {
            (flag and RouteFlag.FLAG_LOGIN) != 0 -> {
                // 需要 login
                callback?.onInterrupt(RuntimeException("need login"))
                showToast("请先登录")
            }
            (flag and RouteFlag.FLAG_AUTHENTICATION) != 0 -> {
                // 需要实名认证
                callback?.onInterrupt(RuntimeException("need authtication"))
                showToast("请先实名认证")
            }
            (flag and RouteFlag.FLAG_VIP) != 0 -> {
                // 需要 vip
                callback?.onInterrupt(RuntimeException("need vip"))
                showToast("请先加入会员")
            }
            else -> {
                callback?.onContinue(postcard)
            }
        }
    }

    private fun showToast(text: String) {
        // 拦截器的 process 是运行在子线程中的，所以这里想 taost 需要转到主线程中
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).run {
                setGravity(Gravity.CENTER,0, 0)
                show()
            }
        }
    }

}