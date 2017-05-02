package com.wiz.dev.wiztalk.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;

public class MyAlertDialog implements OnClickListener {

	String title;
	String message;
	String cancleBtnText;
	String okBtnText;

	Context context;

	OnMyDialogBtnClickListener mListener;
	
	AlertDialog mAlertDialog;

	public interface OnMyDialogBtnClickListener {
		void onCancleClick();

		void onOkClick();
	}

	public MyAlertDialog(Context context, String title, String message,
			String cancleBtnText, String okBtnText,OnMyDialogBtnClickListener listener) {
		this.title = title;
		this.message = message;
		this.cancleBtnText = cancleBtnText;
		this.okBtnText = okBtnText;
		this.context = context;
		this.mListener = listener;
	}

	public void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		mAlertDialog = builder.create();
		mAlertDialog.show();

		View view = LayoutInflater.from(context).inflate(
				R.layout.mydialog_layout, null);
		TextView title = (TextView) view.findViewById(R.id.dialog_title);
		title.setText(this.title);
		TextView message = (TextView) view.findViewById(R.id.dialog_message);
		message.setText(this.message);
		Button cancle = (Button) view.findViewById(R.id.cancleBtn);
		cancle.setOnClickListener(this);
		Button ok = (Button) view.findViewById(R.id.okBtn);
		ok.setOnClickListener(this);
		mAlertDialog.getWindow().setContentView(view);
		
		DisplayMetrics dm = new DisplayMetrics();

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		mAlertDialog.getWindow().setLayout(width*4/5, LayoutParams.WRAP_CONTENT);
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.cancleBtn:
			if (mListener != null)
				mListener.onCancleClick();
			mAlertDialog.dismiss();
			break;
		case R.id.okBtn:
			if (mListener != null)
				mListener.onOkClick();
			mAlertDialog.dismiss();
			break;
		}

	}

}
