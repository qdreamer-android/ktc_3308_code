package com.pwong.uiframe.view

import androidx.databinding.ObservableField
import com.pwong.uiframe.base.BaseViewModel

/**
 * @author android
 * @date 20-5-7
 * @instruction fucking bugs
 */
class CrashViewModel : BaseViewModel("崩溃日志") {

    val content = ObservableField<String>()

}