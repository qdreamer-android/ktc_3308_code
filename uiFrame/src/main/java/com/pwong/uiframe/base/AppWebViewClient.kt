package com.pwong.uiframe.base

import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * @author android
 * @date 20-5-29
 */
class AppWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        webView?.loadUrl(url)
        return true
    }
}