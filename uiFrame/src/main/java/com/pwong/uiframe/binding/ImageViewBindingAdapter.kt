package com.pwong.uiframe.binding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.pwong.library.utils.ImgLoadUtil
import com.pwong.uiframe.R

/**
 * @author android
 * @date 2019/4/4
 */
object ImageViewBindingAdapter {

    @BindingAdapter(value = ["avatar"], requireAll = false)
    @JvmStatic
    fun onNormalImgBinding(imgView: ImageView, avatar: String?) {
        ImgLoadUtil.loadCircleImg(imgView.context, avatar, R.drawable.ic_default_avatar, imgView)
    }

    @BindingAdapter(value = ["iconRes"], requireAll = false)
    @JvmStatic
    fun onNormalImgBinding(imgView: ImageView, @DrawableRes iconRes: Int = 0) {
        if (iconRes != 0) {
            imgView.setImageResource(iconRes)
        }
    }

    @BindingAdapter(value = ["imgUrl", "imgBitmap", "imgHolder"], requireAll = false)
    @JvmStatic
    fun onNormalImgBinding(imgView: ImageView, imgUrl: String?, bitmap: Bitmap?, placeHolder: Drawable?) {
        if (placeHolder != null) {
            if (bitmap != null) {
                ImgLoadUtil.loadImg(imgView.context, bitmap, placeHolder, imgView)
            } else {
                ImgLoadUtil.loadImg(imgView.context, imgUrl, placeHolder, imgView)
            }
        } else {
            if (bitmap != null) {
                ImgLoadUtil.loadImg(imgView.context, bitmap, imgView)
            } else {
                ImgLoadUtil.loadImg(imgView.context, imgUrl, imgView)
            }
        }
    }

    @BindingAdapter(value = ["circleUrl", "circleBitmap", "circleHolder"], requireAll = false)
    @JvmStatic
    fun onCircleImgBinding(imgView: ImageView, circleUrl: String?, bitmap: Bitmap?, placeHolder: Drawable?) {
        if (placeHolder != null) {
            if (bitmap != null) {
                ImgLoadUtil.loadCircleImg(imgView.context, bitmap, placeHolder, imgView)
            } else {
                ImgLoadUtil.loadCircleImg(imgView.context, circleUrl, placeHolder, imgView)
            }
        } else {
            if (bitmap != null) {
                ImgLoadUtil.loadCircleImg(imgView.context, bitmap, imgView)
            } else {
                ImgLoadUtil.loadCircleImg(imgView.context, circleUrl, imgView)
            }
        }
    }

    @BindingAdapter(value = ["blurUrl"], requireAll = false)
    @JvmStatic
    fun onBlurImgBinding(imgView: ImageView, blurUrl: String?) {
        ImgLoadUtil.gaussianBlur(imgView.context, blurUrl, imgView)
    }
}