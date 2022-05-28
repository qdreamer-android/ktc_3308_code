package com.pwong.uiframe.view

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.pwong.library.utils.WaveUtils
import com.pwong.uiframe.base.BaseActivity
import com.pwong.uiframe.base.BaseViewModel
import com.pwong.uiframe.utils.ToastUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

/**
 * @Author: Pen
 * @Create: 2022-01-04 11:57:43
 * @Email:  Rocking@7189.com
 */
@RuntimePermissions
abstract class VoiceActivity<DB : ViewDataBinding, VM : BaseViewModel> : BaseActivity<DB, VM>() {

    protected open val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
    protected open var AUDIO_SAMPLE = 16000
    protected open var AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO // AudioFormat.CHANNEL_IN_STEREO
    protected open val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    private var mAudioRecord: AudioRecord? = null
    private var mBufferSize: Int = 0

    private var mAudioReadJob: Job? = null

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    open fun initAudioRecord() {
        mBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE, AUDIO_CHANNEL, AUDIO_FORMAT)
        mAudioRecord = AudioRecord(AUDIO_SOURCE, AUDIO_SAMPLE, AUDIO_CHANNEL, AUDIO_FORMAT, mBufferSize)
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    fun onDeniedAudioRecord() {
        ToastUtil.showShortToast(this, "没有录音权限，拜拜~")
        finish()
    }

    protected fun isInitAudioRecord(): Boolean {
        return mAudioRecord != null && mAudioRecord!!.state == AudioRecord.STATE_INITIALIZED
    }

    protected open fun startRecording() {
        if (mAudioRecord!!.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord!!.startRecording()
        }
    }

    protected open fun stopRecording() {
        if (mAudioRecord!!.recordingState != AudioRecord.RECORDSTATE_STOPPED) {
            mAudioRecord!!.stop()
        }
        mAudioReadJob?.cancel()
    }

    protected fun releaseRecord() {
        stopRecording()
        mAudioRecord?.release()
        mAudioRecord = null
    }

    protected fun readRecording() {
        mAudioReadJob?.cancel()
        mAudioReadJob = GlobalScope.launch(Dispatchers.IO) {
            while (mAudioRecord != null && mAudioRecord!!.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val buffer = ByteArray(mBufferSize)
                val ret = mAudioRecord!!.read(buffer, 0, mBufferSize)
                if (ret > 0) {
                    if (ret == mBufferSize) {
                        onRecordAudio(buffer)
                    } else {
                        onRecordAudio(buffer.copyOfRange(0, ret))
                    }
                } else {
                    Log.e(this@VoiceActivity.javaClass.simpleName, "录音失败 ---- $ret")
                }
            }
        }
    }

    private fun getChannelCount(): Short {
        return if (AUDIO_CHANNEL == AudioFormat.CHANNEL_IN_MONO) {
            1
        } else {
            2
        }
    }

    protected fun pcmAudio2Wav(pcmPath: String): String? {
        return try {
            val wavPath = pcmPath.replace(".pcm", ".wav")
            WaveUtils.pcm2Wav(wavPath, pcmPath, AUDIO_SAMPLE, getChannelCount())
            wavPath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    protected abstract fun onRecordAudio(audio: ByteArray)

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onDestroy() {
        releaseRecord()
        super.onDestroy()
    }

}