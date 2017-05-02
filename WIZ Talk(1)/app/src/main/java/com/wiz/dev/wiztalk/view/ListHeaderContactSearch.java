package com.wiz.dev.wiztalk.view;

import org.androidannotations.annotations.EViewGroup;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.wiz.dev.wiztalk.R;

@EViewGroup(R.layout.list_header_contact_search)
public class ListHeaderContactSearch extends RelativeLayout {

	public ListHeaderContactSearch(Context context) {
		super(context);
	}

	public ListHeaderContactSearch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListHeaderContactSearch(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

}
