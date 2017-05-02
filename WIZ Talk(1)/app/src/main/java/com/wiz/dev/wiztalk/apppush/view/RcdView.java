package com.wiz.dev.wiztalk.apppush.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.wiz.dev.wiztalk.R;

@EViewGroup(R.layout.view_rcd)
public class RcdView extends FrameLayout {

	@ViewById
	RcdProgressView viewRcdProgress;

	@ViewById
	RcdVoiceView viewRcdVoice;

	@ViewById
	RcdCancelView viewRcdCancel;

	@ViewById
	RcdWarningView viewRcdWarning;
	
	public RcdView(Context context) {
		super(context);
	}

	public RcdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RcdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void hideRcdViews() {
		this.setVisibility(View.GONE);
		this.viewRcdProgress.setVisible(false);
		this.viewRcdVoice.setVisible(false);
		this.viewRcdCancel.setVisible(false);
		this.viewRcdWarning.setVisible(false);
	}
	
	public void showRcdView(View view) {
		hideRcdViews();
		if (view != null) {
			this.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
		}
	}
}
