package com.oldfeel.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by weidingqiang on 15/9/23.
 */
public class MediaManager {
    private static MediaPlayer mMediaPlayer;

    private static boolean isPause;
    private static boolean isError;
    private static boolean isPrepared;

    public static void playSound(Context context, int resId) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer = null;
            return;
        }
        mMediaPlayer = MediaPlayer.create(context, resId);
        mMediaPlayer.start();
    }

    /**
     * 播放音乐
     *
     * @param recordUrl
     * @param onCompletionListener
     */
    public static void playSound(String recordUrl, MediaPlayer.OnCompletionListener onCompletionListener, final OnStopListener onStopListener) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            onStopListener.onStop();
            return;
        }
        onStopListener.onStart();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(recordUrl);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    isPrepared = true;
                    if (!isError) {
                        mMediaPlayer.start();
                    }
                }
            });
            isPrepared = false;
            isError = false;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mMediaPlayer != null && !isPrepared) {
                        isError = true;
                        onStopListener.onError();
                    }
                }
            }, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public interface OnStopListener {
        public void onStart();

        public void onStop();

        public void onError();
    }
}
