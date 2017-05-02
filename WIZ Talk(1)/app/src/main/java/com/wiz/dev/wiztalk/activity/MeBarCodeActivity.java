package com.wiz.dev.wiztalk.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.HeaderLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_me_bar_code)
public class MeBarCodeActivity extends ActionBarActivity {

	@ViewById
	ImageView iv;

	HeaderLayout mHeaderLayout;
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.blueback,
				new HeaderLayout.onLeftImageButtonClickListener() {
					@Override
					public void onClick() {
						finish();
					}
				});
	}
	@AfterViews
	void afterViews() {
		String uid= MyApplication.getInstance().getUid();
		String userName=MyApplication.getInstance().getLocalUserName();
		iv.setImageBitmap(Utils.createCard(this, uid, userName));

		final ActionBar bar = getSupportActionBar();
		bar.hide();
		initTopBarForLeft("我的二维码");
	}
	
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
}
