package com.wiz.dev.wiztalk.apppush.view;


import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.apppush.OnOperationClickListener;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;

import java.util.List;

@EViewGroup(R.layout.footer_view)
public class FooterView extends RelativeLayout implements View.OnClickListener {
	
	@ViewById
	public FooterTextView viewFooterText;
	
	@ViewById
	public FooterListView viewFooterList;
	
	public FooterView(Context context) {
		super(context);
	}

	public FooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FooterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void bind(String remoteUserName, List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations) {
//		operations = new ArrayList<Operation>();
//		Operation opt;
//		Operation subOpt;
//		
//		opt = new Operation();
//		opt.content = "1";
//		operations.add(opt);
//		
//		subOpt = new Operation();
//		subOpt.content = "1-1";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "1-2";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "1-3";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "1-4";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "1-5";
//		opt.addOperation(subOpt);
//		
//		opt = new Operation();
//		opt.content = "2";
//		operations.add(opt);
//		
//		opt = new Operation();
//		opt.content = "3";
//		operations.add(opt);
//		
//		subOpt = new Operation();
//		subOpt.content = "3-1";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "3-2";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "3-3";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "3-4";
//		opt.addOperation(subOpt);
//		subOpt = new Operation();
//		subOpt.content = "3-5";
//		opt.addOperation(subOpt);
//		
//		operations = null;
		if (operations == null || operations.size() == 0) {
			viewFooterText.setVisibility(View.VISIBLE);
		} else {
			viewFooterList.setVisibility(View.VISIBLE);
		}
		
		viewFooterList.setOnOperationClickListener(mOnOperationClickListener);
		
		viewFooterText.bind(remoteUserName, operations);
		viewFooterList.bind(remoteUserName, operations);
		
		viewFooterText.ibTextToList.setOnClickListener(this);
		viewFooterList.ibListToText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == viewFooterText.ibTextToList) {
			viewFooterText.setVisibility(View.GONE);
			viewFooterList.setVisibility(View.VISIBLE);
		} else if (v == viewFooterList.ibListToText) {
			viewFooterText.setVisibility(View.VISIBLE);
			viewFooterList.setVisibility(View.GONE);
		}
	}
	
	private OnOperationClickListener mOnOperationClickListener;
	
	public void setOnOperationClickListener(OnOperationClickListener l) {
		mOnOperationClickListener = l;
	}
}
