package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.skysea.group.Group;
import com.skysea.group.GroupService;
import com.skysea.group.MemberInfo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.QunMemberListAdapter;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.view.HeaderLayout;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.rest.RestErrorHandler;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.springframework.core.NestedRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@EActivity(R.layout.activity_qun_memberlist)
public class QunMemberListActivity extends Activity implements
        RestErrorHandler {

    private String mGroupJid;
    @Extra
    String remoteUserName;

    @Bean
    QunMemberListAdapter mAdapter;

    @ViewById
    ListView mListView;

    GroupService gService;

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


    @AfterViews
    void afterViews() {
//        final ActionBar bar = getSupportActionBar();
////		 bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.photo1));
//        bar.hide();
        initTopBarForLeft("选择回复的人");

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mGroupJid = remoteUserName;
        if (remoteUserName.contains("/")) {
            mGroupJid = remoteUserName.substring(0,
                    remoteUserName.lastIndexOf("/"));
        }
        getQunMembersDoInBackground(true);
        MyApplication.getInstance().addActivity(this);
    }


    @Background(delay = 100, id = "getQunMembersDoInBackground")
    void getQunMembersDoInBackground(boolean isShowProgressBar) {
        if (isShowProgressBar)
            onPreExecute();

        Group group = null;

        try {
            gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            group = gService.getGroup(mGroupJid);
        } catch (Exception e) {
            e.printStackTrace();
            getQunMembersOnPostExecute(null);
        }

        getQunMembersOnPostExecute(group);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundExecutor.cancelAll("getQunMembersDoInBackground", true);
    }

    @UiThread
    void getQunMembersOnPostExecute(Group group) {

        dismissProgressDialog();

        if (group == null) {
            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            List<MemberInfo> members = new ArrayList<MemberInfo>();
            MemberInfo member;
            for (DataForm.Item item : group.getMembers().getItems()) {
                HashMap<String, String> rows = mapFields(item);
                /*
				 * if (rows.get("username").equalsIgnoreCase(user.getUserName())
				 * && rows.get("nickname").equalsIgnoreCase(user.getNickname()))
				 * { }
				 */
                member = new MemberInfo(rows.get("username"),
                        rows.get("nickname"));
                members.add(member);
            }

            if (members.size() == 0) {
                Toast.makeText(this, "当前群组无成员！", Toast.LENGTH_SHORT).show();
                return;
            }

            changeData(members);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "访问失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private static HashMap<String, String> mapFields(DataForm.Item item) {
        HashMap<String, String> maps = new HashMap<String, String>(item
                .getFields().size());
        for (FormField field : item.getFields()) {
            maps.put(field.getVariable(), field.getValues().get(0));
        }
        return maps;
    }

    protected String localBarJid;

    @AfterInject
    void afterInject() {
        localBarJid = MyApplication.getInstance().getOpenfireJid();
        localBarJid = localBarJid.substring(0, localBarJid.lastIndexOf("@"));
    }

    private void changeData(List<MemberInfo> memberList) {
        Collections.sort(memberList, new Comparator<MemberInfo>() {

            @Override
            public int compare(MemberInfo lhs, MemberInfo rhs) {

                if (lhs.getUserName().equals(localBarJid)) {
                    return -1;
                } else if (rhs.getUserName().equals(localBarJid)) {
                    return 1;
                }
                return 0;
            }
        });
        mAdapter.changeData(memberList);
    }

    private ProgressDialog mProgressDialog;

    @UiThread
    void onPreExecute() {
        mProgressDialog = ProgressDialog.show(this, "提示", "正在操作...");
    }

    @Override
    protected void onDestroy() {

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @UiThread
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        dismissProgressDialog();
        Toast.makeText(this, "访问失败", Toast.LENGTH_SHORT).show();
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            MemberInfo memberInfo = (MemberInfo) parent.getItemAtPosition(position);
            String bareUsername = memberInfo.getNickname();


            Intent intent = new Intent();
            intent.putExtra("bareUsername",bareUsername);

            setResult(RESULT_OK,intent);
            finish();
//            bareUsername = bareUsername.concat("@user");
//            ContactDetailActivity_.intent(QunMemberListActivity.this)
//                    .userName(bareUsername).member(null).fromWhere(ContactDetailActivity.FROM_WHERE_IS_GROUP_CHAT_DETAIL)
//                    .start();



        }


    };


}
