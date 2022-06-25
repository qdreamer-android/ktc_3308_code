package com.pwong.uiframe.expand

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.pwong.uiframe.base.AppWebViewClient

@SuppressLint("SetJavaScriptEnabled")
fun WebView.initWebSettings(mChromeClient: WebChromeClient? = null) {
    settings?.javaScriptEnabled = true
    settings?.useWideViewPort = true
    settings?.loadWithOverviewMode = true
    settings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
    settings?.domStorageEnabled = true
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    setOnLongClickListener { true }
    isFocusable = false
    requestFocus()
    webChromeClient = mChromeClient
    webViewClient = AppWebViewClient()
}