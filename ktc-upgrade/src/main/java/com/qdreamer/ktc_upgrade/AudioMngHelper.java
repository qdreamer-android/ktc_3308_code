package com.qdreamer.ktc_upgrade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;

public class AudioMngHelper {

    @SuppressLint("StaticFieldLeak")
    private static AudioMngHelper mHelper;

    private AudioManager mAudioManager;
    private int mProgress = -1;

    private AudioMngHelper(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static AudioMngHelper getInstance(Context context) {
        if (mHelper == null) {
            synchronized (AudioMngHelper.class) {
                if (mHelper == null) {
                    mHelper = new AudioMngHelper(context);
                }
            }
        }
        return mHelper;
    }

    public void updateVolume(int progress) {
        mProgress = progress;
        if (mProgress < 0 || mAudioManager == null) return;
        updateSystemVolume();
        updateTipVolume();
        updateMediaVolume();
        updateCallVolume();   // 如果蓝牙打开的情况下修改通话音量，会弹出系统的 蓝牙已停止运行 弹窗
//        updateRingVolume();
    }

    public int getMaxVolume() {
        if (mAudioManager == null) return 0;
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    public int getCurrentVolume() {
        if (mAudioManager == null) return 0;
        return mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * 修改系统音量
     */
    private void updateSystemVolume() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
//        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        int volume = (int) Math.ceil((mProgress) * maxVolume * 0.01);
        volume = volume <= 0 ? 0 : volume;
        volume = volume >= maxVolume ? maxVolume : volume;
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 修改通话音量
     */
    private void updateCallVolume() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
//        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int volume = (int) Math.ceil((mProgress) * maxVolume * 0.01);
        volume = volume <= 0 ? 0 : volume;
        volume = volume >= maxVolume ? maxVolume : volume;
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 修改媒体音量
     */
    private void updateMediaVolume() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int) Math.ceil((mProgress) * maxVolume * 0.01);
        volume = volume <= 0 ? 0 : volume;
        volume = volume >= maxVolume ? maxVolume : volume;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 修改铃声音量
     */
    private void updateRingVolume() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        int volume = (int) Math.ceil((mProgress) * maxVolume * 0.01);
        volume = volume <= 0 ? 0 : volume;
        volume = volume >= maxVolume ? maxVolume : volume;
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 修改提示音量
     */
    private void updateTipVolume() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
//        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int volume = (int) Math.ceil((mProgress) * maxVolume * 0.01);
        volume = volume <= 0 ? 0 : volume;
        volume = volume >= maxVolume ? maxVolume : volume;
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}