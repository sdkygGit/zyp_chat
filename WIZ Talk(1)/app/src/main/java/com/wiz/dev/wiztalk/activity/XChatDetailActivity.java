package com.wiz.dev.wiztalk.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.afinal.simplecache.ACache;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.rest.RestErrorHandler;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.packet.DataForm.Type;
import org.springframework.core.NestedRuntimeException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.skysea.group.Group;
import com.skysea.group.GroupService;
import com.skysea.group.MemberInfo;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.XChatDetailAdapter;
import com.wiz.dev.wiztalk.dto.request.SetUserAvatarRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.ResponseUpload;
import com.wiz.dev.wiztalk.dto.response.SetUserAvatarResponse;
import com.wiz.dev.wiztalk.fragment.PersonalCenterFragment;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.ImageUtils;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.HeaderLayout;
import com.wiz.dev.wiztalk.view.WrapContentHeightGridView;

@EActivity(R.layout.activity_chat_detail)
public class XChatDetailActivity extends ActionBarActivity implements
        RestErrorHandler, XChatDetailAdapter.OnXDelMemberListenr {

    private static final String TAG = XChatDetailActivity.class.getSimpleName();

    String localUserName;

    @Extra
    Member localMember;

    @Extra
    String remoteUserName;// groupJid 接收方长这样 126@group.admin-pc/xujianxue
    // 发送方126@group.admin-pc

    private String mGroupJid;

    @Extra
    String remoteDisplayName;

    @ViewById(R.id.grid)
    WrapContentHeightGridView mGridView;

    @ViewById
    View trQun;

    @ViewById
    TextView tvQun;

    @ViewById
    Button btnQuitQun;

    @ViewById
    Button btnSenMsg2Group;//进入群聊

    @ViewById
    ImageView iv_icon;

    @Extra
    int fromWhere;

    public final static int FROM_GROUP_LIST_ACTIVITY = 1;//从群列表来的。
    public final static int FROM_XCHAT_ACTIVITY = 2;//从聊天界面来的。

    @Bean
    XChatDetailAdapter mAdapter;

    private String mOwner; // 群里的管理员 不包含Jid的

    GroupService gService;

    private final static int ACTOIN_DESTROY = 1;//删除群操作

    private final static int ACTOIN_KICK = 2;//踢人操作

    private final static int ACTOIN_EXITE = 2;//退出群操作


    protected String localBarJid;

    @DimensionPixelOffsetRes(R.dimen.icon_size_biggest)
    int sizeBiggest;

    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int sizeBigger;

    @DimensionPixelOffsetRes(R.dimen.icon_size_normal)
    int sizeNormal;

    @DimensionPixelOffsetRes(R.dimen.icon_size_small)
    int sizeSmall;


    @AfterInject
    void afterInject() {
        localUserName = MyApplication.getInstance().getLocalUserName();
        localBarJid = MyApplication.getInstance().getOpenfireJid();
        localBarJid = localBarJid.substring(0, localBarJid.lastIndexOf("@"));
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

    @AfterViews
    void afterViews() {
        final ActionBar bar = getSupportActionBar();
//		 bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.photo1));
        bar.hide();
        initTopBarForLeft("群信息");
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setAdapter(mAdapter);
        mAdapter.setLocalUserName(localBarJid);
        mAdapter.setOnDelMemberListenr(this);

        trQun.setVisibility(View.VISIBLE);
        tvQun.setText(remoteDisplayName);
        btnQuitQun.setVisibility(View.VISIBLE);

        mGroupJid = remoteUserName;
        if (remoteUserName.contains("/")) {
            mGroupJid = remoteUserName.substring(0,
                    remoteUserName.lastIndexOf("/"));
        }
        getQunMembersDoInBackground(true);

        ImageLoader.getInstance().displayImage(Utils.getAvataUrl(remoteDisplayName.split("@")[0].concat("@group"), 108), iv_icon,
                ImagerLoaderOptHelper.getGroupAvatarOpt());
        MyApplication.getInstance().addActivity(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
        L.d(TAG, "getQunMembersOnPostExecute() response:" + group);

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

            Form actualForm = new Form(group.getInfo());
            // 不包含Jid的
            mOwner = actualForm.getField("owner").getValues().get(0);
            mAdapter.setShowAdd(true);
            mAdapter.setShowMinus(MyApplication.getInstance().getOpenfireJid().contains(mOwner));

            changeData(members);

            remoteDisplayName = actualForm.getField("name").getValues().get(0);
            tvQun.setText(remoteDisplayName);

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
            // TODO paas 和openfire用户数据的不对对应 ,现在做不了！
//			member.UserName 和 jid(邮箱前半段)对应
            String username = MyApplication.getInstance().getLocalUserName();
            Member member = MyApplication.getInstance().getLocalMember();
            List<Member> members = new ArrayList<Member>();
            members.add(member);
            if (!mAdapter.isPositionAdd(position)
                    && !mAdapter.isPositionMinus(position)) {
//				MemberInfo memberInfo = (MemberInfo) parent.getItemAtPosition(position);
//				ContactDetailActivity_.intent(XChatDetailActivity.this)
//						.userName(username).member(member).start();
                MemberInfo memberInfo = (MemberInfo) parent.getItemAtPosition(position);
                String bareUsername = memberInfo.getUserName();

                bareUsername = bareUsername.concat("@user");
                ContactDetailActivity_.intent(XChatDetailActivity.this)
                        .userName(bareUsername).member(null).fromWhere(ContactDetailActivity.FROM_WHERE_IS_GROUP_CHAT_DETAIL)
                        .start();

            } else if (mAdapter.isPositionAdd(position)) {
                // TODO: 2016/3/2
                /*ContactSelectActivity_.intent(XChatDetailActivity.this)
                        .isPickMode(true).lockMembers(members)
						.startForResult(REQUESTCODE_PICK);*/
                ComponentName component = new ComponentName(
                        XChatDetailActivity.this, ContactSelectActivity_.class);
                Intent intent = new Intent();
                //设置Intent的Component属性
                intent.setComponent(component);
                ObjectMapper mapper = new ObjectMapper();
                //设置参数
                try {
                    intent.putExtra("lockMembers", mapper.writeValueAsString(members));
                    intent.putExtra("localUserName", username);
                    intent.putExtra("isPickMode", true);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //启动SecondActivity
                startActivityForResult(intent, REQUESTCODE_PICK);
            } else if (mAdapter.isPositionMinus(position)) {
                mAdapter.setDelMode(!mAdapter.isDelMode());
            }

        }
    };

    public static final int REQUESTCODE_PICK = 11;


    @Background
    void addQunMemberDoInBackground(List<Member> members) {
        onPreExecute();
        try {
            Group group = gService.getGroup(mGroupJid);
//         TODO 用到了邮箱
            for (Member member : members) {
                String userName = member.Uid;
                group.inviteToJoin(userName, member.NickName);
                L.d(TAG, "inivte :" + userName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            addQunMemberOnPostExecute(false);
        }

        addQunMemberOnPostExecute(true);
    }

    @UiThread
    void addQunMemberOnPostExecute(boolean result) {
        dismissProgressDialog();
        Toast.makeText(this, result ? "操作成功" : "操作失败", Toast.LENGTH_SHORT)
                .show();
    }

    @Click(R.id.trQun)
    void onClickTrQun(View v) {

        if (mOwner.equals(localBarJid)) {
            final Dialog dialog = new Dialog(XChatDetailActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
            View view = LayoutInflater.from(XChatDetailActivity.this).inflate(R.layout.input_qun_name_lay, null);
            final EditText et = (EditText) view.findViewById(R.id.name_et);
            et.setText(remoteDisplayName);
            Button cancleBtn = (Button) view.findViewById(R.id.cancleBtn);
            cancleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            Button okBtn = (Button) view.findViewById(R.id.okBtn);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTopic = et.getText().toString();
                    if (!TextUtils.isEmpty(newTopic) && newTopic.trim().length() != 0 && !newTopic.equals(remoteDisplayName))
                        updateQunTopicDoInBackground(newTopic);
                }
            });

            dialog.getWindow().setContentView(view);
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) XChatDetailActivity.this.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            dialog.getWindow().setLayout(width * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        } else {
            Toast.makeText(XChatDetailActivity.this, "请联系群主进行该项操作！", Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.btnQuitQun)
    void onClickBtnQuitQun() {
        if (!mOwner.equals(localBarJid)) {
            doInBackgroundQuitQun();
        } else {
            doInBackgroundDestroyQun();
        }
    }

    @Click(R.id.btnSenMsg2Group)
    void onClickbtnSenMsg2Group() {
        if (fromWhere == FROM_GROUP_LIST_ACTIVITY) {
            XchatActivity_.intent(this).remoteUserName(mGroupJid)
                    .remoteUserNick(remoteDisplayName)
                    .chatType(MsgInFo.TYPE_GROUPCHAT).start();
        }
        finish();
    }

    //管理员删除群
    @Background
    void doInBackgroundDestroyQun() {
        onPreExecute();
        try {
            Group group = gService.getGroup(mGroupJid);
            group.destroy("管理员解散群");
            onPostExecuteQuitQun(true, XChatDetailActivity.ACTOIN_DESTROY);
        } catch (Exception e) {
            e.printStackTrace();
            onPostExecuteQuitQun(false, XChatDetailActivity.ACTOIN_DESTROY);
        }
    }

    public void sendB(String content, String remoteUserName) {
        if (!TextUtils.isEmpty(content)) {
            String localUserJid = MyApplication.getInstance().getOpenfireJid();
            String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
            XmppMessage msg = new XmppMessage();
            //设置为聊天类型
            msg.setType(MsgInFo.TYPE_GROUP_OPERATE);
            msg.setTo_(remoteUserName);
            msg.setFrom_(mGroupJid);
            msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
            msg.setExtLocalUserName(localUserJid);


            msg.setExtRemoteUserName(remoteUserName);
            msg.setExtRemoteDisplayName(remoteDisplayName);

            msg.setExtLocalDisplayName(remoteDisplayName);
            msg.setExtGroupOperateUserNick(LocalUserNickName);
            msg.setBody(content);
            msg.setMold(MsgInFo.MOLD_TEXT);
            msg.setDirect(MsgInFo.INOUT_OUT);
            msg.setStatus(MsgInFo.STATUS_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            msg.setCreateTime(System.currentTimeMillis());
            MsgService.getMsgWriter(XChatDetailActivity.this).sendMsg(msg);
        }
    }

    // TODO 退出群
    @Background
    void doInBackgroundQuitQun() {
        onPreExecute();
        try {
            Group group = gService.getGroup(mGroupJid);
            group.exit(getResources().getString(R.string.group_operate_tips_exit));
            onPostExecuteQuitQun(true, XChatDetailActivity.ACTOIN_EXITE);
        } catch (Exception e) {
            e.printStackTrace();
            onPostExecuteQuitQun(false, XChatDetailActivity.ACTOIN_EXITE);
        }
    }

    @UiThread
    void onPostExecuteQuitQun(boolean result, int actionType) {
        Log.d(TAG, "onPostExecuteQuitQun() ");
        dismissProgressDialog();

        if (!result) {
            Toast.makeText(XChatDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
        }
        // TODO: 2016/3/13 因为自己加入的群数据被缓存了
        ACache.get(this).remove(GroupListActivity.CACHE_KEY);
        XmppDbManager.getInstance(this).deleteChat(localBarJid, remoteUserName);
        setResult(RESULT_OK);
        finish();

    }

    @Background
    void updateQunTopicDoInBackground(String topic) {
        onPreExecute();

        GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
        Group group = gService.getGroup(mGroupJid);

        try {
            Form form = new Form(group.getInfo());

            DataForm updateForm = new DataForm(Type.submit);

            FormField field = new FormField("name");
            updateForm.addField(field);
            field.addValue(topic);

            field = new FormField("subject");
            updateForm.addField(field);
            field.addValue(form.getField("subject").getValues().get(0));

            field = new FormField("description");
            updateForm.addField(field);
            field.addValue(form.getField("description").getValues().get(0));

            field = new FormField("category");
            updateForm.addField(field);
            field.addValue(form.getField("category").getValues().get(0));

            field = new FormField("openness");
            updateForm.addField(field);
            field.addValue(form.getField("openness").getValues().get(0));


            group.updateInfo(updateForm);
            //TODO 更新 groupName
            XmppDbManager.getInstance(this).updateQunTopic(remoteUserName, topic);

        } catch (Exception e) {
            e.printStackTrace();
            updateQunTopicOnPostExecute(false, topic);
        }
        updateQunTopicOnPostExecute(true, topic);

    }

    @UiThread
    void updateQunTopicOnPostExecute(boolean result,
                                     String topic) {
        Log.d(TAG, "updateQunTopicOnPostExecute() response:" + topic);

        dismissProgressDialog();

        if (!result) {
            Toast.makeText(XChatDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            return;
        }

        tvQun.setText(topic);

        remoteDisplayName = topic;

    }

    @Override
    public void onDelMember(MemberInfo member) {
        delMemberDoInBackground(member);
    }

    @Background
    void delMemberDoInBackground(MemberInfo member) {
        onPreExecute();

        try {
            GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            Group group = gService.getGroup(mGroupJid);
            group.kick(member.getUserName(), "群管理员踢人！");
        } catch (Exception e) {
            e.printStackTrace();
            delQunMemberOnPostExecute(false);
        }

        delQunMemberOnPostExecute(true);

    }

    @UiThread
    void delQunMemberOnPostExecute(boolean result) {
        Log.d(TAG, "delQunMemberOnPostExecute() response:" + result);

        dismissProgressDialog();

        if (result) {
            getQunMembersDoInBackground(false);

            Toast.makeText(this, "操作成功",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "操作失败",
                    Toast.LENGTH_SHORT).show();
        }
        // changeData(response.memberList);
    }

    @Click(R.id.iv_icon)
    void setIcon(View v) {

        final Dialog dialog = new Dialog(XChatDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        View view = LayoutInflater.from(XChatDetailActivity.this).inflate(R.layout.two_lay_dialog, null);
        TextView one = (TextView) view.findViewById(R.id.dialog_one);
        one.setText("本地图片");
        TextView two = (TextView) view.findViewById(R.id.dialog_two);
        two.setText("拍照");
        Button cancle = (Button) view.findViewById(R.id.cancleBtn);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);

                dialog.cancel();
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getCameraTempFile());
                startActivityForResult(intent,
                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                dialog.cancel();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.getWindow().setContentView(view);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) XChatDetailActivity.this.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(width * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    Uri cropUri, origUri;


    // 拍照保存的绝对路径
    private Uri getCameraTempFile() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(PersonalCenterFragment.FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            Toast.makeText(XChatDetailActivity.this, "无法保存上传的头像，请检查SD卡是否挂载",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // 照片命名
        String cropFileName = "EPIC_camera_" + timeStamp + ".jpg";
        // 裁剪头像的绝对路径

        cropUri = Uri.fromFile(new File(PersonalCenterFragment.FILE_SAVEPATH + cropFileName));
        this.origUri = this.cropUri;
        return this.cropUri;
    }


    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        int result_ok = -1;
        if (resultCode == result_ok) {
            switch (requestCode) {
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                    startActionCrop(origUri);// 拍照后裁剪
                    break;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                    startActionCrop(data.getData());// 选图后裁剪
                    break;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                    uploadNewPhoto(data);// 上传新照片
                    break;


                case REQUESTCODE_PICK:
                    @SuppressWarnings("unchecked")
                    List<Member> members = (List<Member>) data
                            .getSerializableExtra("members");
                    Log.d(TAG, "members:" + members);
                    addQunMemberDoInBackground(members);
                    break;
            }
        }

    }


    private void startActionCrop(Uri data) {
//		mCropOutPut = this.getUploadTempFile(data);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
//		intent.putExtra("output", mCropOutPut);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", PersonalCenterFragment.CROP);// 输出图片大小
        intent.putExtra("outputY", PersonalCenterFragment.CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        L.d(TAG, "getUploadTempFile() uri:" + uri);
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(PersonalCenterFragment.FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            Toast.makeText(XChatDetailActivity.this, "无法保存上传的头像，请检查SD卡是否挂载",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (thePath == null || "".equals(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(XChatDetailActivity.this,
                    uri);
        }
        int point = thePath.lastIndexOf('.');
        String ext = thePath.substring(point + 1);
        if (ext == null || "".equals(ext)) {
            ext = "jpg";
        }
        // 照片命名
        String cropFileName = "epic_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径

        cropUri = Uri.fromFile(new File(PersonalCenterFragment.FILE_SAVEPATH + cropFileName));
        L.d(TAG, "getUploadTempFile() cropUri:" + cropUri);
        return this.cropUri;
    }

    private void uploadNewPhoto(Intent data) {
        L.i("上传照片", "ok");
        L.d(TAG, "uploadNewPhoto() cropUri:" + cropUri);
//		L.d(TAG, "uploadNewPhoto() data:" + data.getExtras());
        RequestCallBack<String> callback = new RequestCallBack<String>() {
            @Override
            public void onStart() {
                L.d(TAG, "onStart()");
                onPreExecute();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                L.d(TAG, "onLoading() isUploading:" + isUploading);
                if (isUploading) {
//					tvUploadPercent.setText((int) (current * 100 / total) + "%");
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.d(TAG, "onSuccess()");
                ResponseUpload responseUpload = Utils.readValue(
                        responseInfo.result, ResponseUpload.class);
                L.d(TAG, "onSuccess() responseUpload:" + responseUpload);
                if (responseUpload != null && TextUtils.isEmpty(responseUpload.error)) {
                    setAvatarInBackground(cropUri.getPath(), responseUpload);
                } else {
                    mProgressDialog.dismiss();
                    Utils.showToastShort(XChatDetailActivity.this, "上传头像失败！");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                L.d(TAG, "onFailure() error:" + error);
                L.d(TAG, "onFailure() msg:" + msg);
                mProgressDialog.dismiss();
                Utils.showToastShort(XChatDetailActivity.this, "上传头像失败！");
            }
        };
//		String url = Utils.URL_FILE_AVATA;
        String url = Utils.getURLFileServer(XChatDetailActivity.this, Utils.URL_FILE_AVATA);
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", new File(cropUri.getPath()));
        HttpHandler<String> handler = http.send(HttpRequest.HttpMethod.POST, url, params, callback);
    }

    Appmsgsrv8094 myRestClient;

    @UiThread
    void onPreSetAvatar() {
        myRestClient = Appmsgsrv8094Proxy.create(5 * 1000);
        myRestClient.setRestErrorHandler(this);
        mProgressDialog = ProgressDialog.show(this, "提示", "上传照片...");
    }

    @Background
    void setAvatarInBackground(String fileUrl, ResponseUpload responseUpload) {
        onPreSetAvatar();
        SetUserAvatarRequest request = new SetUserAvatarRequest();
        request.BaseRequest = CacheUtils.getBaseRequest(XChatDetailActivity.this);
        request.fileExtention = MimeTypeMap
                .getFileExtensionFromUrl(fileUrl);
        request.responseUpload = responseUpload;

        request.userName = remoteUserName.split("@")[0].concat("@group");
        L.d(TAG, "request:" + request);
        L.d(TAG, "rootUrl:" + myRestClient.getRootUrl());
        SetUserAvatarResponse response = myRestClient.setUserAvatar(request);
        L.d(TAG, "response:" + response);
        onPostSetAvatarExecute(response);
    }

    @UiThread
    void onPostSetAvatarExecute(SetUserAvatarResponse response) {
        mProgressDialog.dismiss();
        if (response == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Utils.showToastShort(XChatDetailActivity.this, "上传头像失败！");
            return;
        }

        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(remoteUserName.split("@")[0].concat("@group"), sizeSmall), new
                ImageSize(sizeSmall, sizeSmall));
        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(remoteUserName.split("@")[0].concat("@group"), sizeNormal), new
                ImageSize(sizeNormal, sizeNormal));
        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(remoteUserName.split("@")[0].concat("@group"), sizeBigger), new
                ImageSize(sizeBigger, sizeBigger));

        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(remoteUserName.split("@")[0].concat("@group"), sizeBiggest), new
                ImageSize(sizeBiggest, sizeBiggest));
        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(remoteUserName.split("@")[0].concat("@group"), sizeSmall), new
                ImageSize(108, 108));

        DisplayImageOptions options = ImagerLoaderOptHelper.getConerOpt();
        if (cropUri != null) {
            ImageLoader.getInstance().displayImage(cropUri.toString(), iv_icon, options);
        }

        Utils.showToastShort(XChatDetailActivity.this, "上传头像成功！");
    }
}
