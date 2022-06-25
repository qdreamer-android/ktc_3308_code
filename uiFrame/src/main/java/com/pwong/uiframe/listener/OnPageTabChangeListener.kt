package com.pwong.uiframe.listener

import androidx.viewpager.widget.ViewPager

interface OnPageTabChangeListener: ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

}