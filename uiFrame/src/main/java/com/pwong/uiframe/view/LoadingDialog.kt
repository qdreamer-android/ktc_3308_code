package com.pwong.uiframe.view

import android.content.Context
import androidx.databinding.ObservableField
import com.pwong.uiframe.R
import com.pwong.uiframe.base.BaseDialog
import com.pwong.uiframe.databinding.DialogLoadingBinding

/**
 * @author android
 * @date 20-5-7
 * @instruction fucking bugs
 */
class LoadingDialog(context: Context, override val isFullWidth: Boolean = false) :
    BaseDialog<DialogLoadingBinding>(context, R.layout.dialog_loading) {

    val loadingDesc = ObservableField<String>()

    override fun initDialog() {
        mBinding?.viewModel = this
        setCanceledOnTouchOutside(false)
    }

    fun setLoadingDesc(desc: String) {
        loadingDesc.set(desc)
    }

}