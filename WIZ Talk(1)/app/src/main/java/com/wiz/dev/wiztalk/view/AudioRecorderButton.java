package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.PixelUtils;

import java.text.DecimalFormat;

/**
 * Created by Admin on 2015/8/17.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {

    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int STATE_NORMAL = 1; //默认状态
    private static final int STATE_RECORDING = 2; //正在录音
    private static final int STATE_WANT_TO_CANCEL = 3; //希望取消
    private static final int STATE_RECORD_TIME_GOTO_MAX_LENGH = 4;

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;
    private static final int MSG_DIALOG_FINISH = 0X113;

    private int mCurState = STATE_NORMAL; //当前的状态
    //已经开始录音
    private boolean isRecording = false; //是否已经开始录音

    private DialogManager mDialogManger;
    private AudioManager mAudioManger;

    private float mTime;
    //是否触发longClick方法

    private boolean mReady;

    public static final int maxRecordTime = 60;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDialogManger = new DialogManager(getContext());
        //TODO 判断SD卡是否存在

        String dir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            dir = context.getExternalFilesDir("epic_recorders").getAbsolutePath();
        else
            dir = context.getFilesDir().getAbsolutePath();


        mAudioManger = AudioManager.getmInstance(dir);
        mAudioManger.setOnAudioStateListener(this);
    }


    private AudioFinishRecorderListener mListener;

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording)
                mTime += 0.1f;
            if (mTime < maxRecordTime) {
                handler.sendEmptyMessage(MSG_VOICE_CHANGED);
                if (isRecording)
                    handler.postDelayed(this, 100);
            } else {
                handler.sendEmptyMessage(MSG_DIALOG_FINISH);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示应该在audio end prepared以后
                    isRecording = true;
                    handler.postDelayed(mGetVoiceLevelRunnable,100);
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManger.updateVoiceLevel(mAudioManger.getVoiceLevel(7));
                    if(mTime > 0){
                        DecimalFormat decimalFormat = new DecimalFormat(".0");
                        String time = decimalFormat.format(mTime);
                        mDialogManger.setTime(time.split("\\.")[0] + "秒");
                    } else {
                        mDialogManger.setTime("0秒");
                    }
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManger.dimissDialog();

                    break;
                case MSG_DIALOG_FINISH:
                    changeState(STATE_RECORD_TIME_GOTO_MAX_LENGH);
                    DecimalFormat decimalFormat2 = new DecimalFormat(".0");
                    String time2 = decimalFormat2.format(mTime);
                    mDialogManger.setTime(time2.split("\\.")[0] + "秒");
                    mAudioManger.release();
                    handler.removeCallbacks(mGetVoiceLevelRunnable);
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        if(mReady)
            handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
        else
            mAudioManger.cancel();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mReady) {
                    mTime = 0;
                    mDialogManger.showRecordingDialog();
                    changeState(STATE_RECORDING);
                    mReady = true;
                    mAudioManger.prepareAudio();
                } else {
                    mDialogManger.dimissDialog();
                    //cancel
                    reset();
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    //根据x,y的坐标,判断是否取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        if (mTime < maxRecordTime) {
                            changeState(STATE_RECORDING);
                        } else {
                            changeState(STATE_RECORD_TIME_GOTO_MAX_LENGH);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                }


                if (!isRecording || mTime < 0.6f) {
                    mDialogManger.tooShort();
                    mAudioManger.cancel();
                    handler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurState == STATE_RECORDING) { //正常录制的时候结束
                    mDialogManger.dimissDialog();
                    //release
                    mAudioManger.release();
                    //callbackToAct
                    if (mListener != null && mAudioManger.getCurrentFilePath() != null) {
                        mListener.onFinish(mTime, mAudioManger.getCurrentFilePath());
                    }
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManger.dimissDialog();
                    //cancel
                    mAudioManger.cancel();
                } else if (mCurState == STATE_RECORD_TIME_GOTO_MAX_LENGH) { //正常录制的时候结束
                    mDialogManger.dimissDialog();

                    //callbackToAct
                    if (mListener != null && mAudioManger.getCurrentFilePath() != null) {
                        mListener.onFinish(mTime, mAudioManger.getCurrentFilePath());
                    }
                }
                reset();
                break;
        }

        return true;
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mReady = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        //超过按钮的宽度
        if (x < 0 || x > getWidth()) {
            return true;
        }
        //超过按钮的高度
        if (y < -PixelUtils.dp2px(DISTANCE_Y_CANCEL) || y > getHeight() + PixelUtils.dp2px(DISTANCE_Y_CANCEL)) {
            return true;
        }
        return false;
    }

    /**
     * 改变Button的文本以及背景色
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.button_recorder_normal);
                    setText(R.string.voice_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.button_recorder);
                    setText(R.string.voice_up_tips);
                    if (isRecording) {
                        mDialogManger.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.button_recorder);
                    setText(R.string.voice_cancel_tips);
                    mDialogManger.wantToCancel();
                    break;

                case STATE_RECORD_TIME_GOTO_MAX_LENGH:
                    setBackgroundResource(R.drawable.button_recorder_normal);
                    setText("录音至最长时间，请发送");
                    mDialogManger.tooLong();
                    break;
            }
        }
    }
}
