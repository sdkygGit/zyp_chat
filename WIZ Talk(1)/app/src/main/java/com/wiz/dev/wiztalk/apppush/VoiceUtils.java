package com.wiz.dev.wiztalk.apppush;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;
import android.util.Log;

public class VoiceUtils {

	private Context context;
	private MediaPlayerAudio mediaPlayer;

	public VoiceUtils(Context context) {
		this(context, null);
	}

	public VoiceUtils(Context context, String diskCachePath) {
		if (context == null) {
			throw new IllegalArgumentException("context may not be null");
		}

		this.context = context.getApplicationContext();
		this.mediaPlayer = new MediaPlayerAudio(context);
	}

	public void play(String uri) {
		play(uri, null, null);
	}

	// private void play(String uri, VoicePlayConfig playConfig) {
	// play(uri, playConfig, null);
	// }

	public void play(String uri, VoiceLoadCallBack callBack) {
		play(uri, null, callBack);
	}

	private void play(String uri, VoicePlayConfig playConfig,
			VoiceLoadCallBack callBack) {
		if (uri != null && uri.equals(mediaPlayer.getPath())) {
			mediaPlayer.destroy(true);
			return;
		}
		
		mediaPlayer.destroy(false);
		
		if (callBack == null) {
			callBack = new DefaultVoiceLoadCallBack();
		}
		mVoiceLoadCallBack = callBack;

		callBack.onPreLoad(uri);

		if (TextUtils.isEmpty(uri)) {
			callBack.onLoadFailed(uri);
			return;
		}

		mediaPlayer.playAudio(uri, callBack);
	}

	private VoiceLoadCallBack mVoiceLoadCallBack;
	
	public void setVoiceLoadCallBack(VoiceLoadCallBack callBack) {
		mVoiceLoadCallBack = callBack;
		mediaPlayer.setVoiceLoadCallBack(callBack);
	}
	
	private static class VoicePlayConfig {

	}

	private static abstract class VoiceLoadCallBack {

		protected Object userTag;

		public abstract void onPreLoad(String uri);

		public abstract void onLoadFailed(String uri);

		public abstract void onError();

		public abstract void onCompletion();

		public abstract void onDestroy();
	}

	public static class DefaultVoiceLoadCallBack extends VoiceLoadCallBack {

		@Override
		public void onPreLoad(String uri) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadFailed(String uri) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompletion() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			
		}

	}

	private static class MediaPlayerAudio implements OnErrorListener,
			OnCompletionListener {
		private static final String TAG = "MediaPlayerAudio";

		private String mPath;
		private MediaPlayer mMediaPlayer;
		private VoiceLoadCallBack mVoiceLoadCallBack;
		
		private boolean mUseSpeaker = true;
		private AudioManager audioManager;

		public MediaPlayerAudio(Context context) {
			audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		}

		public void setVoiceLoadCallBack(VoiceLoadCallBack callBack) {
			mVoiceLoadCallBack = callBack;
		}

		public void playAudio(String path, VoiceLoadCallBack callback) {
			Log.d(TAG, "playAudio() path:" + path);
			try {
				mPath = path;
				mVoiceLoadCallBack = callback;
				if (path == null || path == "") {
					return;
				}
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnErrorListener(this);
				mMediaPlayer.setOnCompletionListener(this);
				
				if (mUseSpeaker) {
					audioManager.setMode(AudioManager.MODE_NORMAL);
					audioManager.setSpeakerphoneOn(true);
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
				} else {
					audioManager.setSpeakerphoneOn(false);// 关闭扬声器
					// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
					audioManager.setMode(AudioManager.MODE_IN_CALL);
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
				}
				
				mMediaPlayer.setDataSource(path);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			} catch (Exception e) {
				Log.e(TAG, "error: " + e.getMessage(), e);
				destroy(false);
				if (mVoiceLoadCallBack != null) {
					mVoiceLoadCallBack.onError();
				}
			}

		}

		public void destroy(boolean callback) {
			if (callback && mVoiceLoadCallBack != null) {
				mVoiceLoadCallBack.onDestroy();
			}
			if (mMediaPlayer != null) {
				mMediaPlayer.release();
				mMediaPlayer = null;
				mPath = null;
			}
		}

		public boolean isPlaying() {
			return mMediaPlayer != null && mMediaPlayer.isPlaying();
		}

		public String getPath() {
			return mPath;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.d(TAG, "onCompletion()");
			destroy(false);
			if (mVoiceLoadCallBack != null) {
				mVoiceLoadCallBack.onCompletion();
			}
		}

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.d(TAG, "onError()");
			destroy(false);
			if (mVoiceLoadCallBack != null) {
				mVoiceLoadCallBack.onError();
			}
			return false;
		}
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public void destroy() {
		mediaPlayer.destroy(true);
	}

	public String getPath() {
		return mediaPlayer.getPath();
	}
}
