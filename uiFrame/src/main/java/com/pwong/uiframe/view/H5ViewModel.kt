package com.pwong.uiframe.view

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.pwong.uiframe.base.BaseViewModel

/**
 * @author android
 * @date 20-5-29
 */
class H5ViewModel : BaseViewModel() {

    var h5Url: String = ""
    val webTitle = ObservableField<String>()
    val curProgress = ObservableInt(0)

}