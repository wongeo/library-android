package com.feng.media;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by feng on 2017/11/10.
 */

public class VideoMediaPlayer extends MediaPlayer {

    private static final int VIDEO_PROGRESS = 1;

    private int mPlayerState;

    private IPlayStateCallback mPlayStateCallback;

    private float mPercent;

    public void play(String path, float percent) {
        try {
            mPercent = percent;
            reset();
            setDataSource(path);
            prepareAsync();
            mProgressHandler.removeMessages(VIDEO_PROGRESS);
            mProgressHandler.sendEmptyMessageDelayed(VIDEO_PROGRESS, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        super.setDisplay(sh);
    }

    public void seekTo(float percent) {
        super.seekTo((int) (getDuration() * percent));
        start();
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        notifyPlayerStateChanged(PlayerState.PLAYING);
        mProgressHandler.sendEmptyMessage(VIDEO_PROGRESS);
    }

    @Override
    public void pause() throws IllegalStateException {
        if (!isPlaying()) {
            stop();
            return;
        }
        super.pause();
        notifyPlayerStateChanged(PlayerState.PAUSED);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        notifyPlayerStateChanged(PlayerState.STOPED);
    }

    public void setPlayStateCallback(IPlayStateCallback callback) {
        mPlayStateCallback = callback;

        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerState = PlayerState.STOPED;
                if (mPlayStateCallback != null) {
                    mPlayStateCallback.onCompletion();
                }
            }
        });
        setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mPlayerState = PlayerState.STOPED;
                if (mPlayStateCallback != null) {
                    mPlayStateCallback.onMediaError(new Exception("what:" + what + " extra:" + extra));
                }
                return false;
            }
        });
        setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayerState = PlayerState.PREPARED;
                if (mPlayStateCallback != null) {
                    mPlayStateCallback.onPrepared(mp.getDuration());
                }
                if (mPercent != 0) {
                    int position = (int) (mp.getDuration() * mPercent);
                    seekTo(position);
                }

                start();
            }
        });
    }


    private synchronized void notifyPlayerStateChanged(int playerState) {
        mPlayerState = playerState;
        if (mPlayStateCallback != null) {
            mPlayStateCallback.onPlayerStateChanged(playerState);
        }
    }

    private Handler mProgressHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_PROGRESS:
                    if (mPlayStateCallback != null && isPlaying()) {
                        long position = getCurrentPosition();
                        long duration = getDuration();
                        mPlayStateCallback.onPlayPositionChanged(position * 1.0f / duration, position, duration);
                        mProgressHandler.sendEmptyMessageDelayed(VIDEO_PROGRESS, 1000);
                    }
                    break;
            }
        }
    };

    public int getPlayerState() {
        return mPlayerState;
    }
}
