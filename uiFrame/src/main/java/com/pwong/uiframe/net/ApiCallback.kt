package com.pwong.uiframe.net

interface ApiCallback<T> {

    fun onSuccess(resp: T)

    fun onError(error: Throwable)

}