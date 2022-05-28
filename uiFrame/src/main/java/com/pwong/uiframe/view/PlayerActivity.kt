package com.pwong.uiframe.view

import android.media.MediaPlayer
import androidx.databinding.ViewDataBinding
import com.pwong.uiframe.base.BaseViewModel
import kotlinx.coroutines.*

/**
 * 播放器 MediaPlayer, 一般 录音机用的多，而播放器和 AudioRecord 搭配使用，因此采用 MediaPlayer 直接继承自 VoiceActivity
 *
 * @Author: Pen
 * @Create: 2022-02-16 14:41:13
 * @Email:  Rocking@7189.com
 */
abstract class PlayerActivity<DB : ViewDataBinding, VM : BaseViewModel> : VoiceActivity<DB, VM>(

), MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    protected val mMediaPlayer: MediaPlayer by lazy {
        MediaPlayer().apply {
            setOnErrorListener(this@PlayerActivity)
            setOnCompletionListener(this@PlayerActivity)
        }
    }

    protected fun startPlayerAsync(resource: String) {
        mMediaPlayer.reset()
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setDataSource(resource)
        mMediaPlayer.prepareAsync()
    }

    protected fun startPlayerSync(resource: String, justPrepare: Boolean = false) {
        mMediaPlayer.reset()
        mMediaPlayer.setDataSource(resource)
        mMediaPlayer.prepare()
        if (!justPrepare) {
            mMediaPlayer.start()
        }
    }

    protected fun startPlayerAssets(name: String, justPrepare: Boolean = false) {
        mMediaPlayer.reset()
        val asd = assets.openFd(name)
        mMediaPlayer.setDataSource(asd.fileDescriptor, asd.startOffset, asd.length)
        mMediaPlayer.prepare()
        if (!justPrepare) {
            mMediaPlayer.start()
        }
    }

    protected fun pausePlayer() {
        if (isPlaying()) {
            mMediaPlayer.pause()
        }
    }

    protected fun startPlayer(position: Int? = null) {
        if (position != null && position >= 0) {
            mMediaPlayer.seekTo(position)
        }
        if (!isPlaying()) {
            mMediaPlayer.start()
        }
    }

    protected fun stopPlayer() {
        if (isPlaying()) {
            mMediaPlayer.stop()
            mMediaPlayer.prepare()  // 为了能够重新继续调用 start 播放
        }
    }

    protected fun isPlaying() = mMediaPlayer.isPlaying

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onDestroy() {
        try {
            stopPlayer()
            mMediaPlayer.reset()
            mMediaPlayer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

}