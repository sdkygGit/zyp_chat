package com.wiz.dev.wiztalk.apppush.view;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;


@EViewGroup(R.layout.view_rcd_voice)
public class RcdVoiceView extends LinearLayout {

	@ViewById
	ImageView ivAmp;
	
	@ViewById
	TextView tvRcd;
	
	int[] amps;
	
	public RcdVoiceView(Context context) {
		super(context);
	}

	public RcdVoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterInject
	void afterInject() {
		TypedArray ampsTypedArray = getContext().getResources().obtainTypedArray(R.array.amps);
		amps = new int[ampsTypedArray.length()];
		for (int i = 0; i < amps.length; i++) {
			amps[i] = ampsTypedArray.getResourceId(i, 0);
		}
		ampsTypedArray.recycle();
	}
	
	public void setAmp(int amp) {
		ivAmp.setImageResource(amps[clip(amp, 0, amps.length)]);
	}
	
	public static int clip(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}
	
	public void setVisible(boolean visible) {
		this.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(int resid) {
		tvRcd.setText(resid);
	}
	
	public void setText(CharSequence text) {
		tvRcd.setText(text);
	}
}
