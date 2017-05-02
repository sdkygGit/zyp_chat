package com.wiz.dev.wiztalk.apppush.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


@EViewGroup(R.layout.list_item_app102_body)
public class App102BodyItem extends RelativeLayout {

	@ViewById
	TextView tv;
	
	public App102BodyItem(Context context) {
		super(context);
	}

	public void bind(AppPushEntity.ObjectContentEntity.BodyEntity item) {
		tv.setText(item.getContent());
	}

}
