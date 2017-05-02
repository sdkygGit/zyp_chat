package com.wiz.dev.wiztalk.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.media.*;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.VoiceDb;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.DB.XmppMessageDao;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.SaveConfig;

import java.io.IOException;
import java.util.HashSet;


/**
 * Created by Admin on 2015/8/18.
 */
public class MediaManager {

    public static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static AnimationDrawable anim;//动画

    public static String currentPath;

    public static ImageView animView;

    public static boolean isLeft;

    public static ChatItemSendVoiceView itemViewSend;
    public static ChatItemReceiveVoiceView itemViewRec;

    public static HashSet<ChatItemReceiveVoiceView> currentChatItemReceiveVoiceViews = new HashSet<ChatItemReceiveVoiceView>();
    public static HashSet<ChatItemSendVoiceView> currentChatItemSendVoiceViews = new HashSet<ChatItemSendVoiceView>();


//    static  SaveConfig saveConfig;
//filePath _ic

    public static void initPlayer() {
        if (mMediaPlayer == null) {
            synchronized (MediaManager.class) {
                mMediaPlayer = new MediaPlayer();
//                saveConfig = new SaveConfig(MyApplication.getInstance());
            }
        }
    }

    /**
     * 播放音乐
     *
     * @param filePath
     */
    public static void playSound(final String filePath) {
        currentPath = filePath;
        if (mMediaPlayer == null) {
            initPlayer();

           /* //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });*/
        } else {
            mMediaPlayer.reset();
        }
//        try {



        if (!isLeft) {
            if (itemViewSend != null) {
                animView = itemViewSend.iv_voice;
                animView.setImageResource(R.drawable.voice_src);
                animView.setBackgroundResource(R.drawable.anim_chat_voice_left);
                anim = (AnimationDrawable) animView
                        .getBackground();
            }
        } else {

            if (itemViewRec.message.getRecVoiceReadStatus() != MsgInFo.READ_TRUE) {
                itemViewRec.message.setRecVoiceReadStatus(MsgInFo.READ_TRUE);
                MyApplication.getDaoSession(itemViewRec.mContext).getXmppMessageDao().update(itemViewRec.message);
                try {
                    ImageView pointIv = (ImageView) itemViewRec.pointIv;
                    if (pointIv.getVisibility() != View.INVISIBLE)
                        pointIv.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                }

            }

            if (itemViewRec != null) {
                animView = itemViewRec.iv_voice;
                animView.setImageResource(R.drawable.voice_src);
                animView.setBackgroundResource(R.drawable.anim_chat_voice_right);
                anim = (AnimationDrawable) animView
                        .getBackground();
            }
        }
        if (anim != null)
            anim.start();

        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                     @Override
                                                     public void onCompletion(MediaPlayer mp) {
                                                         reset();
                                                         try {

                                                             if (!isLeft) {

                                                             } else {
                                                                 long pathIndex = 0;
                                                                 pathIndex = itemViewRec.message.getCreateTime();

                                                                 Cursor cursor = MyApplication.getDaoSession(itemViewRec.mContext).getXmppMessageDao().getDatabase().query(
                                                                         MyApplication.getDaoSession(itemViewRec.mContext).getXmppMessageDao().getTablename(), null,
                                                                         XmppMessageDao.Properties.ExtLocalUserName.columnName
                                                                                 + " like ? AND "
                                                                                 + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                                                                                 + " like ?  AND "
                                                                                 + XmppMessageDao.Properties.CreateTime.columnName
                                                                                 + " > ?  AND "
                                                                                 + XmppMessageDao.Properties.RecVoiceReadStatus.columnName
                                                                                 + " = ? AND "
                                                                                 + XmppMessageDao.Properties.Mold.columnName
                                                                                 + " =?"


                                                                         , new String[]{itemViewRec.message.getExtLocalUserName().concat("%"),
                                                                                 itemViewRec.message.getExtRemoteUserName().concat("%")
                                                                                 , pathIndex + ""
                                                                                 , MsgInFo.READ_FALSE + ""
                                                                                 , MsgInFo.MOLD_VOICE + ""
                                                                         }, null, null, XmppMessageDao.Properties.CreateTime.columnName + " ASC ");

                                                                 if (cursor.moveToFirst()) {
                                                                     XmppMessage msg = MyApplication.getDaoSession(itemViewRec.mContext).getXmppMessageDao().readEntity(cursor, 0);

                                                                     String filePath = msg.getFilePath();
                                                                     for (ChatItemReceiveVoiceView view : currentChatItemReceiveVoiceViews) {
                                                                         if (view.message.getFilePath().equals(filePath)) {
                                                                             itemViewRec = view;
                                                                             cursor.close();
//                                                                             playSound(itemViewRec.filePath);
                                                                             itemViewRec.onClick(null);
                                                                             break;
                                                                         }
                                                                     }

                                                                 } else {
                                                                     release();
                                                                     cursor.close();
                                                                 }

                                                             }

                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                 }
            );
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()

                                            {
                                                @Override
                                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                                    release();
                                                    return true;
                                                }
                                            }

            );

            Log.d("MediaPlayer ", "file path:" + filePath);
//            if(saveConfig.getStringConfig("httpConfig").equals("true")){
//                mMediaPlayer.setDataSource(filePath.split("&")[0]);
//                mMediaPlayer.prepare();
//                mMediaPlayer.start();
//            }else {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void reset() {
        if (anim != null) {
            anim.stop();
            anim = null;
        }
        if (animView != null) {
            if (isLeft)
                animView.setBackgroundResource(R.drawable.voice_right3);
            else
                animView.setBackgroundResource(R.drawable.voice_left3);
        }

    }

    /**
     * 暂停播放
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static boolean isStarting() {
        try {
            if (mMediaPlayer != null)
                return mMediaPlayer.isPlaying();
        } catch (Exception e) {
        }

        return false;
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
        currentPath = null;
        if (anim != null)
            anim.stop();
        if (animView != null) {
            if (isLeft)
                animView.setBackgroundResource(R.drawable.voice_right3);
            else
                animView.setBackgroundResource(R.drawable.voice_left3);
            animView = null;
        }

    }

}
