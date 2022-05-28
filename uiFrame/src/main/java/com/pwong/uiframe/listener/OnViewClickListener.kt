package com.pwong.uiframe.listener

import android.view.View
import com.pwong.library.utils.FastClickUtil

/**
 * @author android
 * @date 20-5-5
 */
interface OnViewClickListener {

    fun onClick(v: View) {
        if (FastClickUtil.isNotFastClick()) {
            onFastClick(v)
        }
    }

    fun onFastClick(v: View)

}