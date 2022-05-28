package com.pwong.uiframe.net

import com.pwong.library.utils.JsonHelper
import com.pwong.library.utils.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author android
 * @date 19-10-6
 */
open class AbsRepository {

    fun <T> subApi(observable: Observable<T>, callback: ApiCallback<T>? = null): Disposable {
        return observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtil.logE("NetApi", "onSuccess： ${JsonHelper.toJson(it)}")
                callback?.onSuccess(it)
            }, {
                LogUtil.logE("NetApi", "onError： $it")
                callback?.onError(it)
            })
    }

}