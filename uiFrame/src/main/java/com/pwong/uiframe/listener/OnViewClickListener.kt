package com.pwong.uiframe.listener

import android.view.View
import com.pwong.library.utils.FastClickUtil

interface OnViewClickListener {

    fun onClick(v: View) {
        if (FastClickUtil.isNotFastClick()) {
            onFastClick(v)
        }
    }

    fun onFastClick(v: View)

}