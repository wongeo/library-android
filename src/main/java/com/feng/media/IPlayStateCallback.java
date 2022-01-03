package com.feng.media;

public interface IPlayStateCallback {


    void onMediaError(Exception ex);

    void onBufferingProgressChanged(int percent);

    void onPlayerStateChanged(@PlayerState.Status int newState);

    void onPrepared(int duration);

    void onPlayPositionChanged(float percent, long position, long duration);

    void onCompletion();
}
