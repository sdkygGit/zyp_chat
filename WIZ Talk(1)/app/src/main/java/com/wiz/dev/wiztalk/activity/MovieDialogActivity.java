package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dtr.zxing.camera.CameraManager;
import com.wiz.dev.wiztalk.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MovieDialogActivity extends Activity implements MediaRecorder.OnErrorListener, SurfaceHolder.Callback {

    private CameraManager cameraManager;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;


    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mVecordFile = null;// 文件

    boolean isRelease;

    boolean isException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.small_movie_layout);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = RadioGroup.LayoutParams.FILL_PARENT;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Button shoot_button = (Button) findViewById(R.id.shoot_button);

        shoot_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        record();
                        break;

                    case MotionEvent.ACTION_UP:
                        stop();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        stopRecord();
                        releaseRecord();
                        releaseCamera();
                        break;
                }

                return true;
            }
        });

        // movieRecorderView.setmOnRecordFinishListener(new
        // MovieRecorderView.OnRecordFinishListener() {
        // @Override
        // public void onRecordFinish(String path, int second) {
        // Intent intent = new Intent();
        // intent.putExtra("path", path);
        // intent.putExtra("second", second);
        // setResult(Activity.RESULT_OK, intent);
        // finish();
        // }
        //
        // @Override
        // public void onRecordError() {
        // Toast.makeText(MovieDialogActivity.this, "录制失败",
        // Toast.LENGTH_SHORT).show();
        // }

        // });

        mWidth = 640;// 默认320
        mHeight = 480;// 默认240
        isOpenCamera = true;// 默认打开
        mRecordMaxTime = 10;// 默认为10
        mProgressBar.setMax(mRecordMaxTime);
    }

    boolean isHasSurface;

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            getCamera();
            if (isHasSurface) {
                setStartPriview(mCamera, mSurfaceHolder);
            }else{
                mSurfaceHolder.addCallback(this);
            }
        }
    }

    @Override
    protected void onPause() {
        stopRecord();
        releaseRecord();
        releaseCamera();
        super.onPause();
    }

    private void getCamera() {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            Toast.makeText(MovieDialogActivity.this,"摄像头打开失败",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setStartPriview(Camera camera, SurfaceHolder holder) {
        try {
            if(camera == null){
                getCamera();
                setCameraParams(mCamera );
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                mCamera.unlock();
            }else{
                setCameraParams(camera );
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                camera.unlock();
            }
//			initRecord(mWidth, mHeight);
        } catch (Exception e) {

        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isRelease= true;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isHasSurface = true;
        setStartPriview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        stopRecord();
        releaseRecord();
        releaseCamera();
        if (isRelease)// 如果未打开摄像头，则打开
            if (mCamera == null) {
                if (isHasSurface) {
                    setStartPriview(mCamera, mSurfaceHolder);
                }
            }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
        releaseCamera();
    }

    /**
     * 设置摄像头为竖屏
     *
     * @author 胡汉三
     */
    private void setCameraParams(Camera mCamera) {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();

            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            params.set("orientation", "portrait");
//			params.setRotation(90);
            mCamera.setDisplayOrientation(90);

            List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();

            Camera.Size size = getCloselyPreSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), previewSizes, true);
            // Camera.Size size = getBestPreviewSize(mSurfaceView.getWidth(),
            // mSurfaceView.getHeight(), params);

//            ViewGroup.LayoutParams layoutParams =
//                    mSurfaceView.getLayoutParams();
//            layoutParams.height = (size.height * layoutParams.width) /
//                    size.width;
//
//            mSurfaceView.setLayoutParams(layoutParams);
            //
            params.setPreviewSize(size.width, size.height);

            mWidth = size.width;
            mHeight = size.height;
            // mWidth = size.height;
            // mHeight = size.width;
            mCamera.setParameters(params);
        }
    }

    Camera.Size getCloselyPreSize(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList,
                                  boolean mIsPortrait) {
        int ReqTmpWidth;
        int ReqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (mIsPortrait) {
            ReqTmpWidth = surfaceHeight;
            ReqTmpHeight = surfaceWidth;
        } else {
            ReqTmpWidth = surfaceWidth;
            ReqTmpHeight = surfaceHeight;
        }

        for (Camera.Size size : preSizeList) {
            if ((size.width == ReqTmpWidth) && (size.height == ReqTmpHeight)) {
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) ReqTmpWidth) / ReqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

    private void createRecordDir() {
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "wizTalk/sendVideo/");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        File vecordDir = sampleDir;
        // 创建文件
        try {
            mVecordFile = File.createTempFile(generateFileName(), ".mp4", vecordDir);// mp4格式
        } catch (IOException e) {
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 初始化
     *
     * @throws IOException
     * @author 胡汉三
     */
    private void initRecord(int wid, int hei) throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        // 设置从摄像头采集图像
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置视频文件的输出格式
        // 必须在设置声音编码格式、图像编码格式之前设置
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置声音编码的格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		/*
		 * mMediaRecorder.setAudioChannels(1);//1单声道 2 多声道
		 * mMediaRecorder.setAudioSamplingRate(12500); //设置录制的音频采样率——频率越高，音质越好
		 * mMediaRecorder.setAudioEncodingBitRate(16); // 设置录制的音频编码比特率
		 */
        mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024);// 设置帧频率，然后就清晰了
        mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        // 设置图像编码的格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mMediaRecorder.setVideoSize(wid, hei);
        // 每秒 4帧
        // mMediaRecorder.setVideoFrameRate(20);
        mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
        mMediaRecorder.prepare();
        mMediaRecorder.start();


        // try {
        // mMediaRecorder = new MediaRecorder();
        // mMediaRecorder.reset();
        // if (mCamera != null)
        // mMediaRecorder.setCamera(mCamera);
        //
        // mMediaRecorder.setOnErrorListener(this);
        // mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        //
        // // 设置从摄像头采集图像
        // mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
        // mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // // 设置视频文件的输出格式
        // // 必须在设置声音编码格式、图像编码格式之前设置
        // mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // // 设置声音编码的格式
        // mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // /*
        // * mMediaRecorder.setAudioChannels(1);//1单声道 2 多声道
        // * mMediaRecorder.setAudioSamplingRate(12500); //设置录制的音频采样率——频率越高，音质越好
        // * mMediaRecorder.setAudioEncodingBitRate(16); // 设置录制的音频编码比特率
        // */
        // mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);//
        // 设置帧频率，然后就清晰了
        // mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        // // 设置图像编码的格式
        // mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // mMediaRecorder.setVideoSize(mWidth, mHeight);
        // // 每秒 4帧
        //// mMediaRecorder.setVideoFrameRate(20);
        // mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
        // mMediaRecorder.prepare();
        // mMediaRecorder.start();
        // isException = false;
        // } catch (Exception e) {
        // e.printStackTrace();
        // isException = true;
        // }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    public void setmOnRecordFinishListener(OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
    }

    /**
     * 开始录制视频
     *
     * @author 胡汉三 视频储存位置 达到指定时间之后回调接口
     */
    public void record() {
        try {
            createRecordDir();
            initRecord(mWidth, mHeight);
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stopRecord();
                    }
                }
            }, 0, 1000);
            isException = false;
        } catch (Exception e) {
            e.printStackTrace();
            if (mTimer != null)
                mTimer.cancel();
            if (mMediaRecorder != null) {
                // 设置后不会崩
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                try {
                    mMediaRecorder.stop();

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (mMediaRecorder != null) {
                mMediaRecorder.setOnErrorListener(null);
                try {
                    mMediaRecorder.release();

                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            mMediaRecorder = null;

            try {
                initRecord(640, 480);
//				mMediaRecorder.start();
                mTimeCount = 0;// 时间计数器重新赋值
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimeCount++;
                        mProgressBar.setProgress(mTimeCount);// 设置进度条
                        if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                            stopRecord();
                        }
                    }
                }, 0, 1000);

            } catch (IOException e1) {
                isException = true;
                e1.printStackTrace();
            }

        }
    }

    /**
     * 停止拍摄
     *
     * @author 胡汉三
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        releaseCamera();

        if (mTimeCount > 3) {// 达到指定时间，停止拍摄

            if (isException) {
                Toast.makeText(MovieDialogActivity.this, "录制失败！", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("path", mVecordFile.getAbsolutePath());
                intent.putExtra("second", mTimeCount);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } else {
            if (mVecordFile != null)
                mVecordFile.delete();
            Toast.makeText(MovieDialogActivity.this, "录制时间太短！", Toast.LENGTH_SHORT).show();

            if (isRelease)// 如果未打开摄像头，则打开
                if (mCamera == null) {
                    getCamera();
                    if (isHasSurface) {
                        setStartPriview(mCamera, mSurfaceHolder);
                    }
                }
        }
    }

    /**
     * 停止录制
     *
     * @author 胡汉三
     */
    public void stopRecord() {
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     *
     * @author 胡汉三
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
        if(mCamera != null)
            mCamera.lock();
    }

    /**
     * 录制完成回调接口
     *
     * @author 胡汉三
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish(String path, int second);
        public void onRecordError();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        isException = true;
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
