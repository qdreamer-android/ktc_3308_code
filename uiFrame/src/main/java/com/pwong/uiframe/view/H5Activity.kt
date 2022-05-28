package com.pwong.uiframe.view

import android.content.Context
import android.content.Intent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.pwong.uiframe.R
import com.pwong.uiframe.base.BaseActivity
import com.pwong.uiframe.expand.initWebSettings
import com.pwong.uiframe.databinding.ActivityH5Binding
import kotlinx.android.synthetic.main.activity_h5.*

/**
 * @author android
 * @date 20-5-29
 */
class H5Activity : BaseActivity<ActivityH5Binding, H5ViewModel>() {

    companion object {
        private const val KEY_H5_URL = "h5Url"
        private const val KEY_H5_TITLE = "h5Title"

        fun action(context: Context?, h5Url: String?, title: String? = "") {
            if (context == null || h5Url.isNullOrBlank()) return
            val intent = Intent(context, H5Activity::class.java)
            intent.putExtra(KEY_H5_TITLE, title)
            intent.putExtra(KEY_H5_URL, h5Url)
            context.startActivity(intent)
        }
    }

    override fun layoutView() = R.layout.activity_h5

    override fun preInit(): Boolean {
        return true
    }

    override fun initView() {
        initStatusBarHeight(vStatusH5)
        setSupportActionBar(tlbH5)
        tlbH5?.setNavigationOnClickListener {
            onBackPressed()
        }
        getViewModel().webTitle.set(intent?.getStringExtra(KEY_H5_TITLE) ?: "")
        getViewModel().h5Url = intent?.getStringExtra(KEY_H5_URL) ?: ""
        wbvH5?.initWebSettings(mChromeClient)
        wbvH5?.loadUrl(getViewModel().h5Url)
    }

    private val mChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            val progress = view?.progress!!
            getViewModel().curProgress.set(progress)
            if (progress > 90) {
                pgbH5?.visibility = View.GONE
            } else {
                pgbH5?.visibility = View.VISIBLE
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            getViewModel().webTitle.set(title)
        }
    }

    override fun onBackPressed() {
        if (wbvH5?.canGoBack() == true) {
            wbvH5?.goBack()
        } else {
            wbvH5?.destroy()
            super.onBackPressed()
        }
    }

}