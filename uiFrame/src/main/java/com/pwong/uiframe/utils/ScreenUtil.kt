package com.pwong.uiframe.utils

import android.annotation.SuppressLint
import android.content.Context

object ScreenUtil {

    @SuppressLint("PrivateApi")
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        try {
            Class.forName("com.android.internal.R\$dimen")?.let { clz ->
                val obj = clz.newInstance()
                val field = clz.getField("status_bar_height")
                field.isAccessible = true
                val x = field.get(obj)?.toString()?.toIntOrNull() ?: 0
                statusBarHeight = context.resources.getDimensionPixelSize(x)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusBarHeight
    }

}