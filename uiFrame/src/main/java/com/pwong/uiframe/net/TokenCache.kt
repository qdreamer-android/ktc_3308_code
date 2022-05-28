package com.pwong.uiframe.net

import android.content.Context
import com.pwong.library.utils.SPUtils
import com.pwong.uiframe.BuildConfig
import java.lang.ref.WeakReference

/**
 * @author android
 * @date 20-4-7
 */
object TokenCache {

    private const val KEY_TOKEN = "${BuildConfig.LIBRARY_PACKAGE_NAME}_token"

    private var weakReference: WeakReference<Context>? = null

    fun init(context: Context) {
        weakReference = WeakReference(context.applicationContext)
    }

    fun saveToken(token: String?) {
        weakReference?.get()?.let {
            SPUtils.put(it, KEY_TOKEN, token ?: "")
        }
    }

    fun getToken(): String {
        return weakReference?.get()?.let {
            SPUtils.get(it, KEY_TOKEN, "") as? String
        } ?: ""
    }

}