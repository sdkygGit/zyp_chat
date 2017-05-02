package com.wiz.dev.wiztalk.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.AppsActivity_;
import com.wiz.dev.wiztalk.activity.ContactDetailActivity_;
import com.wiz.dev.wiztalk.adapter.FindAdapter2;
import com.wiz.dev.wiztalk.dto.request.AddOrRemoveContactRequest;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.CheckUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberResponse;
import com.wiz.dev.wiztalk.dto.response.AddOrRemoveContactResponse;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.CheckUpdateResponse;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.rest.RestErrorHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.NestedRuntimeException;


@EFragment(R.layout.fragment_find)
public class FindFragment extends FragmentBase implements IMainFragment, RestErrorHandler {

    protected static final String TAG = "FindFragment";

    @ViewById(android.R.id.list)
    ListView mPinnedHeaderListView;

    private FindAdapter2 mSectionedBaseAdapter;

    /**
     * 调二维码扫描CODE
     */
    private static final int CAPTURE_REQUEST_CODE = 200;

    @FragmentArg
    String localUserName;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        L.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @AfterViews
    public void afterViews() {
        initTopBarForOnlyTitle("发现");

        mPinnedHeaderListView.setOnItemClickListener(mOnItemClickListener);
        mSectionedBaseAdapter = new FindAdapter2(getActivity());
        mPinnedHeaderListView.setAdapter(mSectionedBaseAdapter);
    }
    // private void initHeaderView() {
    // for (int i = 0; i < FakeData.FINDS.length; i++) {
    // ContactItemView view = new ContactItemView(getActivity());
    // view.mIconIV.setImageResource(FakeData.FIND_DRAWABLES[i]);
    // view.mNameTV.setText(FakeData.FINDS[i]);
    // mPinnedHeaderListView.addHeaderView(view);
    // }
    // }

    private Appmsgsrv8094 mIMInterfaceProxy;

    @AfterInject
    void afterInject() {
        mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
        mIMInterfaceProxy.setRestErrorHandler(this);
    }


    @Background
    void getMemberDoInBackground(String userName) {
        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());
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
            Toast.makeText(getActivity(), response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(response.Member.StarFriend == 0){
            addOrRemoveContactDoInBackground(response.Member);
        }else if(response.Member.StarFriend == 1){
            Toast.makeText(getActivity(),"已经是联系人",Toast.LENGTH_SHORT).show();
        }
    }

