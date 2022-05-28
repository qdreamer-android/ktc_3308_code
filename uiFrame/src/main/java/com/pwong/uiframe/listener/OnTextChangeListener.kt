package com.pwong.uiframe.listener

import android.text.Editable
import android.text.TextWatcher

/**
 * @author android
 * @date 2018/12/14
 */
interface OnTextChangeListener : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged(s)
    }

    fun onTextChanged(s: CharSequence?)
}