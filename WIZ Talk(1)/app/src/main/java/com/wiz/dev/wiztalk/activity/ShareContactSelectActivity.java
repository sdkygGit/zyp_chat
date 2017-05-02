package com.wiz.dev.wiztalk.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.ShareContactSelectAdapter;
import com.wiz.dev.wiztalk.fragment.ContactListFragment;
import com.wiz.dev.wiztalk.fragment.ContactListFragment_;
import com.wiz.dev.wiztalk.fragment.ContactSubListFragment;
import com.wiz.dev.wiztalk.fragment.ContactSubListFragment_;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;
import com.wiz.dev.wiztalk.listener.OnMemberClickListener;
import com.wiz.dev.wiztalk.listener.OnMemberDeleteListener;
import com.wiz.dev.wiztalk.listener.OnMembersCheckedChangedListener;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.ContactItemView;
import com.wiz.dev.wiztalk.view.HeaderLayout;
import com.wiz.dev.wiztalk.view.horizontallistview.HorizontalListView;

import org.json.JSONObject;

import java.util.List;

public class ShareContactSelectActivity extends ActionBarActivity implements
        OnMemberClickListener, OnMemberCheckedChangedListener,
        OnMembersCheckedChangedListener, OnMemberDeleteListener {

    private static final String TAG = "ContactSelectActivity";


    HorizontalListView mHorizontalListView;

//    @ViewById
//    TextView tvTip;

    FrameLayout mContentLayout;

    ShareContactSelectAdapter mContactSelectAdapter;


    Button mCustomView;


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
        setContentView(R.layout.activity_contact_select);
        afterViews();
        MyApplication.getInstance().addActivity(this);

        if(getIntent() == null){
            Toast.makeText(this,"数据为空，不能分享",Toast.LENGTH_SHORT).show();
            finish();
        }
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


    void afterViews() {

        localUserName = MyApplication.getInstance().getLocalUserName();
        mHorizontalListView = (HorizontalListView) findViewById(R.id.listContactSelect);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);

        mContactSelectAdapter = new ShareContactSelectAdapter(this);

        getActionBar().hide();
        initTopBarForBoth2("发送到",
                R.drawable.blueback,
                R.drawable.ok_selector,
                new HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        final List<Member> members = mContactSelectAdapter.getMembers();
                        if (members.size() > 0) {
                            final Dialog dialog = new Dialog(ShareContactSelectActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.show();
                            View view = LayoutInflater.from(ShareContactSelectActivity.this).inflate(R.layout.share_app_dialog, null);
                            ImageView iv_recent_avatar = (ImageView) view.findViewById(R.id.iv_recent_avatar);
                            Button cancleBtn = (Button) view.findViewById(R.id.cancleBtn);
                            TextView name = (TextView) view.findViewById(R.id.name);
                            TextView desc = (TextView) view.findViewById(R.id.desc);
                            Intent intent = getIntent();

                            if (intent.getStringExtra("share_type").equals("app")) {
                                Member member = (Member) intent.getSerializableExtra("share_member");
                                name.setText(member.NickName);
                                desc.setText(member.Description);
                                DisplayImageOptions options = ImagerLoaderOptHelper.getAppAvatarOpt();
                                ImageLoader.getInstance().displayImage(Utils.getImgUrl(member.Avatar,
                                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics())), iv_recent_avatar, options);
                            } else if (intent.getStringExtra("share_type").equals("svg")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.svg);
                            } else if (intent.getStringExtra("share_type").equals("pdf")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.pdf);
                            } else if (intent.getStringExtra("share_type").equals("ppt")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.ppt);
                            } else if (intent.getStringExtra("share_type").equals("pr")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.pr);
                            } else if (intent.getStringExtra("share_type").equals("dwg")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.dwg);
                            } else if (intent.getStringExtra("share_type").equals("xls")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.excle);
                            } else if (intent.getStringExtra("share_type").equals("doc")) {
                                name.setText(intent.getStringExtra("share_title"));
                                desc.setText(intent.getStringExtra("share_desc"));
                                iv_recent_avatar.setImageResource(R.mipmap.word);
                            }

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
                                    doInBackground(members);
                                    dialog.dismiss();
                                }
                            });

                            dialog.getWindow().setContentView(view);
                            DisplayMetrics dm = new DisplayMetrics();
                            WindowManager wm = (WindowManager) ShareContactSelectActivity.this.getSystemService(Context.WINDOW_SERVICE);
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
    public void onMemberClick(Member member,View view) {
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
        updateFragment();
    }

    @Override
    public void onMembersCheckedChanged(List<Member> members, boolean isChecked) {
        if (isChecked) {
            mContactSelectAdapter.addMembers(members);
        } else {
            mContactSelectAdapter.removeMembers(members);
        }
        updateFragment();
    }

    @Override
    public void onMemberDelete(Member member) {
        mContactSelectAdapter.removeMember(member);
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

    void doInBackground(final List<Member> list) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPostExecute(Boolean suc) {
                mContactSelectAdapter.clear();
                closeProgressDialog();
                if (suc)
                    Toast.makeText(ShareContactSelectActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    for (Member member : list) {
                        String jid = member.Uid;
                        jid = jid.toLowerCase();
                        jid = jid.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
                        if (jid.equals(MyApplication.getInstance().getOpenfireJid())) {
                            if (list.size() == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ShareContactSelectActivity.this, "不能分享自己", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                            continue;
                        }
                        Intent intent = getIntent();
                        if (intent.getStringExtra("share_type").equals("app")) {
                            JSONObject o = new JSONObject();
                            Member m = (Member) intent.getSerializableExtra("share_member");
                            o.put("share_type", "app");
                            o.put("share_id", m.UserName);
                            o.put("share_title", m.NickName);
                            o.put("share_desc", m.Description);
                            o.put("share_app_avatar", m.Avatar);
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName, MsgInFo.MOLD_SHARE_APP);
                        } else if (intent.getStringExtra("share_type").equals("svg")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "svg");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        } else if (intent.getStringExtra("share_type").equals("pdf")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "pdf");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        } else if (intent.getStringExtra("share_type").equals("ppt")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "ppt");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        } else if (intent.getStringExtra("share_type").equals("pr")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "pr");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        } else if (intent.getStringExtra("share_type").equals("dwg")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "dwg");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        } else if (intent.getStringExtra("share_type").equals("xls")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "xls");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        } else if (intent.getStringExtra("share_type").equals("doc")) {
                            JSONObject o = new JSONObject();
                            o.put("share_type", "doc");
                            o.put("share_id", intent.getStringExtra("share_id"));
                            o.put("share_title", intent.getStringExtra("share_title"));
                            o.put("share_desc", intent.getStringExtra("share_desc"));
                            shareSend(SaveConfig.SHARE_PWD + "#" + o.toString(), jid, member.NickName,MsgInFo.MOLD_SHARE_DOC);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO 处理错误
                    closeProgressDialog();
                }
                return true;
            }

            @Override
            protected void onPreExecute() {
                mProgressDialog = ProgressDialog.show(ShareContactSelectActivity.this, "提示", "发送...");
            }
        }.execute();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private ProgressDialog mProgressDialog;


    public void shareSend(String content,String remoteUserName, String remoteUserNick,int mold) {
        if (!TextUtils.isEmpty(content)) {
            String localUserJid = MyApplication.getInstance().getOpenfireJid();
            String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
            XmppMessage msg = new XmppMessage();
            //设置为聊天类型
            msg.setType(MsgInFo.TYPE_CHAT);
            msg.setTo_(remoteUserName);
            msg.setFrom_(localUserJid);
            msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
            msg.setExtLocalUserName(localUserJid);
            msg.setExtRemoteUserName(remoteUserName);
            msg.setExtRemoteDisplayName(remoteUserNick);
            msg.setBody(content);
            msg.setMold(mold);
            msg.setDirect(MsgInFo.INOUT_OUT);
            msg.setStatus(MsgInFo.STATUS_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            msg.setCreateTime(System.currentTimeMillis());
            msg.setShowTime(System.currentTimeMillis());
            MsgService.getMsgWriter(ShareContactSelectActivity.this).sendMsg(msg);
        }
    }
}
