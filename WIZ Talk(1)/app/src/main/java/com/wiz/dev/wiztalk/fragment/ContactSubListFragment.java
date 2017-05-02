package com.wiz.dev.wiztalk.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.ContactListAdapter;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetOrgUserListRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetOrgUserListResponse;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;
import com.wiz.dev.wiztalk.listener.OnMemberClickListener;
import com.wiz.dev.wiztalk.listener.OnMembersCheckedChangedListener;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.ContactItemFooterView;
import com.wiz.dev.wiztalk.view.ContactItemFooterView_;
import com.wiz.dev.wiztalk.view.PinnedHeaderPullToRefreshListView;

@EFragment(R.layout.fragment_contact_sub_list)
public class ContactSubListFragment extends Fragment implements RestErrorHandler {

	protected static final String TAG = "ContactSubListFragment";

	@FragmentArg("Member")
	Member member;

	@FragmentArg("isSelectMode")
	boolean isSelectMode;

	@Bean
	ContactListAdapter mContactSubListAdapter;

	Appmsgsrv8094 mRestClient;

	@ViewById(R.id.listContactSub)
//	PinnedHeaderListView mPinnedHeaderListView;
			PinnedHeaderPullToRefreshListView mPinnedHeaderListView;

	@AfterInject
	void afterInject() {
		mRestClient = Appmsgsrv8094Proxy.create();
		mRestClient.setRestErrorHandler(this);
	}

	@AfterViews
	void afterViews() {
		Log.d(TAG, "afterViews()");

		ActionBar bar = getActionBar();
		bar.setTitle(member.NickName);

		mPinnedHeaderListView.setAdapter(mContactSubListAdapter);
		mPinnedHeaderListView.setPinHeaders(false);
		mPinnedHeaderListView.setOnItemClickListener(mOnItemClickListener);
		mPinnedHeaderListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
//						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//				// Update the LastUpdatedLabel
//				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				doInBackground();
			}
		});
		
		
		mContactSubListAdapter.setSelectMembers(mSelectMembers);
		mContactSubListAdapter.setLockMembers(lockMembers);
		mContactSubListAdapter.setOnMemberCheckedChangedListener(mOnMemberCheckedChangedListener);
		mContactSubListAdapter.setOnMembersCheckedChangedListener(mOnMembersCheckedChangedListener);
		mContactSubListAdapter.setIsSelectMode(isSelectMode);

		Map<String, List<Member>> map = new LinkedHashMap<String, List<Member>>();

		if (member.MemberList != null && member.MemberList.size() != 0) {
			sortMemberListBySort(member.MemberList);
			map.put("组织结构", member.MemberList);
		}

		map.put("成员", new ArrayList<Member>());

		mContactSubListAdapter.changeData(map);

		setAdapter();

