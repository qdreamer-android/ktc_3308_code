package com.pwong.library.utils

/**
 * @author android
 * @date 20-5-2
 */
object FastClickUtil {
    private var lastClickTime: Long = 0
    private const val INTERVAL = 500

    fun isNotFastClick(): Boolean = !isFastClick()

    fun isNotFastClick(ms: Int) = !isFastClick(ms)

    fun isFastClick(): Boolean =
        isFastClick(INTERVAL)

    fun isFastClick(ms: Int): Boolean {
        if (System.currentTimeMillis() - lastClickTime > ms) {
            lastClickTime = System.currentTimeMillis()
            return false
        }
        return true
    }
}