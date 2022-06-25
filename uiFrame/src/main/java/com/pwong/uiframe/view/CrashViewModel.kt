package com.pwong.uiframe.view

import androidx.databinding.ObservableField
import com.pwong.uiframe.base.BaseViewModel

class CrashViewModel : BaseViewModel("崩溃日志") {

    val content = ObservableField<String>()

}