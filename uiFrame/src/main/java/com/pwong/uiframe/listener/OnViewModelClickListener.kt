package com.pwong.uiframe.listener

import android.view.View
import com.pwong.library.utils.FastClickUtil
import com.pwong.uiframe.base.IViewModel

/**
 * @author android
 * @date 2019/4/5
 */
interface OnViewModelClickListener<VM : IViewModel> {

    fun onClick(v: View, model: VM) {
        if (FastClickUtil.isNotFastClick()) {
            onFastClick(v, model)
        }
    }

    fun onFastClick(v: View, model: VM)

}