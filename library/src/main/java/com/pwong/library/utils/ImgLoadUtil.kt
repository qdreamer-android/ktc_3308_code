package com.pwong.library.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.lang.reflect.InvocationTargetException

object ImgLoadUtil {

    fun loadImg(context: Context?, url: String?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .into(imgView)
    }

    fun loadImg(context: Context?, url: String?, @DrawableRes placeHolder: Int, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.placeholderOf(placeHolder))
            .into(imgView)
    }

    fun loadImg(context: Context?, url: String?, placeHolder: Drawable?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.placeholderOf(placeHolder))
            .into(imgView)
    }

    fun loadImg(context: Context?, @DrawableRes imgRes: Int, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(imgRes)
            .into(imgView)
    }

    fun loadImg(context: Context?, bitmap: Bitmap?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .into(imgView)
    }

    fun loadImg(context: Context?, bitmap: Bitmap?, @DrawableRes placeHolder: Int, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions.placeholderOf(placeHolder))
            .into(imgView)
    }

    fun loadImg(context: Context?, bitmap: Bitmap?, placeHolder: Drawable?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions.placeholderOf(placeHolder))
            .into(imgView)
    }

    fun loadCircleImg(context: Context?, url: String?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .into(imgView)
    }

    fun loadCircleImg(context: Context?, url: String?, @DrawableRes placeHolder: Int, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions
                .placeholderOf(placeHolder)
                .circleCrop()
            ).into(imgView)
    }

    fun loadCircleImg(context: Context?, url: String?, placeHolder: Drawable?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions
                .placeholderOf(placeHolder)
                .circleCrop()
            ).into(imgView)
    }

    fun loadCircleImg(context: Context?, bitmap: Bitmap?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions.circleCropTransform())
            .into(imgView)
    }

    fun loadCircleImg(context: Context?, bitmap: Bitmap?, @DrawableRes placeHolder: Int, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions
                .placeholderOf(placeHolder)
                .circleCrop()
            ).into(imgView)
    }

    fun loadCircleImg(context: Context?, bitmap: Bitmap?, placeHolder: Drawable?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions
                .placeholderOf(placeHolder)
                .circleCrop()
            ).into(imgView)
    }

    fun loadImgAsBitmap(context: Context?, url: String?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(imgView)
    }

    fun gaussianBlur(context: Context?, url: String?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.bitmapTransform(GlideBlurTransformation(context)))
            .into(imgView)
    }

    fun gaussianBlur(context: Context?, bitmap: Bitmap?, imgView: ImageView?) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .load(bitmap)
            .apply(RequestOptions.bitmapTransform(GlideBlurTransformation(context)))
            .into(imgView)
    }

    fun loadOneGif(context: Context?, model: Any?, imgView: ImageView?, listener: OnGifCompleteListener? = null) {
        if (context == null || imgView == null || (context is Activity) && context.isDestroyed) return
        Glide.with(context)
            .asGif()
            .load(model)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                    listener?.onError()
                    return false
                }

                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    try {
                        val gifStateField = GifDrawable::class.java.getDeclaredField("state")
                        gifStateField.isAccessible = true
                        val gifStateClass =
                            Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable\$GifState")
                        val gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader")
                        gifFrameLoaderField.isAccessible = true
                        val gifFrameLoaderClass =
                            Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader")
                        val gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder")
                        gifDecoderField.isAccessible = true
                        val gifDecoderClass =
                            Class.forName("com.bumptech.glide.gifdecoder.GifDecoder")
                        val gifDecoder =
                            gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)))
                        val getDelayMethod = gifDecoderClass.getDeclaredMethod(
                            "getDelay",
                            Int::class.javaPrimitiveType
                        )
                        getDelayMethod.isAccessible = true
                        ////设置播放次数
                        resource?.setLoopCount(1)
                        //获得总帧数
                        val count = resource?.frameCount ?: 0
                        var delay: Long = 0
                        for (i in 0 until count) {
                            //计算每一帧所需要的时间进行累加
                            delay += getDelayMethod.invoke(gifDecoder, i) as Int
                        }
                        imgView.postDelayed({
                            listener?.onComplete()
                        }, delay)
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return false
                }
            }).into(imgView)
    }

}