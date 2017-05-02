package com.wiz.dev.wiztalk.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.TextView;

import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.R;


@EViewGroup(R.layout.list_item_chat_tips)
public class XChatItemTipsView extends ChatItemView {

	@ViewById
	TextView tvContent;
	
	public XChatItemTipsView(Context context) {
		super(context);
	}

	@Override
	public void bindOther(XmppMessage message) {
		tvContent.setText(message.getBody());
	}

}