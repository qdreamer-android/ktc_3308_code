package com.pwong.uiframe.binding

import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter


object TextViewBindingAdapter {


    @BindingAdapter(value = ["isBold", "isItalic"], requireAll = false)
    @JvmStatic
    fun onTextStyleBinding(view: TextView, isBold: Boolean = false, isItalic: Boolean = false) {
        view.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        if (isBold) {
            view.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }

        if (isItalic) {
            view.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
        }
    }

    @BindingAdapter(value = ["iconStart", "iconEnd", "iconTop", "iconBottom"], requireAll = false)
    @JvmStatic
    fun onTextIconBinding(
        txtView: TextView,
        @DrawableRes iconStart: Int? = 0,
        @DrawableRes iconEnd: Int? = 0,
        @DrawableRes iconTop: Int? = 0,
        @DrawableRes iconBottom: Int? = 0
    ) {
        val ds = if (iconStart ?: 0 == 0) null else ContextCompat.getDrawable(txtView.context, iconStart!!)
        val de = if (iconEnd ?: 0 == 0) null else ContextCompat.getDrawable(txtView.context, iconEnd!!)
        val dt = if (iconTop ?: 0 == 0) null else ContextCompat.getDrawable(txtView.context, iconTop!!)
        val db = if (iconBottom ?: 0 == 0) null else ContextCompat.getDrawable(txtView.context, iconBottom!!)
        ds?.setBounds(0, 0, ds.minimumWidth, ds.minimumHeight)
        de?.setBounds(0, 0, de.minimumWidth, de.minimumHeight)
        dt?.setBounds(0, 0, dt.minimumWidth, dt.minimumHeight)
        db?.setBounds(0, 0, db.minimumWidth, db.minimumHeight)
        txtView.setCompoundDrawables(ds, dt, de, db)
    }

}