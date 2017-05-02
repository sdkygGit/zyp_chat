package com.wiz.dev.wiztalk.apppush.view;

import android.content.Context;

import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.view.ChatItemView;

import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class ChatItemNullView extends ChatItemView {

	public ChatItemNullView(Context context) {
		super(context);
	}

	@Override
	public void bindOther(XmppMessage message) {
//		super.bind(message);
	}
	
}