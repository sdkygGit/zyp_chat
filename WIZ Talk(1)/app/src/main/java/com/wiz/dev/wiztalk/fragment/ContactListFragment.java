package com.wiz.dev.wiztalk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.ContactDetailActivity_;
import com.wiz.dev.wiztalk.activity.ContactSearchActivity_;
import com.wiz.dev.wiztalk.activity.ContactSubListActivity_;
import com.wiz.dev.wiztalk.activity.XchatActivity_;
import com.wiz.dev.wiztalk.adapter.ContactListAdapter;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetOrgInfoRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetOrgInfoResponse;
import com.wiz.dev.wiztalk.listener.OnMainFragmentHideListener;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;
import com.wiz.dev.wiztalk.listener.OnMemberClickListener;
import com.wiz.dev.wiztalk.listener.OnMembersCheckedChangedListener;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.ContactItemFooterView;
import com.wiz.dev.wiztalk.view.ContactItemFooterView_;
import com.wiz.dev.wiztalk.view.ListHeaderContactSearch;
import com.wiz.dev.wiztalk.view.ListHeaderContactSearch_;
import com.wiz.dev.wiztalk.view.PinnedHeaderPullToRefreshListView;

import org.afinal.simplecache.ACache;
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@EFragment(R.layout.fragment_contact_list)
public class ContactListFragment extends FragmentBase implements IMainFragment, OnMainFragmentHideListener, RestErrorHandler {

    private static final String TAG = "ContactListFragment";

    @FragmentArg
    String localUserName;

    @FragmentArg
    boolean isSelectMode;

    //	@FragmentArg("selectMembers")
    List<Member> selectMembers;

    // @RestService
    // IMInterface mRestClient;

    //	IMInterface mIMInterfaceProxy;
    Appmsgsrv8094 mIMInterfaceProxy;

    // @Bean
    // MyErrorHandler myErrorHandler;

    @ViewById(android.R.id.list)
//	PinnedHeaderListView mListView;
            PinnedHeaderPullToRefreshListView mListView;

    @Bean
    ContactListAdapter mContactListAdapter;

    public boolean isLoaded = false; //是否加载过

    @ViewById
    ImageView search;

    @ViewById
    View hear_view;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        L.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        L.d(TAG, "onResume()");
//		mContactListAdapter.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        mContactListAdapter.onPause();
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        L.d(TAG, "onAttach()");
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        L.d(TAG, "onStart()");
        super.onStart();
    }

    @AfterInject
    void afterInject() {
        L.d(TAG, "afterInject() localUserName:" + localUserName);

//		mIMInterfaceProxy = IMInterfaceProxy.create();
        mIMInterfaceProxy = Appmsgsrv8094Proxy.create(8 * 1000);
        mIMInterfaceProxy.setRestErrorHandler(this);

        // changeData();
    }

    private void changeData(GetOrgInfoResponse response) {
        LinkedHashMap<String, List<Member>> map = new LinkedHashMap<String, List<Member>>();

        map.put("公共通讯录", null);
        map.put("我的常联系人", null);

        if (response != null
                && response.BaseResponse.Ret == BaseResponse.RET_SUCCESS) {
            List<Member> list = Utils.listFindPath(
                    response.OgnizationMemberList, response.UserOgnization);
            Collections.reverse(list);

            map.put("公共通讯录", list);

            for(int i=0;i<response.StarMemberList.size();i++){
                if(MyApplication.getInstance().getLocalUserName().equalsIgnoreCase(response.StarMemberList.get(i).UserName)){
                    response.StarMemberList.remove(i);
                    break;
                }
            }

            map.put("我的常联系人", response.StarMemberList);
        }

        // map.put("公共通讯录", new ArrayList<Member>());
        // map.put("我的常联系人", new ArrayList<Member>());

        mContactListAdapter.setOnMemberCheckedChangedListener(null);
        mContactListAdapter.changeData(map);
        mContactListAdapter.setOnMemberCheckedChangedListener(mOnMemberCheckedChangedListener);
        mContactListAdapter.setOnMembersCheckedChangedListener(mOnMembersCheckedChangedListener);
    }

    @AfterViews
    void afterViews() {
        L.d(TAG, "afterViews()");
        String titleName = getResources().getString(R.string.tab_cts);
        /*initTopBarForBoth2(titleName,
                R.drawable.actionbar_left_ico,
				-1,null);*/
//		initTopBarForOnlyTitle(titleName);
        if (isSelectMode) {
            hear_view.setVisibility(View.GONE);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactSearchActivity_.intent(getActivity()).start();
            }
        });


        mContactListAdapter.setSelectMembers(selectMembers);
        mContactListAdapter.setLockMembers(lockMembers);
        mContactListAdapter.setIsSelectMode(isSelectMode);
        mContactListAdapter.setOnMemberCheckedChangedListener(mOnMemberCheckedChangedListener);
        mContactListAdapter.setOnMembersCheckedChangedListener(mOnMembersCheckedChangedListener);

        // mListView.setMode(Mode.DISABLED);

        mListView.setPinHeaders(false);
        // mListView.getRefreshableView().setPinHeaders(false);
        mListView.setOnItemClickListener(mOnItemClickListener);
