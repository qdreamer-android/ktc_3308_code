package com.pwong.uiframe.net

import io.reactivex.disposables.Disposable

interface ApiDisposable {

    fun onDisposable(disposable: Disposable)

}