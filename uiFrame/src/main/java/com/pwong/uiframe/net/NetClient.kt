package com.pwong.uiframe.net

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.pwong.uiframe.net.interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetClient(val context: Context, baseURL: String) {

    private var retrofit: Retrofit

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        // Level 改成 HEADERS, 避免 BODY 时， 上传文件太大导致 OOM
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        val httpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(TokenInterceptor())
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }


    fun <T> createApi(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

}