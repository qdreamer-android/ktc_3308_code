package com.pwong.uiframe.net

/**
 * @author android
 * @date 19-11-11
 */
data class ApiResp<T>(
    val isSuccess: Boolean = false,
    val data: T? = null,
    val message: String? = ""
)