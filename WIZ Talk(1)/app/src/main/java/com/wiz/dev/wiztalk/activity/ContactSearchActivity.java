package com.wiz.dev.wiztalk.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.ContactSearchAdapter;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.SearchRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.SearchResponse;
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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

@EActivity(R.layout.activity_contact_search)
public class ContactSearchActivity extends ActionBarActivity implements
		OnScrollListener, RestErrorHandler {

	private static final String TAG = "ContactSearchActivity";

	@ViewById
	EditText etSearch;

	@ViewById
	ImageButton btnSearch;

	@ViewById(android.R.id.list)
	ListView mListView;

	@Bean
	ContactSearchAdapter mAdapter;

	private ContactItemFooterView mFooterView;

	Appmsgsrv8094 mIMInterfaceProxy;

	int mOffset;

	@AfterInject
	void afterInject() {
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
		final ActionBar bar = getSupportActionBar();
		bar.hide();
		initTopBarForLeft("搜索联系人");

		mListView.setOnScrollListener(this);

		ContactItemFooterView footerView = ContactItemFooterView_.build(this);
		mFooterView = footerView;

		setAdapter();
		mFooterView.hide();

		MyApplication.getInstance().addActivity(this);
		
//		etSearch.setText("刘");

	}

	@Override
	protected void onDestroy() {
		MyApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	private void setAdapter() {
		mAdapter.clear();
		mListView.removeFooterView(mFooterView);
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mAdapter);
	}

	private String searchKey;

	@Click(R.id.btnSearch)
	void onClickBtnSearch(View v) {
		Log.d(TAG, "onClickBtnSearch() searchKey:"+searchKey);
		searchKey = etSearch.getText().toString();
		if (!TextUtils.isEmpty(searchKey)) {
			mAdapter.clear();
			setAdapter();
			mFooterView.show();
			mOffset = 0;
			doInBackground(searchKey, mOffset);
		}
	}

	int lastVisibleItem = 0;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d(TAG, "onScrollStateChanged()");
		Log.d(TAG, "onScrollStateChanged() lastVisibleItem:" + lastVisibleItem);
		Log.d(TAG,
				"onScrollStateChanged() mAdapter.getCount():"
						+ mAdapter.getCount());
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleItem == mAdapter.getCount()) {
			mOffset++;
			doInBackground(searchKey,mOffset);
		}
	}

	private int Count;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.d(TAG, "onScroll()");
		Log.d(TAG, "onScroll() totalItemCount:" + totalItemCount);
		Log.d(TAG, "onScroll() Count:" + Count);
		// 计算最后可见条目的索引
		lastVisibleItem = firstVisibleItem + visibleItemCount - 1;

		// 所有的条目已经和最大条数相等，则移除底部的View
		// if (totalItemCount == Count + 1) {
		// mListView.removeFooterView(mFooterView);
		// }
	}

	@Background
	void doInBackground(String searchKey, int offset) {
		Log.d(TAG, "doInBackground() ");
		onPreExecute();
		SearchResponse response =null;
		
	
		BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
		SearchRequest request = new SearchRequest();
		request.BaseRequest = baseRequest;
		// request.SearchKey = "";
		request.SearchKey = searchKey;
		request.pageNo = offset;
		request.pageCount = 20;
		request.SearchType = SearchRequest.SEARCH_TYPE_USER;
		
		Log.d(TAG, "doInBackground() request:" + request);
		
		response = mIMInterfaceProxy.search(request);
		
		onPostExecute(response);
	}

	@UiThread
	void onPreExecute() {
		mFooterView.setVisibility(View.VISIBLE);
	}

	@UiThread
	void onPostExecute(SearchResponse response) {
		Log.d(TAG, "onPostExecute() response:" + response);
		if (response == null) {
			Toast.makeText(this, "访问失败",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
			Toast.makeText(this, response.BaseResponse.ErrMsg,
					Toast.LENGTH_LONG).show();
			return;
		}
		

		Count = response.Count;
		if(Count==0){
			mListView.removeFooterView(mFooterView);
			Toast.makeText(this, "查找不到此联系人",
					Toast.LENGTH_SHORT).show();
			
			return;
		}
		
		mAdapter.addMembers(response.MemberList);

		if (mAdapter.getCount() == Count) {
			mListView.removeFooterView(mFooterView);
		}

		if (Count > 0 && mAdapter.getCount() == Count) {
			mListView.removeFooterView(mFooterView);
			Toast.makeText(this, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
		}
	}

	@UiThread
	@Override
	public void onRestClientExceptionThrown(NestedRuntimeException e) {
		Toast.makeText(this, "访问失败", Toast.LENGTH_LONG).show();
		if (mAdapter.getCount() == 0) {
			mListView.removeFooterView(mFooterView);
		}
	}

	@ItemClick(android.R.id.list)
	void onItemClick(Member member) {
		ContactDetailActivity_.intent(this).member(member)
				.userName(member.UserName).start();
	}

}
