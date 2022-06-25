package com.pwong.uiframe.base

import androidx.databinding.ObservableBoolean

open class FListViewModel(refreshEnable: Boolean = true) : FViewModel() {
    val refreshEnable = ObservableBoolean(refreshEnable)
    val updateRefreshEnable = ObservableBoolean(true)
}