package com.wiz.dev.wiztalk.apppush.view;

import java.io.File;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.apppush.SoundMeter;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;
import com.wiz.dev.wiztalk.utils.SmileyParser;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.BiaoQingView;

@EViewGroup(R.layout.footer_text)
public class FooterTextView extends LinearLayout {

	private static final String TAG = "FooterTextView";

	Activity mActivity;
	
	@ViewById(R.id.btn_mmfooter_texttolist)
	public ImageButton ibTextToList;
	
	@ViewById
	View viewDivider;
	
	/**
	 * isCheck:[false:文本;true:语音]
	 */
	@ViewById
	CheckBox cbChattingSetmode;

	@ViewById
	EditText editText;

	@ViewById
	CheckBox btnChattingBiaoqing;

	@ViewById
	Button btnVoice;

	@ViewById
	ImageButton btnTypeSelect;

	@ViewById
	BiaoQingView viewBiaoQing;

	@ViewById
	TypeSelectView viewTypeSelect;
	
	@ViewById
	Button btnSend;

	public FooterTextView(Context context) {
		super(context);
	}

	public FooterTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterInject
	void afterInject() {
		mActivity = (Activity) getContext();
	}
	
	@AfterViews
	void afterViews() {
		viewBiaoQing.setOnItemClickListener(mBiaoQingOnItemClickListenr);
		viewTypeSelect.setOnItemClickListener(mOnItemClickListenrTypeSelect);
		
		btnVoice.setBackgroundResource(R.drawable.btn_white_with_border_normal);
	}

	@TextChange(R.id.editText)
	void onTextChange(CharSequence s, int start, int before, int count) {
		if (TextUtils.isEmpty(s)) {
			btnTypeSelect.setVisibility(View.VISIBLE);
			btnSend.setVisibility(View.GONE);
		} else {
			btnTypeSelect.setVisibility(View.GONE);
			btnSend.setVisibility(View.VISIBLE);
		}
	}

	@CheckedChange(R.id.cbChattingSetmode)
	void onCheckedChangeChattingSetmode(CompoundButton buttonView,
			boolean isChecked) {
		if (isSoftInputShowing) {
			toggleSoftInput(getContext());
		} else {
			mUIHandler.onResize();
		}

		if (isChecked) {
			editText.setVisibility(View.GONE);
			btnChattingBiaoqing.setVisibility(View.GONE);
			btnVoice.setVisibility(View.VISIBLE);
			btnTypeSelect.setVisibility(View.VISIBLE);
			btnSend.setVisibility(View.GONE);

			btnChattingBiaoqing.setChecked(false);
			viewBiaoQing.setVisibility(View.GONE);
			viewTypeSelect.setVisibility(View.GONE);
		} else {
			editText.setVisibility(View.VISIBLE);
			btnChattingBiaoqing.setVisibility(View.VISIBLE);
			btnVoice.setVisibility(View.GONE);
			
			if (TextUtils.isEmpty(editText.getText().toString())) {
				btnTypeSelect.setVisibility(View.VISIBLE);
				btnSend.setVisibility(View.GONE);
			} else {
				btnTypeSelect.setVisibility(View.GONE);
				btnSend.setVisibility(View.VISIBLE);
			}
		}
	}

	@CheckedChange(R.id.btnChattingBiaoqing)
	void onCheckedChangeBiaoQing(CompoundButton buttonView, boolean isChecked) {
		if (isSoftInputShowing) {
			toggleSoftInput(getContext());
		} else {
			mUIHandler.onResize();
		}
	}

	@Click(R.id.btnTypeSelect)
	void onClickTypeSelect() {
		if (isSoftInputShowing) {
			toggleSoftInput(getContext());
		} else {
			mUIHandler.onResize();
		}
		
		boolean isTypeSelectShowing = viewTypeSelect.getVisibility() == View.VISIBLE;
		
		if (!isTypeSelectShowing) {
			cbChattingSetmode.setChecked(false);
			btnChattingBiaoqing.setChecked(false);
			
			viewBiaoQing.setVisibility(View.GONE);
			viewTypeSelect.setVisibility(View.VISIBLE);
		} else {
			viewTypeSelect.setVisibility(View.GONE);
		}
	}

	@Click(R.id.btnSend)
	void onBtnSend(View v) {
		if (mOnEventListener != null) {
			mOnEventListener.onBtnSendClick(v, editText.getText().toString());
		}
		editText.setText(null);
	}