//		mListView.setMode(Mode.DISABLED);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getOrgInfoDoInBackground();
            }
        });
        mListView.getRefreshableView().setFadingEdgeLength(0);

        setAdapter(isSelectMode);

        GetOrgInfoResponse response = (GetOrgInfoResponse) ACache.get(
                getActivity()).getAsObject("getOrgInfo");

        changeData(response);

        getOrgInfoDoInBackground();
    }

    private void setAdapter(boolean isSelectMode) {
        if (!isSelectMode) {
//			initHeaderView();
        }
        initFooterView();

        mListView.setAdapter(mContactListAdapter);
    }

    private ContactItemFooterView mFooterView;

    private void initHeaderView() {
        ListHeaderContactSearch headerView1 = ListHeaderContactSearch_.build(getActivity());
        mListView.addHeaderView(headerView1);
    }

    private void initFooterView() {
        ContactItemFooterView footer = ContactItemFooterView_
                .build(getActivity());
        mListView.addFooterView(footer);
        mFooterView = footer;
    }

    @Background(id = "getOrgInfo")
    void getOrgInfoDoInBackground() {
        L.d(TAG, "getOrgInfoDoInBackground() ");
        BackgroundExecutor.cancelAll("getOrgInfo", true);

        onPreExecute();

        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());

        GetOrgInfoRequest request = new GetOrgInfoRequest();
        request.BaseRequest = baseRequest;

        L.d(TAG, "getOrgInfoDoInBackground() rootUrl:" + mIMInterfaceProxy.getRootUrl());
        L.d(TAG, "getOrgInfoDoInBackground() request:" + request);

        // GetOrgInfoResponse response = mRestClient.getOrgInfo(request);
