package com.wiz.dev.wiztalk.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skysea.group.Group;
import com.skysea.group.GroupService;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.ContactSelectAdapter;
import com.wiz.dev.wiztalk.fragment.ContactListFragment;
import com.wiz.dev.wiztalk.fragment.ContactListFragment_;
import com.wiz.dev.wiztalk.fragment.ContactSubListFragment;
import com.wiz.dev.wiztalk.fragment.ContactSubListFragment_;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;
import com.wiz.dev.wiztalk.listener.OnMemberClickListener;
import com.wiz.dev.wiztalk.listener.OnMemberDeleteListener;
import com.wiz.dev.wiztalk.listener.OnMembersCheckedChangedListener;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8093;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.view.ContactItemView;
import com.wiz.dev.wiztalk.view.HeaderLayout;
import com.wiz.dev.wiztalk.view.horizontallistview.HorizontalListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.packet.DataForm.Type;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@EActivity(R.layout.activity_contact_select)
public class ContactSelectActivity extends ActionBarActivity implements
        OnMemberClickListener, OnMemberCheckedChangedListener,
        OnMembersCheckedChangedListener, OnMemberDeleteListener {

    private static final String TAG = "ContactSelectActivity";


    @ViewById(R.id.listContactSelect)
    HorizontalListView mHorizontalListView;


    // @FragmentById(R.id.fragment_contact_select_top)
    // ContactSelectTopFragment mContactSelectTopFragment;

    @ViewById(R.id.layout_content)
    FrameLayout mContentLayout;

    @Bean
    ContactSelectAdapter mContactSelectAdapter;

    @SystemService
    LayoutInflater mLayoutInflater;

    Button mCustomView;

    Appmsgsrv8094 mRestClient;
    Appmsgsrv8093 mAppmsgsrv8093;

    // @Bean
    // MyErrorHandler myErrorHandler;

    // @InstanceState
    // int mStackLevel = 1;

    //	@Extra
    boolean isPickMode;
    //TODO  这个member对象不能被 gson序列化
//	@Extra
    List<Member> lockMembers;

    //	@Extra
    String localUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isPickMode = intent.getBooleanExtra("isPickMode", false);
        localUserName = intent.getStringExtra("localUserName");
        String localMembersStr = intent.getStringExtra("lockMembers");

        if (!TextUtils.isEmpty(localMembersStr)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                lockMembers = mapper.readValue(localMembersStr, new TypeReference<List<Member>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MyApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    HeaderLayout mHeaderLayout;

    public void initTopBarForBoth2(String titleName, int leftDrawableId, int rightDrawableId,
                                   HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_BOTH_IMAGE);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                leftDrawableId,
                leftDrawableId < 0 ? null : new HeaderLayout.onLeftImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        if (!popBackStack()) {
                            finish();
                        }
                    }
                });
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
    }

    @AfterViews
    void afterViews() {
        // TODO: 2016/3/3
//		initActionBar();
        final ActionBar bar = getSupportActionBar();
        bar.hide();

        initTopBarForBoth2("选择联系人",
                R.drawable.blueback,
                R.drawable.ok_selector,
                new HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        if (isPickMode) {
                            Intent data = new Intent();
                            data.putExtra("members",
                                    (Serializable) mContactSelectAdapter.getMembers());
                            setResult(RESULT_OK, data);
                            finish();
                        } else if (mContactSelectAdapter.getMembers().size() > 0) {

                            final Dialog dialog = new Dialog(ContactSelectActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.show();

                            View view = LayoutInflater.from(ContactSelectActivity.this).inflate(R.layout.input_qun_name_lay, null);
                            final EditText et = (EditText) view.findViewById(R.id.name_et);
                            Button cancleBtn = (Button) view.findViewById(R.id.cancleBtn);
                            cancleBtn.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });
                            Button okBtn = (Button) view.findViewById(R.id.okBtn);
                            okBtn.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newTopic = et.getText().toString();
                                    doInBackground(mContactSelectAdapter.getMembers(), newTopic);
                                    dialog.dismiss();
                                }
                            });

                            dialog.getWindow().setContentView(view);
                            DisplayMetrics dm = new DisplayMetrics();
                            WindowManager wm = (WindowManager) ContactSelectActivity.this.getSystemService(Context.WINDOW_SERVICE);
                            wm.getDefaultDisplay().getMetrics(dm);
                            int width = dm.widthPixels;
                            dialog.getWindow().setLayout(width * 4 / 5, LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        }
                    }
                });

        mContactSelectAdapter.setOnMemberDeleteListener(this);
        mHorizontalListView.setAdapter(mContactSelectAdapter);

        Log.d(TAG, "afterViews()");
        ContactListFragment fragment = ContactListFragment_.builder()
                .localUserName(localUserName).isSelectMode(true).build();

        fragment.setSelectMembers(mContactSelectAdapter.getMembers());
        fragment.setLockMembers(lockMembers);
        fragment.setOnMemberClickListener(this);
        fragment.setOnMemberCheckedChangedListener(this);
        fragment.setOnMembersCheckedChangedListener(this);
        addFragment(fragment);

    }

    private void addFragment(Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.layout_content, newFragment).commit();
    }

    private void addFragmentToStack(Fragment newFragment) {
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


    @Override
    public void onMemberClick(Member member, View view) {
        if (member.isTypeOrg() || member.isTypeTenant()) {
            ContactSubListFragment fragment = ContactSubListFragment_.builder()
                    .isSelectMode(true).member(member).build();
            fragment.setSelectMembers(mContactSelectAdapter.getMembers());
            fragment.setLockMembers(lockMembers);
            fragment.setOnMemberClickListener(this);
            fragment.setOnMemberCheckedChangedListener(this);
            fragment.setOnMembersCheckedChangedListener(this);
            addFragmentToStack(fragment);
        } else if (member.isTypeUser()) {
            ContactItemView itemView = (ContactItemView) view;
            itemView.mCheckBox.setChecked(!itemView.mCheckBox.isChecked());
        }
    }

    @Override
    public void onMemberCheckedChanged(Member member, boolean isChecked) {
        if (isChecked) {
            mContactSelectAdapter.addMember(member);
        } else {
            mContactSelectAdapter.removeMember(member);
        }
//		updateBtnSubmit();
        updateFragment();
    }

    @Override
    public void onMembersCheckedChanged(List<Member> members, boolean isChecked) {
        if (isChecked) {
            mContactSelectAdapter.addMembers(members);
        } else {
            mContactSelectAdapter.removeMembers(members);
        }

//		updateBtnSubmit();
        updateFragment();
    }

    @Override
    public void onMemberDelete(Member member) {
        mContactSelectAdapter.removeMember(member);

//		updateBtnSubmit();
        updateFragment();
    }

    private void updateFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.layout_content);

        if (f instanceof ContactListFragment) {
            ((ContactListFragment) f).notifyDataSetChanged();
        } else if (f instanceof ContactSubListFragment) {
            ((ContactSubListFragment) f).notifyDataSetChanged();
        }
    }


    private DataForm getCreateForm(String topic) {
        // 和4.0.1版本中有点区别
        final DataForm form = new DataForm(Type.submit);
        FormField field = new FormField("name");
        field.addValue(TextUtils.isEmpty(topic) ? "我的群" : topic);
        form.addField(field);

        field = new FormField("subject");
        field.addValue("subject");
        form.addField(field);

        field = new FormField("description");
        field.addValue("description");
        form.addField(field);

        field = new FormField("category");
        field.addValue("1");
        form.addField(field);

        field = new FormField("openness");

        // 和4.0.1版本中有点区别
        field.setType(FormField.Type.text_single);
        field.addValue("AFFIRM_REQUIRED"); // PUBLIC //AFFIRM_REQUIRED //PRIVATE
        form.addField(field);
        return form;
    }

    @Background
    void doInBackground(List<Member> list, String topic) {
        onPreExecute();

        try {
            GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            String groupJid;
            String groupNickname = null;
            //TODO
            Group group = gService.create(getCreateForm(topic));
            groupNickname = topic;
            groupJid = group.getJid();
            String localUserNickname = MyApplication.getInstance().getLocalMember().NickName;
            group.changeNickname(localUserNickname);
            // 创建群成功的消息。
            XmppMessage msg = new XmppMessage();
            msg.setTo_(groupJid);
            msg.setFrom_(MyApplication.getInstance().getOpenfireJid());
            msg.setType(MsgInFo.TYPE_GROUPCHAT);

            msg.setBody("群 ".concat(groupNickname).concat(" 成功创建！"));
            msg.setMold(MsgInFo.MOLD_TIPS);
            msg.setType(MsgInFo.TYPE_GROUPCHAT);
            msg.setDirect(MsgInFo.INOUT_IN);
            msg.setStatus(MsgInFo.STATUS_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            msg.setCreateTime(System.currentTimeMillis());

            msg.setExtLocalDisplayName(MyApplication.getInstance().getLocalMember().NickName);
            msg.setExtLocalUserName(MyApplication.getInstance().getOpenfireJid());

            msg.setExtRemoteUserName(groupJid);
            msg.setExtRemoteDisplayName(groupNickname);
            msg.setShowTime(System.currentTimeMillis());

            XmppDbManager.getInstance(this).insertMessageWithNotify(
                    msg);

            for (Member member : list) {
                if (TextUtils.isEmpty(member.Uid)) {
                    continue;
                }
                String userName = member.Uid;
                group.inviteToJoin(userName, member.NickName);
                L.d(TAG, "inivte :" + userName);
            }

            onPostExecute(groupJid, groupNickname);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO 处理错误
            onPostExecute(null, null);
        }

    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private ProgressDialog mProgressDialog;

    @UiThread
    void onPreExecute() {
        mProgressDialog = ProgressDialog.show(this, "提示", "正在创建群...");
    }

    @UiThread
    void onPostExecute(String groupJid, String groupNickname) {
        L.d(TAG, "onPostExecute() response:" + groupJid);
        closeProgressDialog();
        if (groupJid == null || groupNickname == null) {
            Toast.makeText(
                    ContactSelectActivity.this,
                    getResources().getString(
                            R.string.toast_tips_operation_failed),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        XchatActivity_.intent(this).remoteUserName(groupJid)
                .remoteUserNick(groupNickname)
                .chatType(MsgInFo.TYPE_GROUPCHAT).start();

        finish();
    }

}
