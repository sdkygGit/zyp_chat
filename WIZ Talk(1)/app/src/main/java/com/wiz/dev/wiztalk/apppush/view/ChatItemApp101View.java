package com.wiz.dev.wiztalk.apppush.view;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.view.ChatItemView;


@EViewGroup(R.layout.list_item_chat_app_101)
public class ChatItemApp101View extends ChatItemView {

//	@ViewById(R.id.tv_time)
//	TextView tvTime;

	@ViewById
	public TextView tvTitle;

	@ViewById
	public TextView tvContent;

	@ViewById
	View content_lay;

	public ChatItemApp101View(Context context) {
		super(context);
	}

	@Override
	public void bindOther(XmppMessage message) {

		tvTitle.setText(message.getExtRemoteDisplayName());
		tvContent.setText(message.getPushContent());

	}

	@Click(R.id.iv_icon)
	void onClickIcon(View view) {
		/*ContactDetailActivity_.intent(getContext())
				.userName(message.getExtRemoteUserName()).start();*/
		// TODO: 2016/3/20 跳转应用 
	}
	public static final String TAG = "ChatItemApp101View";


	@LongClick(R.id.content_lay)
	public void layoutVoice() {
		Log.d(TAG, "layout_voice()");
		initPopupWindow();
		showPopuWindow(content_lay);
	}
	PopupWindow popWin;
	protected View popView;
	protected void initPopupWindow() {
		if (popWin == null) {
			popView = LayoutInflater.from(mContext).inflate(
					R.layout.popup_list_item_long_click, null);

			View copyLay = (View) popView.findViewById(R.id.tvCopy);
			copyLay.setVisibility(View.GONE);
			View line = (View) popView.findViewById(R.id.line);
			line.setVisibility(View.GONE);

			TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
			tvDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					messageDao.delete(message);
					mContext.getContentResolver().notifyChange(
							XmppMessageContentProvider.CONTENT_URI, null);
					popWin.dismiss();
				}
			});

			popWin = new PopupWindow(popView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			popWin.setOutsideTouchable(true);
			popView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popWin.dismiss();
				}
			});
			// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
			// 我觉得这里是API的一个bug
			popWin.setBackgroundDrawable(new BitmapDrawable());
		}
	}

	protected void showPopuWindow(View iv) {
		popView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int popupWidth = popView.getMeasuredWidth();
		int popupHeight = popView.getMeasuredHeight();
		int[] location = new int[2];
		iv.getLocationOnScreen(location);
		popWin.showAtLocation(iv, Gravity.NO_GRAVITY,
				(location[0] + iv.getWidth() / 2) - popupWidth / 2, location[1]
						- popupHeight);
	}


}