    @Background
    void addOrRemoveContactDoInBackground(Member member) {
        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());
        AddOrRemoveContactRequest request = new AddOrRemoveContactRequest();
        request.BaseRequest = baseRequest;
        request.Uid = member.Uid;
        request.StarFriend = 1;
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
            Toast.makeText(getActivity(), response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), "添加成功",
                Toast.LENGTH_SHORT).show();

    }

    private ListView.OnItemClickListener mOnItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (position) {
                case 0:
                    callCapture();


                    break;
                case 1:
                    AppsActivity_.intent(getActivity()).localUserName(localUserName).start();
                    break;
                case 2:
                    checkUpdateDoInBackground();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onTagChanged() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (null != data && requestCode == CAPTURE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    doCaptureResult(data);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 调二维码扫描
     */
    private void callCapture() {
        Intent intent = new Intent("com.yxst.epic.unifyplatform.CaptureActivity");
        startActivityForResult(intent, CAPTURE_REQUEST_CODE);
    }

    /**
     * 对扫描结果进行处理
     *
     * @param data Intent
     */
    private void doCaptureResult(Intent data) {
        String captureText = data.getStringExtra("CaptureActivity");
        if (captureText == null || captureText.length() == 0) {
            Utils.showToastShort(getActivity(), "无效的二维码");
            return;
        }
//		if (captureText.startsWith(UserInfoActivity.UserCardUrl)) {
        if (captureText.contains("/app/user/erweima")) {
            String userName = captureText.substring(captureText
                    .lastIndexOf("=") + 1);
            ContactDetailActivity_.intent(getActivity()).userName(userName)
                    .start();
            return;
        }

        try {
            JSONObject o = new JSONObject(captureText);
            String username = o.getString("username");
            String action = null;
            action = o.getString("action");
            if (action.equals("add_friend")) {
                getMemberDoInBackground(username);
            }

        } catch (JSONException e) {
            Uri uri = Uri.parse(captureText);
            Log.d(TAG, "uri.getHost():" + uri.getHost());
            Log.d(TAG, "uri.getScheme():" + uri.getScheme());

            String scheme = uri.getScheme();

            if (scheme == null) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, captureText);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        }

//			data.setClass(this.getActivity(), CaptureResultActivity.class);
//			startActivity(data);


    }

    // private PinnedHeaderListView.OnItemClickListener mOnItemClickListener =
    // new PinnedHeaderListView.OnItemClickListener() {
    //
    // @Override
    // public void onItemClick(AdapterView<?> adapterView, View view,
    // int section, int position, long id) {
    // Log.d(TAG, "onItemClick() section:" + section);
    // Log.d(TAG, "onItemClick() position:" + position);
    // AppDetailActivity.actionAppDetail(getActivity());
    // }
    //
    // @Override
    // public void onSectionClick(AdapterView<?> adapterView, View view,
    // int section, long id) {
    // Log.d(TAG, "onSectionClick() section:" + section);
    // }
    //
    // @Override
    // public void onHeaderViewClick(AdapterView<?> adapterView, View view,
    // int position) {
    // Log.d(TAG, "onHeaderViewClick() position:" + position);
    // }
    //
    // @Override
    // public void onFooterViewClick(AdapterView<?> adapterView, View view,
    // int position) {
    // Log.d(TAG, "onFooterViewClick() position:" + position);
    // }
    //
    // };

    // @Override
    // public Animation onCreateAnimation(int transit, boolean enter, int
    // nextAnim) {
    // Log.d(TAG, "onCreateAnimation() transit:" + transit);
    // Log.d(TAG, "onCreateAnimation() transit:" + enter);
    // Log.d(TAG, "onCreateAnimation() transit:" + nextAnim);
    // return super.onCreateAnimation(transit, enter, nextAnim);
    // }

    // ==========

    @Background
    void checkUpdateDoInBackground() {
        checkUpdateOnPreExecute();

        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());
        CheckUpdateRequest request = new CheckUpdateRequest();
        request.BaseRequest = baseRequest;
        request.VersionCode = Utils.getVersionCode(getActivity());
        request.VersionName = Utils.getVersionName(getActivity());

        Log.d(TAG, "checkUpdateDoInBackground() request:" + request);

        Appmsgsrv8094 mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
        mIMInterfaceProxy.setRestErrorHandler(this);

        CheckUpdateResponse response = mIMInterfaceProxy.checkUpdate(request);

        checkUpdateOnPostExecute(response);
    }

    private ProgressDialog mProgressDialog;

    @UiThread
    void checkUpdateOnPreExecute() {
        mProgressDialog = ProgressDialog.show(getActivity(), "提示", "检查更新");
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @UiThread
    void checkUpdateOnPostExecute(CheckUpdateResponse response) {
        Log.d(TAG, "checkUpdateOnPostExecute() response:" + response);

        dismissProgressDialog();

        if (response == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(getActivity(), "检查更新失败",
                    Toast.LENGTH_SHORT).show();
            return;
        }

		/*if (response.isUpdate) {
            UpdateDialogFragment f = UpdateDialogFragment_.builder()
					.msg(response.Msg).build();
//		f.setUpdateDialogListener(mUpdateDialogListener);
			f.show(getFragmentManager(), "update");
			
//		UpdateEnableActivity_.intent(getActivity())
//		.flags(Intent.FLAG_ACTIVITY_NEW_TASK).msg(response.Msg).start();
		} else {
			Utils.showToastShort(getActivity(), "您已经是最新版本！");
		}*/

    }

    @UiThread
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        dismissProgressDialog();
        Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
    }

}
