package com.pwong.uiframe.base

import androidx.databinding.ObservableBoolean

/**
 * @author android
 * @date 20-5-9
 * @instruction fucking bugs
 */
open class FListViewModel(refreshEnable: Boolean = true) : FViewModel() {
    val refreshEnable = ObservableBoolean(refreshEnable)
    val updateRefreshEnable = ObservableBoolean(true)
}