package com.wiz.dev.wiztalk.view;

import org.androidannotations.annotations.EViewGroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.wiz.dev.wiztalk.R;


@EViewGroup(R.layout.list_item_contact_footer)
public class ContactItemFooterView extends RelativeLayout {

	public ContactItemFooterView(Context context) {
		super(context);
	}

	public ContactItemFooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactItemFooterView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void hide() {
		this.setVisibility(View.GONE);
//		this.setPadding(0, -this.getHeight(), 0, 0);
		this.setPadding(0, Integer.MIN_VALUE, 0, 0);
	}
	
	public void show() {
		this.setVisibility(View.VISIBLE);
		this.setPadding(0, 0, 0, 0);
	}
}
