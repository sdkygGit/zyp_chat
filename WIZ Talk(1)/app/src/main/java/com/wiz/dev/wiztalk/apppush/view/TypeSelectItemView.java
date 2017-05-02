package com.wiz.dev.wiztalk.apppush.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;

@EViewGroup(R.layout.grid_item_type_select)
public class TypeSelectItemView extends RelativeLayout {

	@ViewById
	TextView tvName;
	
	public TypeSelectItemView(Context context) {
		super(context);
	}

	public void bind(String item) {
		tvName.setText(item);
	}

	
}
