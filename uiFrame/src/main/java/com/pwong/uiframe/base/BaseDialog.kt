package com.pwong.uiframe.base

import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.pwong.uiframe.R


/**
 * @author android
 * @date 2019/4/10
 */
abstract class BaseDialog<DB : ViewDataBinding>(
    context: Context, @LayoutRes layoutId: Int,
    gravity: Int = Gravity.CENTER
) : AlertDialog(context, R.style.DialogTheme) {

    var mBinding: DB? = null
        private set

    protected abstract val isFullWidth: Boolean

    protected open val isFullHeight = false

    init {
        val convertView = LayoutInflater.from(context).inflate(layoutId, null)
        mBinding = DataBindingUtil.bind(convertView)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(gravity)
        when (gravity) {
            Gravity.BOTTOM -> {
                window?.setWindowAnimations(R.style.BottomDialogAnimation)
            }
            Gravity.CENTER -> {
                window?.setWindowAnimations(R.style.CenterDialogAnimation)
            }
        }
        window?.decorView?.setPadding(0, 0, 0, 0)
        val lp = window?.attributes
        lp?.width = if (isFullWidth) {
            WindowManager.LayoutParams.MATCH_PARENT
        } else {
            WindowManager.LayoutParams.WRAP_CONTENT
        }
        lp?.height = if (isFullHeight) {
            WindowManager.LayoutParams.MATCH_PARENT
        } else {
            WindowManager.LayoutParams.WRAP_CONTENT
        }
        window?.attributes = lp
        setView(convertView)
        initDialog()
    }

    protected abstract fun initDialog()

    fun setCanceledOnTouchBack(cancel: Boolean) {
        setOnKeyListener { _, keyCode, _ ->
            if (!cancel) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

}