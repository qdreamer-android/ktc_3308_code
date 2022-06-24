package com.pwong.uiframe.binding

import android.view.View
import androidx.databinding.BindingAdapter

object ViewBindingAdapter {

    @BindingAdapter(value = ["isShow"], requireAll = false)
    @JvmStatic
    fun onShowBinding(view: View, isShow: Boolean) {
        if (isShow) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

}