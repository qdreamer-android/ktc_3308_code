package com.pwong.uiframe.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/**
 * @author android
 * @date 19-11-26
 */
class NoSlideViewPager : ViewPager {

    var isCanSlide = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {}

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isCanSlide && super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isCanSlide && super.onTouchEvent(ev)
    }

    fun setSlideEnable(enable: Boolean) {
        isCanSlide = enable
    }

}