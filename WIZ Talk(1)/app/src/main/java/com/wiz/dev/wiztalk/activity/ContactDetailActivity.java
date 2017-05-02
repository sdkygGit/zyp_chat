package com.wiz.dev.wiztalk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.request.AddOrRemoveContactRequest;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberResponse;
import com.wiz.dev.wiztalk.dto.response.AddOrRemoveContactResponse;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.util.RoundImageView;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.HeaderLayout;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import java.lang.reflect.Field;

@EActivity(R.layout.activity_contact_detail_2)
public class ContactDetailActivity extends ActionBarActivity implements
		RestErrorHandler {

	// intent key
	public static final String USERNAME_JID = "USERNAME_JID";
	public static final String USERNICKNAME = "USERNICKNAME";

	private static final String TAG = "ContactDetailActivity";
	
	@Extra
	String userName;

	@Extra("Member")
	Member member;

	@ViewById
	ImageView ivAvatar;

	@ViewById
	Button btnSendMsg;

	@ViewById
	TextView tvName;

	@ViewById
	TextView tvNickName;

	@ViewById
	ImageView ivStar;

	@DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
	int biggerSize;

	@ViewById
	TextView tvMobile;

	@ViewById
	TextView tvTel;

	@Extra
	Integer fromWhere;//从哪个界面跳转来的

	// 从单聊界面跳转到 某个联系的详情界面，点发送消息
	public static final int FROM_WHERE_IS_SIGAL_CHAT = 0x200;
	//从群聊来的
	public static final int FROM_WHERE_IS_GTOUP_CHAT = 0x201;
	//从通讯录界面来的
	public static final int FROM_WHERE_IS_CONTACT_LIST = 0x202;
	//从联系人搜索界面来的
	public static final int FROM_WHERE_IS_CONTACT_SEARCH = 0x203;
	//从群聊成员列表界面来的
	public static final int FROM_WHERE_IS_GROUP_CHAT_DETAIL = 0x204;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: 2016/3/24 设置ActionBar 的title的color
//        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//        TextView actionBarTitle = (TextView) findViewById(titleId);
//        actionBarTitle.setTextColor(getResources().getColor(R.color.toolbartxtcolor));
		setOverflowShowingAlways();
		MyApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterViews
	void afterViews() {
		final ActionBar bar = getSupportActionBar();
		bar.hide();
		initTopBarForLeft("详细资料");
		bind(member);
		getMemberDoInBackground();
	}


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
		//R.drawable.base_action_bar_back_bg_selector
	}
	void bind(Member member) {
		this.member = member;
		if (member != null) {
			String localUserName = MyApplication.getInstance().getLocalUserName();
			btnSendMsg.setVisibility(localUserName.equalsIgnoreCase(member.UserName) ? View.GONE : View.VISIBLE);
			ivStar.setVisibility(localUserName.equalsIgnoreCase(member.UserName) ? View.INVISIBLE : View.VISIBLE);
			if(member.StarFriend == 0)
				ivStar.setBackgroundResource(R.drawable.star_01);
			else
				ivStar.setBackgroundResource(R.drawable.star_02);

			tvName.setText(member.NickName);
			tvNickName.setText(member.OrgName);
			tvMobile.setText(member.Mobile);
			tvTel.setText(member.Tel);
			DisplayImageOptions options = ImagerLoaderOptHelper.getConerOpt();

			final String avatarUrl = Utils.getAvataUrl(member.UserName, biggerSize);

			if (ivAvatar.getTag() == null || ivAvatar.getTag() != avatarUrl) {
				Bitmap bitmapFromCache = ImagerLoaderOptHelper.getBitmapFromCache(avatarUrl, new ImageSize(biggerSize, biggerSize));
				
				if (bitmapFromCache != null) {
					Bitmap bitmap = RoundImageView.toRoundBitmap(bitmapFromCache);
					ivAvatar.setImageBitmap(bitmap);
					return;
				}
				ImageLoader.getInstance().displayImage(avatarUrl, ivAvatar, options,new
						SimpleImageLoadingListener(){
							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view, loadedImage);
								// TODO: 2016/4/3 增加缓存 
								ImagerLoaderOptHelper.putCache(avatarUrl, new ImageSize(biggerSize, biggerSize),loadedImage);
							}
						});

			}
		}

		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}


	@Click(R.id.ivStar)
	void onAddContact(View view){
		addOrRemoveContactDoInBackground();
	}

	@Click(R.id.btnSendMsg)
	void onClickBtnSendMsg(View view) {
		String jid = member.Uid;
		jid = jid.toLowerCase();
		jid = jid.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME);


		if (fromWhere != null) {
			switch (fromWhere) {
				case FROM_WHERE_IS_SIGAL_CHAT:
					finish();
					break;
				case FROM_WHERE_IS_GTOUP_CHAT:
				case FROM_WHERE_IS_CONTACT_LIST:
				case FROM_WHERE_IS_CONTACT_SEARCH:
				case FROM_WHERE_IS_GROUP_CHAT_DETAIL:
					XchatActivity_.intent(this).remoteUserName(jid)
							.remoteUserNick(member.NickName)
							.chatType(MsgInFo.TYPE_CHAT)
							.start();

					finish();
					break;
			}
		} else {
			XchatActivity_.intent(this).remoteUserName(jid)
					.remoteUserNick(member.NickName)
					.chatType(MsgInFo.TYPE_CHAT)
					.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu()");
		// Inflate the menu; this adds items to the action bar if it is present.
		if (member != null) {
			getMenuInflater().inflate(R.menu.activity_contact_detail, menu);
			MenuItem item = menu.findItem(R.id.action_star_friend);
			item.setTitle(member.StarFriend == 0 ? R.string.starFriend_set
					: R.string.starFriend_cancel);
		}
		return true;
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// Log.d(TAG, "onPrepareOptionsMenu()");
	// MenuItem item = menu.findItem(R.id.action_star_friend);
	// item.setTitle(member.StarFriend == 0 ? R.string.starFriend_set
	// : R.string.starFriend_cancel);
	// return super.onPrepareOptionsMenu(menu);
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_star_friend) {
			addOrRemoveContactDoInBackground();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Click(R.id.layoutMobile)
	void onClickMobile(View v) {
		if (member != null && !TextUtils.isEmpty(member.Mobile)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + member.Mobile));
			startActivity(intent);
		} else {
			Toast.makeText(this, getResources().getString(R.string.contact_detail_tips_no_mobile), Toast.LENGTH_SHORT).show();
		}
	}

	@Click(R.id.layoutTel)
	void onClickTel(View view) {
		if (member != null && !TextUtils.isEmpty(member.Tel)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + member.Tel));
			startActivity(intent);
		} else {
			Toast.makeText(this, getResources().getString(R.string
					.tips_contactDetail_tel_isnull), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private Appmsgsrv8094 mIMInterfaceProxy;

	@AfterInject
	void afterInject() {
		mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
		mIMInterfaceProxy.setRestErrorHandler(this);
	}

	@Background
	void addOrRemoveContactDoInBackground() {
		BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
		AddOrRemoveContactRequest request = new AddOrRemoveContactRequest();
		request.BaseRequest = baseRequest;
		request.Uid = member.Uid;
		request.StarFriend = member.StarFriend == 0 ? 1 : 0;
		L.d(TAG, "doInBackground() request:" + request);
		
		AddOrRemoveContactResponse response = mIMInterfaceProxy
				.addOrRemoveContact(request);
		addOrRemoveContactOnPostExecute(response);
	}

	@UiThread
	void addOrRemoveContactOnPostExecute(AddOrRemoveContactResponse response) {
		L.d(TAG, "onPostExecute() response:" + response);
		if (response == null) {
			return;
		}

		if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
			Toast.makeText(this, response.BaseResponse.ErrMsg,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (member.StarFriend == 0) {
			member.StarFriend = 1;
			Toast.makeText(this, "已标为常联系人", Toast.LENGTH_SHORT).show();
		} else {
			member.StarFriend = 0;
			Toast.makeText(this, "已取消常联系人", Toast.LENGTH_SHORT).show();
		}

		bind(member);
	}

	@Background
	void getMemberDoInBackground() {
		BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
		GetMemberRequest request = new GetMemberRequest();
		request.BaseRequest = baseRequest;
		request.UserName = userName;

		Log.d(TAG, "getMemberDoInBackground() request:" + request);

		GetMemberResponse response = mIMInterfaceProxy.getMember(request);

		getMemberOnPostExecute(response);
	}

	@UiThread
	void getMemberOnPostExecute(GetMemberResponse response) {
		Log.d(TAG, "getMemberOnPostExecute() response:" + response);
		if (response == null) {
			return;
		}

		if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
			Toast.makeText(this, response.BaseResponse.ErrMsg,
					Toast.LENGTH_SHORT).show();
			return;
		}
		bind(response.Member);
	}

	@UiThread
	@Override
	public void onRestClientExceptionThrown(NestedRuntimeException e) {
		Toast.makeText(this, "访问失败", Toast.LENGTH_SHORT).show();
	}
}
