package com.pwong.uiframe.net

/**
 * @author android
 * @date 2019/4/19
 */
interface ApiCallback<T> {

    fun onSuccess(resp: T)

    fun onError(error: Throwable)

}