//		doInBackground();
		//TODO 设置自动刷新
		mPinnedHeaderListView.setRefreshing();
		
	}

	private void sortMemberListBySort(List<Member> list) {
		if (list == null) {
			return;
		}
		
		Collections.sort(list, new Comparator<Member>() {
			@Override
			public int compare(Member lhs, Member rhs) {
//				return 0;
				return lhs.sort - rhs.sort;
			}
		});
	}
	
	private void setAdapter() {
		initFooterView();

		mPinnedHeaderListView.setAdapter(mContactSubListAdapter);
	}

	private ContactItemFooterView mFooterView;

	private void initFooterView() {
		ContactItemFooterView footer = ContactItemFooterView_
				.build(getActivity());
		mPinnedHeaderListView.addFooterView(footer);

		mFooterView = footer;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		mFooterView.hide();
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	private PinnedHeaderPullToRefreshListView.OnItemClickListener mOnItemClickListener = new PinnedHeaderPullToRefreshListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int section, int position, long id) {
			Log.d(TAG, "onItemClick() section:" + section);
			Log.d(TAG, "onItemClick() position:" + position);

			ContactListAdapter adapter = mContactSubListAdapter;

			Member member = (Member) adapter.getItem(section, position);

			if (mOnMemberClickListener != null) {
				mOnMemberClickListener.onMemberClick(member,view);
			}
		}

		@Override
		public void onSectionClick(AdapterView<?> adapterView, View view,
				int section, long id) {
			Log.d(TAG, "onSectionClick() section:" + section);
		}

		@Override
		public void onHeaderViewClick(AdapterView<?> adapterView, View view,
				int position) {
			Log.d(TAG, "onHeaderViewClick() position:" + position);
		}

		@Override
		public void onFooterViewClick(AdapterView<?> adapterView, View view,
				int position) {
			Log.d(TAG, "onFooterViewClick() position:" + position);
		}

	};

	private OnMemberClickListener mOnMemberClickListener;

	public void setOnMemberClickListener(OnMemberClickListener l) {
		mOnMemberClickListener = l;
	}

	private OnMemberCheckedChangedListener mOnMemberCheckedChangedListener;
	private OnMembersCheckedChangedListener mOnMembersCheckedChangedListener;

	public void setOnMemberCheckedChangedListener(
			OnMemberCheckedChangedListener l) {
		mOnMemberCheckedChangedListener = l;
		if (mContactSubListAdapter != null) {
			mContactSubListAdapter.setOnMemberCheckedChangedListener(l);
		}
	}
	public void setOnMembersCheckedChangedListener(
			OnMembersCheckedChangedListener l) {
		mOnMembersCheckedChangedListener = l;
		if (mContactSubListAdapter != null) {
			mContactSubListAdapter.setOnMembersCheckedChangedListener(l);
		}
	}

	List<Member> mSelectMembers;

	public void setSelectMembers(List<Member> members) {
		mSelectMembers = members;
		if (mContactSubListAdapter != null) {
			mContactSubListAdapter.setSelectMembers(members);
		}
	}

	private ActionBar getActionBar() {
		ActionBarActivity activity = (ActionBarActivity) getActivity();
		return activity.getSupportActionBar();
	}

	@UiThread
	@Override
	public void onRestClientExceptionThrown(NestedRuntimeException e) {
		mFooterView.hide();
		
		if (isCanceledGetOrgUserList) {
			return;
		}
		Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
	}

	@UiThread
	void onPreExecute() {
		Log.d(TAG, "onPreExecute()");
//		mFooterView.show();
		mFooterView.hide();
	}

	@Background(id = "getOrgUserList")
	void doInBackground() {
		onPreExecute();

		BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());

		GetOrgUserListRequest request = new GetOrgUserListRequest();
		request.BaseRequest = baseRequest;
		request.orgid = member.Uid;

		Log.d(TAG, "GetOrgUserListRequest:" + request);
		Log.d(TAG,
				"GetOrgUserListRequest:" + Utils.writeValueAsString(request));

		GetOrgUserListResponse response = mRestClient.getOrgUserList(request);

		onPostExecut(response);
	}

	@UiThread
	void onPostExecut(GetOrgUserListResponse response) {
		Log.d(TAG, "GetOrgUserListResponse:" + response);

		// Call onRefreshComplete when the list has been refreshed.
		mPinnedHeaderListView.onRefreshComplete();
		
		if (isCanceledGetOrgUserList) {
			return;
		}
		
		mFooterView.hide();
		
		if (response == null) {
			return;
		}

		if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
			Toast.makeText(getActivity(), response.BaseResponse.ErrMsg,
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (response.MemberCount != 0) {
			Map<String, List<Member>> map = mContactSubListAdapter.getData();
			List<Member> list = map.get("成员");
			list.clear();
			list.addAll(response.MemberList);
			sortMemberListBySort(list);

			mContactSubListAdapter.setOnMemberCheckedChangedListener(null);
			mContactSubListAdapter.notifyDataSetChanged();
			mContactSubListAdapter.setOnMemberCheckedChangedListener(mOnMemberCheckedChangedListener);
			mContactSubListAdapter.setOnMembersCheckedChangedListener(mOnMembersCheckedChangedListener);
		}
	}

	private List<Member> lockMembers;

	public void setLockMembers(List<Member> lockMembers) {
		this.lockMembers = lockMembers;
		if (mContactSubListAdapter != null) {
			mContactSubListAdapter.setLockMembers(lockMembers);
		}
	}

	private boolean isCanceledGetOrgUserList = false;
	
	@Override
	public void onDestroy() {
		BackgroundExecutor.cancelAll("getOrgUserList", true);
		isCanceledGetOrgUserList = true;

		super.onDestroy();
	}

	public void notifyDataSetChanged() {
		mContactSubListAdapter.notifyDataSetChanged();
	}

}
