package com.pwong.library.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process


/**
 * @author android
 * @date 2020/6/3
 * @instruction fucking bugs
 */
object ApplicationUtil {

    fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.runningAppProcesses?.forEach {
                if (it.pid == pid) {
                    return it.processName
                }
        }
        return null
    }

}