package com.pwong.uiframe.net

import io.reactivex.disposables.Disposable

/**
 * @author android
 * @date 20-4-1
 */
interface ApiDisposable {

    fun onDisposable(disposable: Disposable)

}