	private UIHandler mUIHandler = new UIHandler();

	private class UIHandler extends Handler {
		private static final int MSG_RESIZE = 0;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE:
				if (isSoftInputShowing) {
					btnChattingBiaoqing.setChecked(false);
					viewBiaoQing.setVisibility(View.GONE);
					viewTypeSelect.setVisibility(View.GONE);
				} else {
					if (btnChattingBiaoqing.isChecked()) {
						viewBiaoQing.setVisibility(View.VISIBLE);
						viewTypeSelect.setVisibility(View.GONE);
					} else {
						viewBiaoQing.setVisibility(View.GONE);
					}
				}
				break;

			default:
				break;
			}
		}

		public void onResize() {
			Message msg = new Message();
			msg.what = MSG_RESIZE;
			msg.obj = isSoftInputShowing;
			this.sendMessage(msg);
		}
	}

	private boolean isSoftInputShowing = false;

	public boolean isSoftInputShowing() {
		return isSoftInputShowing;
	}
	
	public void setSoftInputShowing(boolean b) {
		isSoftInputShowing = b;
		mUIHandler.onResize();
	}

	/**
	 * 反复切换软键盘
	 */
	public static void toggleSoftInput(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private AdapterView.OnItemClickListener mBiaoQingOnItemClickListenr = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String item = (String) parent.getItemAtPosition(position);
			insertCharSequence(editText, SmileyParser.getInstance()
					.addSmileySpans(item));
		}
	};
	
	private AdapterView.OnItemClickListener mOnItemClickListenrTypeSelect = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
//				startImagePick();
				if (mOnEventListener != null) {
					mOnEventListener.onClickTypeSelectImagePick();
				}
				break;
			case 1:
