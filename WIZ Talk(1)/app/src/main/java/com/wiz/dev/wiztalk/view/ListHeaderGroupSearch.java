package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.wiz.dev.wiztalk.R;

import org.androidannotations.annotations.EViewGroup;

@EViewGroup(R.layout.list_header_group_search)
public class ListHeaderGroupSearch extends RelativeLayout {

	public ListHeaderGroupSearch(Context context) {
		super(context);
	}

	public ListHeaderGroupSearch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListHeaderGroupSearch(Context context, AttributeSet attrs,
								 int defStyle) {
		super(context, attrs, defStyle);
	}

}