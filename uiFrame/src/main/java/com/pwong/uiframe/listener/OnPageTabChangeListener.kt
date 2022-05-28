package com.pwong.uiframe.listener

import androidx.viewpager.widget.ViewPager

/**
 * @author android
 * @date 2019/4/4
 */
interface OnPageTabChangeListener: ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

}