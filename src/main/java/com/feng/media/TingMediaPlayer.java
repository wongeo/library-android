package com.feng.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.feng.parallel.TaskTimer;

/**
 * 自定义媒体播放器
 *
 * @author WangJing
 */
public class TingMediaPlayer extends MediaPlayer {

    private IPlayStateCallback mIPlayStateCallback;
    private TaskTimer mTaskTimer;
    private int mPlayerState;
    private int mPosition;
    private final Object mStateLocker = new Object();
    private String mPath;

    /**
     * 用于保持WIFI唤醒
     */
    private WifiLock mWifiLock;

    public TingMediaPlayer() {
        super();
        setOnPreparedListener(mOnPreparedListener);
        setOnCompletionListener(mOnCompletionListener);
        setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        setOnErrorListener(mOnErrorListener);
    }

    public void regePlayStateCallback(IPlayStateCallback callback) {
        mIPlayStateCallback = callback;
        mTaskTimer = new TaskTimer();
        mTaskTimer.setInterval(1000);
        mTaskTimer.setOnTickedHandler(new Runnable() {
            @Override
            public void run() {
                if (isPlaying() && mPositionChanged != null) {
                    mPositionChanged.run();
                }
            }
        });
        mTaskTimer.start();
    }

    public void play(String path) {
        play(path, 0);
    }

    public void play(String path, int position) {
        mPosition = position;
        if (isPlaying() && path != null && path.equalsIgnoreCase(mPath)) {
            seekTo(position);
            return;
        }

        mPath = path;
        try {

            reset();
            setDataSource(path);
            setAudioStreamType(AudioManager.STREAM_MUSIC);
            prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        super.seekTo(msec);
        synchronized (mStateLocker) {
            notifyPlayerStateChanged(PlayerState.PLAYING);
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        synchronized (mStateLocker) {
            notifyPlayerStateChanged(PlayerState.PAUSED);
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        synchronized (mStateLocker) {
            notifyPlayerStateChanged(PlayerState.PAUSED);
        }
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        synchronized (mStateLocker) {
            notifyPlayerStateChanged(PlayerState.PREPARING);
        }
    }

    public int getPlayerState() {
        return mPlayerState;
    }

    private void notifyPlayerStateChanged(int playerState) {
        mPlayerState = playerState;
        if (mIPlayStateCallback != null) {
            mIPlayStateCallback.onPlayerStateChanged(playerState);
        }
    }

    private final OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mIPlayStateCallback != null) {
                int duration = mp.getDuration();
                mIPlayStateCallback.onPrepared(duration);
            }
            if (mPosition != 0) {
                seekTo(mPosition);
            }
            try {
                mp.start();
            } catch (Exception e) {
            }
        }
    };

    private final OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mIPlayStateCallback != null) {
                mIPlayStateCallback.onCompletion();
            }

        }
    };

    private final OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mIPlayStateCallback != null) {
                mIPlayStateCallback.onBufferingProgressChanged(percent);
            }
        }
    };

    private final OnErrorListener mOnErrorListener = new OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };

    private final Runnable mPositionChanged = new Runnable() {

        @Override
        public void run() {
            if (mIPlayStateCallback == null) {
                return;
            }
            int position = getCurrentPosition();
            int duration = getDuration();
            float percent = position * 100f / duration;
            mIPlayStateCallback.onPlayPositionChanged(percent, position, duration);
        }
    };

    /**
     * 保持WIFI唤醒
     *
     * @param context
     */
    public void aquirePlayerWifiLocker(Context context) {
        if (mWifiLock == null) {
            mWifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "zyTrPlayWifiLock");
        }
        if (mWifiLock != null && !mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public synchronized void releasePlayerWakeLocker() {
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
            mWifiLock = null;
        }
    }
}
