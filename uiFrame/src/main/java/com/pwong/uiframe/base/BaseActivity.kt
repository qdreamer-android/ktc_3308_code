package com.pwong.uiframe.base

import android.content.DialogInterface
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.pwong.library.broadcast.NetBroadcast
import com.pwong.library.utils.LogUtil
import com.pwong.library.utils.NetworkUtil
import com.pwong.uiframe.BR
import com.pwong.uiframe.R
import com.pwong.uiframe.databinding.AbsActivityBaseBinding
import com.pwong.uiframe.expand.hideNavigationBar
import com.pwong.uiframe.expand.initStatusBar
import com.pwong.uiframe.expand.setStatusBarLightMode
import com.pwong.uiframe.expand.transparentNavigationBar
import com.pwong.uiframe.utils.ScreenUtil
import com.pwong.uiframe.utils.TUtil
import com.pwong.uiframe.view.LoadingDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.abs_activity_base.*
import kotlinx.android.synthetic.main.abs_page_container.*
import java.util.concurrent.TimeUnit

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(),
    DialogInterface.OnDismissListener{

    private lateinit var mRootBinding: AbsActivityBaseBinding

    private var mBinding: DB? = null
    private val mViewModel by lazy {
        ViewModelProvider(this).get(TUtil.getT<VM>(this, 1)!!::class.java)
    }

    private var mKeyboardShowing = false
    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    private var mNetBroad: NetBroadcast? = null

    private var mNavigationHideDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val unUseTitle = preInit()
        setStatusBarLightMode(mViewModel.isBlackTitle.get())
        initStatusBar()
        mRootBinding = DataBindingUtil.setContentView(this, R.layout.abs_activity_base)
        mRootBinding.viewModel = mViewModel
        mBinding = DataBindingUtil.bind(View.inflate(this, layoutView(), null))
        mBinding!!.setVariable(BR.viewModel, mViewModel)
        if (!unUseTitle) {   // 使用默认的 Title
            viewStubContainer.inflate()
            initStatusBarHeight(statusBar)
            addTitleClick(titleBar)
            container?.addView(mBinding!!.root, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            ))
        } else {
            kpsRoot?.addView(mBinding!!.root, 0, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            ))
        }

        registerBroad()

        KeyboardUtil.attach(this, kpsPanel) {
            mKeyboardShowing = it
        }
        kpsRoot?.getChildAt(0)?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (mKeyboardShowing) {
                    KeyboardUtil.hideKeyboard(v)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        initView()
    }

    protected fun setNavigationIcon(icon: Drawable?) {
        titleBar?.navigationIcon = icon
    }

    protected fun setNavigationIcon(@DrawableRes res: Int) {
        titleBar?.setNavigationIcon(res)
    }

    override fun onResume() {
        super.onResume()
        mNavigationHideDisposable?.dispose()
        mNavigationHideDisposable = Observable.interval(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideNavigationBar()
                transparentNavigationBar()
            }
        hideNavigationBar()
        transparentNavigationBar()
    }

    override fun onStop() {
        super.onStop()
        mNavigationHideDisposable?.dispose()
    }

    fun checkKeyboardShowing(): Boolean {
        if (mKeyboardShowing) {
            KeyboardUtil.hideKeyboard(mRootBinding.root)
            return true
        }
        return false
    }

    protected fun initStatusBarHeight(statusBar: View?) {
        val lp = statusBar?.layoutParams
        lp?.height = ScreenUtil.getStatusBarHeight(this)
        statusBar?.layoutParams = lp
    }

    protected fun addTitleClick(titleBar: Toolbar?) {
        setSupportActionBar(titleBar)
        titleBar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    protected fun getBinding() = mBinding

    protected fun getViewModel() = mViewModel

    protected open fun preInit() : Boolean {
        return false
    }

    @LayoutRes
    protected abstract fun layoutView(): Int

    protected abstract fun initView()

    private fun registerBroad() {
        if (mNetBroad == null) {
            mNetBroad = NetBroadcast(this)
        }
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        mNetBroad!!.setOnNetStateListener {
            onNetStateChange(it)
        }
        registerReceiver(mNetBroad, filter)
    }

    protected open fun onNetStateChange(state: NetworkUtil.WifiStatus) {
        LogUtil.logI("network connect state changed:  ${this.javaClass.simpleName} -> $state")
    }

    protected fun showLoading(tips: String? = "正在加载中...", cancel: Boolean = true, dismissFinish: Boolean = false) {
        if (!isDestroyed) {
            mLoadingDialog.setLoadingDesc(tips ?: "")
            mLoadingDialog.setCanceledOnTouchBack(cancel)
            if (cancel && dismissFinish) {
                mLoadingDialog.setOnDismissListener(this)
            } else {
                mLoadingDialog.setOnDismissListener(null)
            }
            mLoadingDialog.show()
        }
    }

    protected fun hideLoading() {
        mLoadingDialog.setOnDismissListener(null)
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.cancel()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        onBackPressed()
    }

    override fun onDestroy() {
        hideLoading()
        try {
            mNetBroad?.let {
                unregisterReceiver(mNetBroad)
                mNetBroad = null
            }
        } catch (e: Exception) {

        }
        super.onDestroy()
    }

}