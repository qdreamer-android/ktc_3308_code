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

    @BindingAdapter(value = ["isVisible"], requireAll = false)
    @JvmStatic
    fun onVisibleBinding(view: View, isVisible: Boolean) {
        if (isVisible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }


    @BindingAdapter(value = ["isSelected"], requireAll = false)
    @JvmStatic
    fun onSelectBinding(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }
}