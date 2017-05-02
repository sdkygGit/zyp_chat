package com.wiz.dev.wiztalk.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wiz.dev.wiztalk.R;


@EViewGroup(R.layout.grid_item_biaoqing)
public class BiaoqingItemView extends LinearLayout {

	@ViewById
	ImageView ivIcon;
	
	public BiaoqingItemView(Context context) {
		super(context);
	}

	public BiaoqingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void bind(int resId) {
		ivIcon.setImageResource(resId);
	}
}
