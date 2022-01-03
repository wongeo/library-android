package com.feng.media;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 播放状态
 */
public class PlayerState {

    @IntDef({STOPED, PREPARING, PREPARED, PLAYING, PAUSED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public static final int STOPED = 0;
    public static final int PREPARING = 1;
    public static final int PREPARED = 2;
    public static final int PLAYING = 3;
    public static final int PAUSED = 4;
}