//				startActionCamera();
				if (mOnEventListener != null) {
					mOnEventListener.onClickTypeSelectImageCapture();
				}
				break;

			default:
				break;
			}
			viewTypeSelect.setVisibility(View.GONE);
		}
	};

	public static void insertCharSequence(EditText editText, CharSequence text) {
		int index = editText.getSelectionStart();
		Editable editable = editText.getText();
		editable.insert(index, text);
	}

	public static void deleteOne(EditText editText) {
		int index = editText.getSelectionStart();
		Editable editable = editText.getText();
		editable.delete(index - 1, index);
	}
	
	public static interface OnFooterTextViewEventListener {
		void onBtnSendClick(View v, String content);
		
		void onClickTypeSelectImagePick();
		
		void onClickTypeSelectImageCapture();

		void onVoiceRcd(String filePath, long voiceLength);
	}

	public OnFooterTextViewEventListener mOnEventListener;

	public void setOnEventListener(OnFooterTextViewEventListener l) {
		mOnEventListener = l;
	}
	
	private static final int DISTANCE_CANCEL = 150;
	
	private boolean isTouchCanHandle = true;
	
	@Touch(R.id.btnVoice)
	void onTouchBtnVoice(View v, MotionEvent e) {
		Log.d(TAG, "onTouchBtnVoice()");
		
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "onTouchBtnVoice() ACTION_DOWN");
			if (isTouchCanHandle) {
				showBtnVoiceStop();
				viewRcd.showRcdView(viewRcd.viewRcdProgress);
				mHandler.soundStart();
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "onTouchBtnVoice() ACTION_UP");
			
			if (isTouchCanHandle) {
				showBtnVoiceStart();
				viewRcd.showRcdView(null);
				
				if (Math.abs(e.getY()) < DISTANCE_CANCEL) {
					mHandler.soundStop();
				} else {
					mHandler.soundCancelWhenActionUpOrActionCancel();
				}
			}
			
			isTouchCanHandle = true;
			
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "onTouchBtnVoice() ACTION_MOVE");
			
			if (isTouchCanHandle) {
				if (Math.abs(e.getY()) < DISTANCE_CANCEL) {
					mHandler.soundStartWhenActionMove();
				} else {
					showBtnVoiceCancel();
					viewRcd.showRcdView(viewRcd.viewRcdCancel);
					mHandler.soundCancelWhenActionMove();
				}
			}
			
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d(TAG, "onTouchBtnVoice() ACTION_CANCEL");
			if (isTouchCanHandle) {
				mHandler.soundCancelWhenActionUpOrActionCancel();
			}
			isTouchCanHandle = true;
			break;

		default:
			break;
		}
	}

	private void deleteRcdFile(String path) {
		if (path != null) {
			File file = new File(path);
			file.delete();
		}
	}

	void showBtnVoiceStart() {
		btnVoice.setBackgroundResource(R.drawable.btn_white_with_border_normal);
		btnVoice.setText(R.string.rcd_start);
	}
	
	void showBtnVoiceStop() {
		btnVoice.setBackgroundResource(R.drawable.btn_white_with_border_pressed);
		btnVoice.setText(R.string.rcd_stop);
	}
	
	void showBtnVoiceCancel() {
		btnVoice.setBackgroundResource(R.drawable.btn_white_with_border_pressed);
		btnVoice.setText(R.string.rcd_cancel);
	}
	
	private RcdView viewRcd;
	
	public void setRcdView(RcdView viewRcd) {
		this.viewRcd = viewRcd;
	}
	
	private MySoundHandler mHandler = new MySoundHandler();
	
	private class MySoundHandler extends Handler {
		
		private static final int MSG_SOUND_START = 1;
		private static final int MSG_SOUND_START_WHEN_ACTION_MOVE =2;
		private static final int MSG_SOUND_CANCEL_WHEN_ACTION_MOVE = 3;
		private static final int MSG_SOUND_CANCEL_WHEN_ACTION_UP_OR_ACTION_CANCEL = 4;
		private static final int MSG_SOUND_STOP = 5;
		private static final int MSG_CHECK_TIME_LEFT_AFTER_SOUND_START = 6;
		
		private static final long TIME_SECOND = 1000;
		private static final long TIME_SOUND_MIN = (long) (1.1 * 1000);
		private static final long TIME_SOUND_MAX = 60 * 1000;
		private static final long TIME_SOUND_LEFT_WARNING = 10 * 1000;
		
		private SoundMeter mSoundMeter;
		
		SoundMeter.OnMediaRecorderListener mOnMaxAmplitudeListener = new SoundMeter.OnMediaRecorderListener() {
			@Override
			public void onMaxAmplitude(int maxAmplitude) {
//				Log.d(TAG, "onMaxAmplitude() maxAmplitude:" + maxAmplitude);
				int vusize = 7 * maxAmplitude / 32768;
				viewRcd.viewRcdVoice.setAmp(vusize);
			}

			@Override
			public void onError() {
				Log.d(TAG, "onError()");
				Utils.showToastShort(getContext(), "录音错误");
				viewRcd.showRcdView(null);
			}

			@Override
			public void onException(Exception e) {
				Log.d(TAG, "onException()");
				Utils.showToastShort(getContext(), "录音异常");
				viewRcd.showRcdView(null);
			}
		};
		
		public MySoundHandler() {
			mSoundMeter = new SoundMeter();
			mSoundMeter.setMediaRecorderListener(mOnMaxAmplitudeListener);
		}
		
		public void soundStartWhenActionMove() {
			if (!mSoundMeter.isRecording()) {
				showBtnVoiceStop();
				viewRcd.showRcdView(viewRcd.viewRcdProgress);
			}
			this.removeCallbacksAndMessages(null);
			this.sendEmptyMessage(MSG_SOUND_START_WHEN_ACTION_MOVE);
		}

		public void soundStop() {
			this.removeCallbacksAndMessages(null);
			this.sendEmptyMessage(MSG_SOUND_STOP);
		}

		public void soundCancelWhenActionUpOrActionCancel() {
			this.removeCallbacksAndMessages(null);
			this.sendEmptyMessage(MSG_SOUND_CANCEL_WHEN_ACTION_UP_OR_ACTION_CANCEL);
		}

		public void soundCancelWhenActionMove() {
			this.removeCallbacksAndMessages(null);
			this.sendEmptyMessage(MSG_SOUND_CANCEL_WHEN_ACTION_MOVE);
		}

		public void soundStart() {
			this.removeCallbacksAndMessages(null);
			this.sendEmptyMessage(MSG_SOUND_START);
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SOUND_START:
				msgSoundStart();
				break;
			case MSG_SOUND_START_WHEN_ACTION_MOVE:
				msgSoundStartWhenActionMove();
				break;
			case MSG_SOUND_CANCEL_WHEN_ACTION_MOVE:
				msgSoundCancleWhenActionMove();
				break;
			case MSG_SOUND_CANCEL_WHEN_ACTION_UP_OR_ACTION_CANCEL:
				msgSoundCancleWhenActionUpOrActionCancel();
				break;
			case MSG_SOUND_STOP:
				msgSoundStop();
				break;
			case MSG_CHECK_TIME_LEFT_AFTER_SOUND_START:
				msgCheckTimeLeftAfterSoundStart();
				break;

			default:
				break;
			}
		}
		
		private void msgSoundStart() {
//			showBtnVoiceStop();
//			showRcdView(viewRcdProgress);
			
			if (!mSoundMeter.isRecording()) {
				
				File youxinDir = Utils.ensureIMSubDir(getContext(), Utils.FILE_PATH_SUB_DIR_SOUND_OUT);
				if (youxinDir != null) {
					String name = String.valueOf(System.currentTimeMillis()) + ".amr";
					File file = new File(youxinDir, name);
					mSoundMeter.start(file.getPath());
				}
			}
			
			showBtnVoiceStop();
			viewRcd.showRcdView(viewRcd.viewRcdVoice);
			viewRcd.viewRcdVoice.setText(R.string.rcd_to_cancel);
			msgCheckTimeLeftAfterSoundStart();
		}
		
		private void msgCheckTimeLeftAfterSoundStart() {
			long timeLeft = TIME_SOUND_MAX - mSoundMeter.getDuration();
			int timeLeftSecond = (int)(timeLeft/TIME_SECOND);
			
			if (timeLeftSecond > 0) {
				if (timeLeft < TIME_SOUND_LEFT_WARNING) {
					viewRcd.viewRcdVoice.setText(getContext().getString(R.string.rcd_time_left, timeLeftSecond));
				}
				this.sendEmptyMessageDelayed(MSG_CHECK_TIME_LEFT_AFTER_SOUND_START, 100);
			} else {
				this.removeCallbacksAndMessages(null);
				soundStop();
				
				showBtnVoiceStart();
				viewRcd.showRcdView(null);
				
				isTouchCanHandle = false;
			}
		}
		
		private void msgSoundStartWhenActionMove() {
			if (!mSoundMeter.isRecording()) {
				msgSoundStart();
			}
			msgCheckTimeLeftAfterSoundStart();
		}
		
		private void msgSoundCancleWhenActionMove() {
//			showBtnVoiceCancel();
//			showRcdView(viewRcdCancel);
			
			if (mSoundMeter.isRecording()) {
				String path = mSoundMeter.getPath();
				mSoundMeter.stop();
				deleteRcdFile(path);
			}
		}
		
		private void msgSoundCancleWhenActionUpOrActionCancel() {
//			showBtnVoiceStart();
//			showRcdView(null);
			
			if (mSoundMeter.isRecording()) {
				String path = mSoundMeter.getPath();
				mSoundMeter.stop();
				deleteRcdFile(path);
			}
		}
		
		private void msgSoundStop() {
//			showBtnVoiceStart();
//			showRcdView(null);
			
			if (mSoundMeter.isRecording()) {
				long duration = mSoundMeter.getDuration();
				String path = mSoundMeter.getPath();
				mSoundMeter.stop();
				
				if (duration >= TIME_SOUND_MIN) {
//					Toast.makeText(getContext(), "录音Uri:" + path + ",duration:" + duration, Toast.LENGTH_SHORT).show();
					if (mOnEventListener != null) {
						mOnEventListener.onVoiceRcd(path, duration);
					}
				} else {
					showRcdWarning();
					deleteRcdFile(path);
				}
			} else {
				String path = mSoundMeter.getPath();
				showRcdWarning();
				deleteRcdFile(path);
			}
		}
		
		private void showRcdWarning() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					viewRcd.showRcdView(viewRcd.viewRcdWarning);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							viewRcd.showRcdView(null);
						}
					}, 300);
				}
			});
		}
	}
	
	

	public void bind(String remoteUserName, List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations) {
		if (Member.isTypeApp(remoteUserName)) {
			if (operations == null || operations.size() == 0) {
				ibTextToList.setVisibility(View.GONE);
				viewDivider.setVisibility(View.GONE);
			} else {
				ibTextToList.setVisibility(View.VISIBLE);
				viewDivider.setVisibility(View.VISIBLE);
			}
		} 
		else {
			ibTextToList.setVisibility(View.GONE);
			viewDivider.setVisibility(View.GONE);
		}
	}

}
