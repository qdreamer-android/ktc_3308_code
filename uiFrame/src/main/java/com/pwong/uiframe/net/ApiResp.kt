package com.pwong.uiframe.net

data class ApiResp<T>(
    val isSuccess: Boolean = false,
    val data: T? = null,
    val message: String? = ""
)