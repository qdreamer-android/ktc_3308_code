package com.pwong.uiframe.widget

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView

class JWebView : WebView {

    companion object {
        private fun getFixedContext(context: Context): Context {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                context.createConfigurationContext(Configuration())
            } else {
                context
            }
        }
    }

    constructor(context: Context) : super(getFixedContext(context))

    constructor(context: Context, attrs: AttributeSet) : super(getFixedContext(context), attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(getFixedContext(context), attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(getFixedContext(context), attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, privateBrowsing: Boolean)
            : super(getFixedContext(context), attrs, defStyleAttr, privateBrowsing)
}
