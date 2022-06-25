package com.pwong.uiframe.net.interceptor

import com.pwong.library.utils.LogUtil
import com.pwong.uiframe.net.TokenCache
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val builder = req.newBuilder()
        val token = TokenCache.getToken()
        if (token.isNotEmpty()) {
            builder.addHeader("Token", token)
        } else {
            builder.addHeader("Token", "none")
        }
        LogUtil.logE("NetApi", "http request url:${req.url()}")
        LogUtil.logE("NetApi", "http request token:$token ")
        val request = builder.build()
        val reqBuilder = request.newBuilder().method(request.method(), request.body())
        return chain.proceed(reqBuilder.build())
    }
}