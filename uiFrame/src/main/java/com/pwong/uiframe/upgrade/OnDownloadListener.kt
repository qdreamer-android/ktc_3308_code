package com.pwong.uiframe.upgrade

interface OnDownloadListener {

    fun onSuccess()

    fun onFailed()

    fun onDownloadStatus(status: Int) {}

    fun onProgress(progress: Int)

}