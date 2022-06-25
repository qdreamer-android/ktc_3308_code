package com.pwong.library.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process


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