package com.pwong.uiframe.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import com.pwong.uiframe.listener.OnPageTabChangeListener

object ViewPagerBindingAdapter {

    @BindingAdapter(value = ["currentItem", "pageSelectedListener"], requireAll = false)
    @JvmStatic
    fun onAdapterBinding(view: ViewPager, currentItem: Int, listener: OnPageTabChangeListener?) {
        view.currentItem = currentItem
        if (listener != null) {
            view.addOnPageChangeListener(listener)
        }
    }

}