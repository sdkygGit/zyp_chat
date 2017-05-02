package com.wiz.dev.wiztalk.apppush.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.view.ChatItemView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


@EViewGroup(R.layout.list_item_chat_app_101)
public class ChatItemApp100View extends ChatItemView {

//	@ViewById(R.id.tv_time)
//	TextView tvTime;

	@ViewById
	public TextView tvTitle;

	@ViewById
	public TextView tvContent;

	public ChatItemApp100View(Context context) {
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

}