//		GetOrgInfoResponse response = mIMInterfaceProxy.getOrgInfo(request);
        GetOrgInfoResponse response = null;
        try {
            response = mIMInterfaceProxy.getOrgInfo(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        L.d(TAG, "getOrgInfoDoInBackground() response:" + response);
        onPostExecute(response);
    }

    @UiThread
    void onPreExecute() {
        L.d(TAG, "onPreExecute()");
        mFooterView.show();
    }

    @UiThread
    void onPostExecute(GetOrgInfoResponse response) {
        L.d(TAG, "onPostExecute()");
        // Utils.longLogD(TAG, "response:" + response);

        // Call onRefreshComplete when the list has been refreshed.
        mListView.onRefreshComplete();


        mFooterView.hide();

        if (isCanceledGetOrgInfo || getActivity() == null) {
            return;
        }

        if (response == null) {
            return;
        }
        if (response.OgnizationMemberList == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            if (isFragmentShowing) {
                Toast.makeText(getActivity(), response.BaseResponse.ErrMsg,
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }
        //TODO 缓存
        GetOrgInfoResponse responseCache = (GetOrgInfoResponse) ACache.get(
                getActivity()).getAsObject("getOrgInfo");
        if (!isEqueals(response, responseCache)) {
            changeData(response);
            ACache.get(getActivity()).put("getOrgInfo", response);
        }
    }

    private boolean isEqueals(GetOrgInfoResponse r1,
                              GetOrgInfoResponse r2) {
        String str1 = Utils.writeValueAsString(r1);
        String str2 = Utils.writeValueAsString(r2);
        if (str1 != null && str1.equals(str2)) {
            return true;
        }
        return false;
    }

    private PinnedHeaderPullToRefreshListView.OnItemClickListener mOnItemClickListener = new PinnedHeaderPullToRefreshListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int section, int position, long id) {
            L.d(TAG, "onItemClick() section:" + section);
            L.d(TAG, "onItemClick() position:" + position);

            ContactListAdapter adapter = mContactListAdapter;

            Member member = (Member) adapter.getItem(section, position);

            if (!isSelectMode) {
                if (member.isTypeOrg() || member.isTypeTenant()) {
                    ContactSubListActivity_.intent(getActivity())
                            .member(member).start();
                } else if (member.isTypeUser()) {

                    String localUserName = MyApplication.getInstance().getLocalUserName();

                    if(localUserName.equalsIgnoreCase(member.UserName)){
                        ContactDetailActivity_
                                .intent(getActivity())
                                .userName(member.UserName)
                                .member((Member) adapter.getItem(section, position))
                                .start();
                    }else{

                        String jid = member.Uid;
                        jid = jid.toLowerCase();
                        jid = jid.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME);

                        XchatActivity_.intent(getActivity()).remoteUserName(jid)
                                .remoteUserNick(member.NickName)
                                .chatType(MsgInFo.TYPE_CHAT)
                                .start();
                    }
                }
            } else {
                if (mOnMemberClickListener != null) {
                    mOnMemberClickListener.onMemberClick(member, view);
                }
            }
        }

        @Override
        public void onSectionClick(AdapterView<?> adapterView, View view,
                                   int section, long id) {
            L.d(TAG, "onSectionClick() section:" + section);
        }

        @Override
        public void onHeaderViewClick(AdapterView<?> adapterView, View view,
                                      int position) {
            L.d(TAG, "onHeaderViewClick() position:" + position);
            if (position == 0) {
//				ContactSearchActivity_.intent(getActivity()).start();
            } else if (position == 1) {
                ContactSearchActivity_.intent(getActivity()).start();
//				AppsActivity_.intent(getActivity()).localUserName(localUserName).start();
            }
        }

        @Override
        public void onFooterViewClick(AdapterView<?> adapterView, View view,
                                      int position) {
            L.d(TAG, "onFooterViewClick() position:" + position);
        }

    };

    // Listener

    private OnMemberClickListener mOnMemberClickListener;

    public void setOnMemberClickListener(OnMemberClickListener l) {
        mOnMemberClickListener = l;
    }

    private OnMemberCheckedChangedListener mOnMemberCheckedChangedListener;
    private OnMembersCheckedChangedListener mOnMembersCheckedChangedListener;

    public void setOnMemberCheckedChangedListener(
            OnMemberCheckedChangedListener l) {
        mOnMemberCheckedChangedListener = l;
        if (mContactListAdapter != null) {
            mContactListAdapter.setOnMemberCheckedChangedListener(l);
        }
    }

    public void setOnMembersCheckedChangedListener(
            OnMembersCheckedChangedListener l) {
        mOnMembersCheckedChangedListener = l;
        if (mContactListAdapter != null) {
            mContactListAdapter.setOnMembersCheckedChangedListener(l);
        }
    }

    boolean isFragmentShowing = false;

    @Override
    public void onTagChanged() {
        L.d(TAG, "onTagChanged()");
        //TODO 没有加载过，才加载
        if (!isLoaded)
            getOrgInfoDoInBackground();

        isFragmentShowing = true;
    }

    @Override
    public void onFragmentHide() {
        isFragmentShowing = false;
        //TODO 切换到其他页面设置为加载过。
        isLoaded = true;
    }

    @UiThread
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        mFooterView.hide();
        if (isCanceledGetOrgInfo || getActivity() == null) {
            return;
        }
        if (isFragmentShowing) {
            Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSelectMembers(List<Member> members) {
        this.selectMembers = members;
        if (mContactListAdapter != null) {
            mContactListAdapter.setSelectMembers(members);
        }
    }

    private List<Member> lockMembers;

    public void setLockMembers(List<Member> lockMembers) {
        this.lockMembers = lockMembers;
        if (mContactListAdapter != null) {
            mContactListAdapter.setLockMembers(lockMembers);
        }
    }

    private boolean isCanceledGetOrgInfo = false;

    @Override
    public void onDestroy() {
        BackgroundExecutor.cancelAll("getOrgInfo", true);
        isCanceledGetOrgInfo = true;

        super.onDestroy();
    }

    public void notifyDataSetChanged() {
        mContactListAdapter.notifyDataSetChanged();
    }
}
