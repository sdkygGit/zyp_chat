package com.wiz.dev.wiztalk.utils;

import android.media.MediaPlayer;

/**
 * Created by Administrator on 2016/6/30.
 */
public class MyMovieMediaPlayer {

    public static MediaPlayer mMediaPlayer;


    public static MediaPlayer initPlayer() {
        if (mMediaPlayer == null) {
            synchronized (MyMovieMediaPlayer.class) {
                mMediaPlayer = new MediaPlayer();
            }
        }
        return mMediaPlayer;
    }


    public static void reset(){
        mMediaPlayer.reset();
    }

    public static void release(){
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

}
