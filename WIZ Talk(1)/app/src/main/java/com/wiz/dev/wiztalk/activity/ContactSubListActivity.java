package com.wiz.dev.wiztalk.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.fragment.ContactSubListFragment;
import com.wiz.dev.wiztalk.fragment.ContactSubListFragment_;
import com.wiz.dev.wiztalk.listener.OnMemberClickListener;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.view.HeaderLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.activity_contact_sub_list)
public class ContactSubListActivity extends ActionBarActivity implements
		OnMemberClickListener {

	private static final String TAG = "ContactSubListActivity";
	@Extra("Member")
	Member member;
	HeaderLayout mHeaderLayout;
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.blueback,
				new HeaderLayout.onLeftImageButtonClickListener() {
					@Override
					public void onClick() {
						if (!popBackStack()) {
							finish();
						}
					}
				});
		//R.drawable.base_action_bar_back_bg_selector
	}
	@AfterViews
	void afterViews() {

		Log.d(TAG, "afterViews() :"+member.NickName);
		final ActionBar bar = getSupportActionBar();
		bar.hide();
		initTopBarForLeft(member.NickName);
		ContactSubListFragment fragment = ContactSubListFragment_.builder()
				.member(member).build();
		fragment.setOnMemberClickListener(this);
		addFragment(fragment);
		MyApplication.getInstance().addActivity(this);
		
	}

	@Override
	protected void onDestroy() {
		MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
		
	}

	@Override
	public boolean onSupportNavigateUp() {
		if (!popBackStack()) {
			finish();
		}
		// return super.onSupportNavigateUp();
		return true;
	}

	private void addFragment(Fragment newFragment) {
		Log.d(TAG, "addFragment() :"+member.NickName);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//TODO 设置Tag为nickName
		ft.add(R.id.layout_content, newFragment,member.NickName).commit();
	}

	private void addFragmentToStack(Fragment newFragment) {
		Log.d(TAG, "addFragmentToStack() :"+member.NickName);
		// Add the fragment to the activity, pushing this transaction
		// on to the back stack.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.layout_content, newFragment, null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}

	private boolean popBackStack() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
			return true;
			// return fm.popBackStackImmediate();
		}
		return false;
	}

	private boolean popBackStackToHome() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack(fm.getBackStackEntryAt(0).getId(),
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		}
		return false;
	}


	@Override
	public void onMemberClick(Member member, View view) {
		if (member.isTypeOrg()) {
			mHeaderLayout.setDefaultTitle(member.NickName);
			ContactSubListFragment fragment = ContactSubListFragment_.builder()
					.isSelectMode(false).member(member).build();
			fragment.setOnMemberClickListener(this);
			addFragmentToStack(fragment);
		} else if (member.isTypeUser()) {

			String localUserName = MyApplication.getInstance().getLocalUserName();

			if(localUserName.equalsIgnoreCase(member.UserName)){
				ContactDetailActivity_.intent(this).userName(member.UserName)
						.member(member).start();
			}else{

				String jid = member.Uid;
				jid = jid.toLowerCase();
				jid = jid.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME);

				XchatActivity_.intent(this).remoteUserName(jid)
						.remoteUserNick(member.NickName)
						.chatType(MsgInFo.TYPE_CHAT)
						.start();
			}

		}
	}
}
