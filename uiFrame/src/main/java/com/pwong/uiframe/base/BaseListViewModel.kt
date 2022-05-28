package com.pwong.uiframe.base

import androidx.databinding.ObservableBoolean

/**
 * @author android
 * @date 20-5-9
 * @instruction fucking bugs
 */
open class BaseListViewModel(
    title: String? = "",    // 标题
    showStatus: Boolean = !title.isNullOrEmpty(),
    isBlackTitle: Boolean = false,    // 标题颜色是否是黑色，黑色时背景为白色，白色时背景为主题色
    refreshEnable: Boolean = true
) : BaseViewModel(title, showStatus, isBlackTitle) {
    val refreshEnable = ObservableBoolean(refreshEnable)
    val updateRefreshEnable = ObservableBoolean(true)
}