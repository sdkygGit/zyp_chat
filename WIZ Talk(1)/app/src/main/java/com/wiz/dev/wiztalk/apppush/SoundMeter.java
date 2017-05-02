package com.wiz.dev.wiztalk.apppush;

import java.io.IOException;
import java.lang.reflect.Field;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public  class SoundMeter {
	
	private static final String TAG = "SoundMeter";
	
	private MediaRecorder mRecorder = null;

	private boolean isRecording;

	private String mPath;
	
	private long timeStart;
	private long timeStop;
	
	public String getPath() {
		return mPath;
	}
	
	public long getDuration() {
		if (this.timeStop == 0) {
			return System.currentTimeMillis() - this.timeStart;
		}
		return this.timeStop - this.timeStart;
	}
	
	public void start(String path) {
		Log.d(TAG, "start() path:" + path);
		mPath = path;
		
		try {
//			if (mRecorder == null) {
				mRecorder = new MediaRecorder();
//			} 
			
			mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
//					stop();
					
					if (mOnMediaRecorderListener != null) {
						mOnMediaRecorderListener.onError();
					}
				}
			});
			
//			mRecorder.setAudioEncodingBitRate(bitRate);
//			mRecorder.setAudioSamplingRate(samplingRate);
			
//			mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//源代码OutputFormat:THREE_GPP;OutputFile:*.amr;
//			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			try {
				// RAW_AMR was deprecated in API level 16.
				Field deprecatedName = MediaRecorder.OutputFormat.class.getField("RAW_AMR");
				mRecorder.setOutputFormat(deprecatedName.getInt(null));
			} catch (Exception e) {
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			}
//			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(path);
			
			mRecorder.setAudioChannels(1);
			
			mRecorder.prepare();
			
			this.timeStart = System.currentTimeMillis();
			this.timeStop = 0;
			
			mRecorder.start();
			
			isRecording=true;
			startOnMaxAmplitudeListener();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			if (mOnMediaRecorderListener != null) {
				mOnMediaRecorderListener.onException(e);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (mOnMediaRecorderListener != null) {
				mOnMediaRecorderListener.onException(e);
			}
		}
		
//		if (mOnMediaRecorderListener != null) {
//			mOnMediaRecorderListener.onStart();
//		}
	}

	public void stop() {
		if (isRecording) {
			if (mRecorder != null) {
				try {
					mRecorder.stop();
					mRecorder.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mRecorder = null;
			}
			isRecording=false;
//			mPath = null;
			stopOnMaxAmplitudeListener();
		}
		
		timeStop = System.currentTimeMillis();
		
//		if (mOnMediaRecorderListener != null) {
//			mOnMediaRecorderListener.onStop();
//		}
	}

	public boolean isRecording() {
		return isRecording;
	}
	
	private OnMediaRecorderListener mOnMediaRecorderListener;
	
	public void setMediaRecorderListener(OnMediaRecorderListener l) {
		mOnMediaRecorderListener = l;
	}
	
	private void startOnMaxAmplitudeListener() {
		mHandler.post(mOnMaxAmplitudeListenerRunnable);
	}
	
	private void stopOnMaxAmplitudeListener() {
		mHandler.removeCallbacks(mOnMaxAmplitudeListenerRunnable);
	}
	
	private Handler mHandler = new Handler();
	
	private Runnable mOnMaxAmplitudeListenerRunnable = new Runnable() {
		@Override
		public void run() {
			if (mOnMediaRecorderListener != null) {
				if (mRecorder != null) {
					try {
						mOnMediaRecorderListener.onMaxAmplitude(mRecorder.getMaxAmplitude());
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			}
			mHandler.postDelayed(mOnMaxAmplitudeListenerRunnable, 300);
		}
	};
	
	public static interface OnMediaRecorderListener {
		void onMaxAmplitude(int maxAmplitude);
		
		void onError();
		void onException(Exception e);
//		
//		void onStart();
//		void onStop();
	}

}
