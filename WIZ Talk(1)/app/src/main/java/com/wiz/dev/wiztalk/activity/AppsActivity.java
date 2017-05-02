package com.wiz.dev.wiztalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.AppListAdapter;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetApplicationListRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetApplicationListResponse;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.view.ContactItemFooterView;
import com.wiz.dev.wiztalk.view.ContactItemFooterView_;
import com.wiz.dev.wiztalk.view.HeaderLayout;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import java.util.Iterator;
import java.util.List;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

@EActivity(R.layout.activity_apps)
public class AppsActivity extends ActionBarActivity implements
		OnItemClickListener, RestErrorHandler {

	public static final String TAG = "AppsActivity";

	// private GridView mGridView;

	@Extra
	String localUserName;

	@ViewById(android.R.id.list)
	PinnedHeaderListView mListView;

	@Bean
	AppListAdapter mAppListAdapter;

	Appmsgsrv8094 mIMInterfaceProxy;

	private ContactItemFooterView mFooterView;

	@Override
	protected void onPause() {
		mAppListAdapter.onPause();
		super.onPause();
	}
	
	@AfterInject
	void afterInject() {
		Log.d(TAG, "afterInject() localUserName:" + localUserName);
		mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
		mIMInterfaceProxy.setRestErrorHandler(this);
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
	}
	@AfterViews
	void afterViews() {
		initFooterView();
		// mListView.setAdapter(new AppAdapter(this));
		mListView.setAdapter(mAppListAdapter);
		mListView.setOnItemClickListener(this);

		final ActionBar bar = getSupportActionBar();
		bar.hide();
		initTopBarForLeft("应用");
		doInBackground();
	}

	private void initFooterView() {
		ContactItemFooterView footer = ContactItemFooterView_.build(this);
		mListView.addFooterView(footer);
		mFooterView = footer;
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	@Background
	void doInBackground() {
		onPreExecute();
		BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
		GetApplicationListRequest request = new GetApplicationListRequest();
		request.BaseRequest = baseRequest;
		Log.d(TAG, "doInBackground() request:" + request);
//		GetApplicationListResponse response = mIMInterfaceProxy
//				.getApplicationList(request);

		//获取所有App列表
		GetApplicationListResponse response = mIMInterfaceProxy
				.getAllApplicationList(request);

		onPostExecute(response);
	}

	@UiThread
	void onPreExecute() {
		mFooterView.show();
	}

	@UiThread
	void onPostExecute(GetApplicationListResponse response) {
		Log.d(TAG, "onPostExecute() response:" + response);
		mFooterView.hide();
		if (response == null) {
			return;
		}
		if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
			Toast.makeText(this, response.BaseResponse.ErrMsg,
					Toast.LENGTH_SHORT).show();
			return;
		}

		mAppListAdapter.changeData(response.memberList);
	}

	//
	// @Override
	// public void finish() {
	// super.finish();
	// overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
	// }

//	private class AppAdapter extends BaseAdapter {
//
//		private Context context;
//
//		public AppAdapter(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		public int getCount() {
//			return FakeData.APPNAMES.length;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//
//			ContactItemView view;
//
//			if (convertView == null) {
//				view = ContactItemView_.build(context);
//			} else {
//				view = (ContactItemView) convertView;
//			}
//
//			view.mNameTV.setText(FakeData.APPNAMES[position]);
//			view.mIconIV.setImageResource(FakeData.PHOTO[position
//					% FakeData.PHOTO.length]);
//			view.mIconIV.setScaleType(ScaleType.CENTER);
//
//			view.setDividerVisible(position != 0);
//
//			return view;
//		}
//	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// AppDetailActivity_.intent(this).start();
		Member member = (Member) parent.getItemAtPosition(position);
//			ChatActivity_.intent(this).localUserName(localUserName)
//			.remoteUserName(member.UserName)
//			.remoteDisplayName(member.NickName).start();

		AppDetailActivity_.intent(this).remoteUserName(member.UserName).nickName(member.NickName)
				.follow(member.Follow)
		.startForResult(REQUEST_CODE_APP_DETAIL);
	}

	private static final int REQUEST_CODE_APP_DETAIL = 4;
	
	@OnActivityResult(REQUEST_CODE_APP_DETAIL)
	void onActivityResultAppDetail(int resultCode, Intent data) {
		Log.d(TAG, "onActivityResultAppDetail() resultCode:" + resultCode);
		Log.d(TAG, "onActivityResultAppDetail() data:" + data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				Log.d(TAG, "onActivityResultAppDetail() data.getExtras():" + data.getExtras());
			}
			Bundle bundle = data.getExtras();
			String appname = bundle.getString("appname");
			String follow = bundle.getString("follow");
			Log.d(TAG, "onActivityResultAppDetail() appname:" + appname);
			Log.d(TAG, "onActivityResultAppDetail() follow:" + follow);
			
			List<Member> members = mAppListAdapter.getData();
			if (members != null) {
				for (Iterator<Member> it = members.iterator(); it.hasNext();) {
					Member m = it.next();
					if (m.UserName.equals(appname)) {
						m.Follow = follow;
						mAppListAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
		} else if (resultCode == AppDetailActivity.RESULT_FINISH) {
			finish();
		}
	}
	
	@UiThread
	@Override
	public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
		mFooterView.hide();
		Toast.makeText(this, "访问失败", Toast.LENGTH_SHORT).show();
	}
}
