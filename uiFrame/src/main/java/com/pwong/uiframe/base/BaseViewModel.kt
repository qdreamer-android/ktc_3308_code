package com.pwong.uiframe.base

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

/**
 * @author android
 * @date 20-5-5
 */
open class BaseViewModel(
    title: String? = "",    // 标题
    showStatus: Boolean = true,
    isBlackTitle: Boolean = false    // 标题颜色是否是黑色，黑色时背景为白色，白色时背景为主题色
) : ViewModel() {

    val title = ObservableField(title)
    val showStatus = ObservableBoolean(showStatus)
    val isBlackTitle = ObservableBoolean(isBlackTitle)

}