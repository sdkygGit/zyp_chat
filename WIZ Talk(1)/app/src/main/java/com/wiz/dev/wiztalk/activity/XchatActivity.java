package com.wiz.dev.wiztalk.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.download.services.DownloadService;
import com.epic.traverse.push.util.L;
import com.lidroid.xutils.HttpUtils;
import com.wiz.dev.wiztalk.DB.DaoSession;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.VoiceDb;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.DB.XmppMessageDao;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.XChatListAdapter;
import com.wiz.dev.wiztalk.apppush.OnOperationClickListener;
import com.wiz.dev.wiztalk.apppush.OperationRest;
import com.wiz.dev.wiztalk.apppush.ServiceRequest;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;
import com.wiz.dev.wiztalk.apppush.view.OperationsMoreDialogFragment;
import com.wiz.dev.wiztalk.apppush.view.OperationsMoreDialogFragment_;
import com.wiz.dev.wiztalk.dto.model.MediaModel;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetAppOperationListRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetAppOperationListResponse;
import com.wiz.dev.wiztalk.dto.response.Response;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.Constant;
import com.wiz.dev.wiztalk.utils.DraftConfig;
import com.wiz.dev.wiztalk.utils.KeyBoardUtils;
import com.wiz.dev.wiztalk.utils.MessageUtils;
import com.wiz.dev.wiztalk.utils.MethodsCompat;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.SmileyParser;
import com.wiz.dev.wiztalk.utils.ThumbnailUtils;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.AudioRecorderButton;
import com.wiz.dev.wiztalk.view.BiaoQingView;
import com.wiz.dev.wiztalk.view.ChatItemReceiveMovieView;
import com.wiz.dev.wiztalk.view.EmoticonsEditText;
import com.wiz.dev.wiztalk.view.MediaManager;
import com.wiz.dev.wiztalk.view.PopMenus;
import com.wiz.dev.wiztalk.view.xlist.XListView;

import org.afinal.simplecache.ACache;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.rest.RestErrorHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.NestedRuntimeException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import map.LocationActivity;
import map.LocationBean;

/**
 * Created by Admin on 2015/8/14.
 */
@EActivity(R.layout.activity_chatx)
public class XchatActivity extends BaseActivity implements
        AudioRecorderButton.AudioFinishRecorderListener,
        XListView.IXListViewListener,
        RestErrorHandler, OnOperationClickListener {

    public static final String TAG = XchatActivity.class.getSimpleName();

//    public VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
//        @Override
//        public void onPlayerItemChanged(com.volokh.danylo.video_player_manager.meta.MetaData metaData) {
//        }
//    });


    public SaveConfig saveConfig;
    public HttpUtils http = new HttpUtils();

    @ViewById
    XListView mListView;

    public long msgTime;
    @ViewById
    Button btn_chat_add;// +号 布局切换按钮

    @ViewById
    Button btn_chat_emo;// 表情 按钮
    @ViewById
    EmoticonsEditText edit_user_comment; // 支持emotion表情的输入框
    @ViewById
    AudioRecorderButton btn_speak;// 发送语音按钮，以后需要自定这个
    @ViewById
    Button btn_chat_voice;// 切换 发送语音按钮
    @ViewById
    Button btn_chat_keyboard;
    @ViewById
    Button btn_chat_send;
    @ViewById
    LinearLayout layout_more;
    @ViewById
    LinearLayout layout_emo; // 包含在more局中
    @ViewById
    LinearLayout layout_add; // 包含在more局中

    @ViewById
    BiaoQingView viewBiaoQing;

    public static final int time_l = 60 * 1000;

    @ViewById
    TextView tv_picture;
    @ViewById
    TextView tv_camera;
    @ViewById
    TextView tv_location;

    @Extra
    String remoteUserName;

    @Extra
    String remoteUserNick;

    String localUserJid;

    @Extra
    String chatType;// 聊天类型

    DaoSession mDaoSession;
    XmppMessageDao messageDao;

    @ViewById
    LinearLayout chat_normal_layout;

    @ViewById
    ImageView imageView;

    @ViewById
    LinearLayout chat_appmenu_layout;

    PopMenus popupWindow_custommenu;

    @RestService
    OperationRest operationRest;

    @RestService
    Appmsgsrv8094 myRestClient;

    private String localCameraPath;// 照片地址

    String barTitle; //状态栏的标题，

    public XChatListAdapter mAdapter;


    public static boolean isTypeApp = false;

    @ViewById
    TextView title;

    @ViewById
    ImageView right_btn;

    @ViewById
    View tips_send_img_lay;

    boolean isTip = false;

    //每次刷新出消息条数
    int limit = 5;

    //显示的消息条数
    int msgCount;

    //是否是删除消息操作
    boolean isDelMsgAction;

    AsyncQueryHandler mAsyncQueryHandler;

    DraftConfig draftConfig;

    //显示最旧消息的创建时间
    long limitTime;

    @Click(R.id.right_btn)
    void onRightClick(View view) {
        if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
            XChatDetailActivity_
                    .intent(XchatActivity.this)
                    .localMember(
                            MyApplication.getInstance().getLocalMember())
                    .remoteUserName(remoteUserName)
                    .remoteDisplayName(remoteUserNick)
                    .fromWhere(XChatDetailActivity.FROM_XCHAT_ACTIVITY)
                    .startForResult(REQUEST_CODE_CHAT_DETAIL);

        } else {
            //signal chat or group tipsss
            if (!Member.isTypeApp(remoteUserName)) {
                ContactDetailActivity_
                        .intent(XchatActivity.this)
                        .userName(remoteUserName.split("@")[0].concat("@user"))
                        .fromWhere(ContactDetailActivity.FROM_WHERE_IS_SIGAL_CHAT)
                        .start();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        draftConfig = new DraftConfig(this);
    }

    @AfterInject
    public void afterInject() {
        mDaoSession = MyApplication.getDaoSession(XchatActivity.this);
        messageDao = this.mDaoSession.getXmppMessageDao();

        myRestClient = Appmsgsrv8094Proxy.create(10 * 1000);
        myRestClient.setRestErrorHandler(this);

        operationRest.setRestErrorHandler(this);
        msgTime = 0;
    }

    private static final int REQUEST_CODE_CHAT_DETAIL = 1;
    private static final int REQUEST_CODE_SEND_FILE = 0x8451;
    private static final int GET_LOCALTION = 0x110;

    @AfterViews
    public void afterViews() {
        L.d(TAG, " afterViews()");
        L.d(TAG, "remote username:" + remoteUserName);
        L.d(TAG, "remote nickname:" + remoteUserNick);
        localUserJid = MyApplication.getInstance().getOpenfireJid();
        L.d(TAG, "localUserJid:" + localUserJid);

        barTitle = remoteUserNick;
        edit_user_comment.setMaxHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
        if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
            right_btn.setBackgroundResource(R.mipmap.tab_txl_select);
            title.setText(barTitle);
            edit_user_comment.addTextChangedListener(new TextWatcher() {
                int startCount;
                int afterCount;

                int getCount(String str) {
                    int count = 0;
                    for (int i = 0; i < str.length(); i++) {
                        if (str.charAt(i) == '@') {
                            count++;
                        }
                    }
                    return count;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (isManAction)
                        startCount = getCount(s.toString());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isManAction) {
                        afterCount = getCount(s.toString());
                        if (afterCount > startCount) {
                            QunMemberListActivity_
                                    .intent(XchatActivity.this)
                                    .remoteUserName(remoteUserName)
                                    .startForResult(0x1201);
                        }
                    }
                }
            });

        } else {
            //signal chat or group tipsss
            if (Member.isTypeApp(remoteUserName)) {
                isTypeApp = true;
                right_btn.setVisibility(View.INVISIBLE);
                title.setText(barTitle);
            } else {
                //右边按钮进入详情页面
                title.setText(barTitle);
                right_btn.setBackgroundResource(R.drawable.contact_detail_selector);
            }
        }

        // 设置录音结束回调
        btn_speak.setAudioFinishRecorderListener(this);
        intiXListView();

        mAsyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            protected void onQueryComplete(int token, Object cookie, android.database.Cursor cursor) {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        if (limitTime == mAdapter.readEntity(cursor).getCreateTime()) {
                            mAdapter.changeCursor(cursor);
                            if (msgCount >= cursor.getCount()) {
                                isDelMsgAction = true;
                            }
                            msgCount = cursor.getCount();
                            mListView.stopRefresh();
                        } else if (msgCount > cursor.getCount()) {
                            mAdapter.changeCursor(cursor);
                            isDelMsgAction = true;
                            msgCount = cursor.getCount();
                        } else if (cursor.moveToLast()) {

                            if (limitTime != mAdapter.readEntity(cursor).getCreateTime()) {
                                limitTime = mAdapter.readEntity(cursor).getCreateTime();
                                mAsyncQueryHandler.startQuery(0, null,
                                        XmppMessageContentProvider.CONTENT_URI, null,
                                        XmppMessageDao.Properties.ExtLocalUserName.columnName
                                                + " like ? AND "
                                                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                                                + " like ? AND "
                                                + XmppMessageDao.Properties.CreateTime.columnName
                                                + " > ? ", new String[]{localUserJid.concat("%"),
                                                remoteUserName.concat("%"), (limitTime - 1) + ""}, XmppMessageDao.Properties.CreateTime.columnName + " ASC ");

                            } else {
                                mListView.stopRefresh();
                            }
                        }
                    } else {
                        mListView.stopRefresh();
                    }
                }

            }
        };

        XmppMessageContentProvider.daoSession = mDaoSession;
        mAsyncQueryHandler.startQuery(0, null,
                XmppMessageContentProvider.CONTENT_URI, null,
                XmppMessageDao.Properties.ExtLocalUserName.columnName
                        + " like ? AND "
                        + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                        + " like ? ", new String[]{localUserJid.concat("%"),
                        remoteUserName.concat("%")}, XmppMessageDao.Properties.CreateTime.columnName + " desc Limit " + limit + " Offset 0");

//        LoaderManager lm = getLoaderManager();
//        Loader<Cursor> cl = lm.getLoader(1);
//        cl.

        mAdapter.setOnOperationClickListener(this);
        initBottomView();

        if (remoteUserName != null && Member.isTypeApp(remoteUserName)) {
            doInBackgroundGetAppOperationList();
        }

        getContentResolver().registerContentObserver(
                XmppMessageContentProvider.CONTENT_URI, true, mContentObserver);

        if (!TextUtils.isEmpty(draftConfig.getStringConfig(remoteUserName + "@" + localUserJid))) {
            edit_user_comment.setText(draftConfig.getStringConfig(remoteUserName + "@" + localUserJid));
        }

        saveConfig = new SaveConfig(this);

    }


    boolean doInBackgroundOperationCancle = false;

    @OnActivityResult(REQUEST_CODE_CHAT_DETAIL)
    void onActivityResultChatDetail(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            finish();
        }
    }

    @OnActivityResult(REQUEST_CODE_SEND_FILE)
    void onActivityResultSendFile(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            MediaModel mediaModel = (MediaModel) data.getSerializableExtra("file");
            if (mediaModel.isVideo) {//视频
                if (mediaModel.size > 7)
                    sendMovieMsg(-1, mediaModel.url);
                else
                    Toast.makeText(XchatActivity.this, "无效视频", Toast.LENGTH_SHORT).show();
            } else {//未知类型文件
                if (mediaModel.isImage) {
                    sendImageMsg(mediaModel.url);
                } else
                    sendFileMsg(mediaModel.size, mediaModel.url);
            }
        }
    }

    /**
     * 发送文件消息
     *
     * @param seconds
     * @param filePath
     */
    @Background
    public void sendFileMsg(int seconds, String filePath) {
        try {
            int voiceLength = seconds;
            XmppMessage msg = new XmppMessage();
            if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
                msg.setType(MsgInFo.TYPE_GROUPCHAT);
                msg.setExtGroupMemberUserName(localUserJid);
                //对应群聊时，发送人的nickname
                msg.setExtGroupMemberUserNick(MyApplication.getInstance().getLocalMember().NickName);
            } else if (Member.isTypeApp(remoteUserName)) {
                msg.setType(MsgInFo.TYPE_HEADLINE);
            } else {
                msg.setType(MsgInFo.TYPE_CHAT);
            }
            msg.setTo_(remoteUserName);
            msg.setFrom_(localUserJid);

            String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
            msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
            msg.setExtLocalUserName(localUserJid);

            msg.setExtRemoteUserName(remoteUserName);
            msg.setExtRemoteDisplayName(remoteUserNick);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("filename", filePath.substring(filePath.lastIndexOf("/") + 1));
            jsonObject.put("filesize", seconds);
            msg.setBody(jsonObject.toString());
            msg.setMold(MsgInFo.MOLD_FILE);
            msg.setDirect(MsgInFo.INOUT_OUT);
            msg.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            msg.setFilePath(filePath);
            msg.setExtInternalFilePath(filePath);
            msg.setVoiceLength(voiceLength);
            long showTime = System.currentTimeMillis();
            msg.setCreateTime(showTime);
            msg.setShowTime(0L);
            if ((showTime - msgTime) > time_l) {
                msg.setShowTime(showTime);
                msgTime = showTime;
            }
            MessageUtils.sendMediaMsg(XchatActivity.this, msg);
        } catch (Exception e) {

        }

    }

    boolean isManAction = true;

    @OnActivityResult(0x1201)
    void onActivityResultQunMember(int resultCode, Intent data) {
        if (RESULT_OK == resultCode && data != null) {
            String name = data.getStringExtra("bareUsername");
//            targetString += edit_user_comment.getText().toString() +  name;
            isManAction = false;
            edit_user_comment.setText(edit_user_comment.getText().toString().substring(0, edit_user_comment.getText().toString().length() - 1));
            insertBitText("@" + name);
            isManAction = true;
        }
    }

    private void insertBitText(String text) {
        Paint p = new Paint();
        // 设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);

        p.setColor(Color.BLACK);
        p.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22,
                getResources().getDisplayMetrics()));
        int length = (int) p.measureText(text);

        // 构建一个bitmap
        Bitmap backgroundBm = Bitmap.createBitmap(length + 20, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45,
                        getResources().getDisplayMetrics()),
                Bitmap.Config.ARGB_8888);
        // new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBm);

        canvas.drawColor(Color.parseColor("#40d4f8"));
        canvas.drawText(text, 10, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                getResources().getDisplayMetrics()), p);
        SpannableString mSpan1 = new SpannableString(text);

        int start = edit_user_comment.getSelectionStart();
        mSpan1.setSpan(new ImageSpan(backgroundBm), mSpan1.length() - text.length(),
                mSpan1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (edit_user_comment != null) {
            Editable et = edit_user_comment.getText();
            et.insert(start, mSpan1);
        }

    }


    @OnActivityResult(GET_LOCALTION)
    void onActivityResultGetLocation(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (data != null) {
                LocationBean locationBean = (LocationBean) data.getSerializableExtra("POS");
                try {
                    JSONObject o = new JSONObject();
                    o.put("latitude", locationBean.latitude + "");
                    o.put("longitude", locationBean.longitude + "");
                    o.put("localName", locationBean.locName + "");
                    o.put("street", locationBean.street + "");
                    sendLocation(ConstDefine.location_pwd + "@" + o.toString(), MsgInFo.MOLD_LOCATION);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendLocation(String content, int mold) {
        if (!TextUtils.isEmpty(content)) {
            String localUserJid = MyApplication.getInstance().getOpenfireJid();
            String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
            XmppMessage msg = new XmppMessage();

            if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
                msg.setType(MsgInFo.TYPE_GROUPCHAT);
                msg.setExtGroupMemberUserName(localUserJid);
                //对应群聊时，发送人的nickname
                msg.setExtGroupMemberUserNick(MyApplication.getInstance().getLocalMember().NickName);
            } else if (chatType.equalsIgnoreCase(MsgInFo.TYPE_CHAT)) {
                msg.setType(MsgInFo.TYPE_CHAT);
            } else if (Member.isTypeApp(remoteUserName)) {
                msg.setType(MsgInFo.TYPE_HEADLINE);
            }

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
            long showTime = System.currentTimeMillis();
            msg.setShowTime(0L);
            if ((showTime - msgTime) > time_l) {
                msg.setShowTime(showTime);
                msgTime = showTime;
            }
            MsgService.getMsgWriter(this).sendMsg(msg);
        }
    }


    @Click({R.id.btn_chat_add, R.id.btn_chat_emo, R.id.btn_chat_voice,
            R.id.btn_chat_keyboard, R.id.btn_chat_send, R.id.edit_user_comment,
            R.id.tv_picture, R.id.tv_camera, R.id.tv_location, R.id.tv_movie, R.id.tv_file})
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.btn_chat_add: // +号
                showAddLayout();
                break;
            case R.id.btn_chat_emo: // emo表情
                showEmoLayout();
                break;

            case R.id.btn_chat_voice: // 切换 发送语音
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_speak.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                // 隐藏文本框
                edit_user_comment.setVisibility(View.GONE);

                break;
            case R.id.btn_chat_keyboard:// 切换 键盘
                layout_more.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                edit_user_comment.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.GONE);

                edit_user_comment.setEnabled(true);
                edit_user_comment.requestFocus();
                KeyBoardUtils.openKeybord(edit_user_comment, XchatActivity.this);
                break;
            case R.id.btn_chat_send:// 发送文本
                sendTextMsg(edit_user_comment.getText().toString());
                edit_user_comment.setText("");
                break;
            case R.id.edit_user_comment:
                layout_more.setVisibility(View.GONE);
                mListView.setSelection(mAdapter.getCount() - 1);
                if (tips_send_img_lay.getVisibility() == View.VISIBLE)
                    tips_send_img_lay.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_picture:// 选择图片
                layout_more.setVisibility(View.GONE);
                selectImageFromLocal();
                break;
            case R.id.tv_camera:
                layout_more.setVisibility(View.GONE);
                selectImageFromCamera();
                break;

            case R.id.tv_location:
                startActivityForResult(new Intent(XchatActivity.this, LocationActivity.class), GET_LOCALTION);
                break;

            case R.id.tv_movie:
                startActivityForResult(new Intent(XchatActivity.this, MovieDialogActivity.class), Constant.REQUEST_CODE_TAKE_MOVIE);
                break;
            case R.id.tv_file:
                startActivityForResult(new Intent(XchatActivity.this, LocalFileSelectActivity.class), REQUEST_CODE_SEND_FILE);
                break;
        }
    }

    @TextChange(R.id.edit_user_comment)
    public void textChanged() {
        if (!TextUtils.isEmpty(edit_user_comment.getText())) {
            btn_chat_voice.setVisibility(View.GONE);
            btn_chat_send.setVisibility(View.VISIBLE);
        } else {
            btn_chat_voice.setVisibility(View.VISIBLE);
            btn_chat_send.setVisibility(View.GONE);
        }
    }

    /**
     * 显示更多 +号的布局
     */
    public void showAddLayout() {
        KeyBoardUtils.closeKeybord(edit_user_comment, XchatActivity.this);
        if (layout_more.getVisibility() == View.GONE) {
            layout_more.setVisibility(View.VISIBLE);

        }
        if (layout_add.getVisibility() == View.GONE) {
            layout_add.setVisibility(View.VISIBLE);
//            if (!isTip)
            new Thread() {
                public void run() {
                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver cr = XchatActivity.this.getContentResolver();
                    Cursor cursor = cr.query(mImageUri, null,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME +
                                    " = ?  or  " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?",
                            new String[]{"Screenshots", "Camera"},
                            MediaStore.Images.Media.DATE_ADDED + " desc");

                    if (cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        long add_time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                        if ((System.currentTimeMillis() - add_time * 1000) < time_l * 2) {

                            String imgTime = saveConfig.getStringConfig("tip");
                            if (imgTime == null || !imgTime.equals(add_time + "")) {
                                mHandler.obtainMessage(0x110, path).sendToTarget();
                                saveConfig.setStringConfig("tip", add_time + "");
                            }
                        }
                    }
                    cursor.close();
                }
            }.start();
            layout_emo.setVisibility(View.GONE);

        } else {
            layout_add.setVisibility(View.GONE);
            if (tips_send_img_lay.getVisibility() == View.VISIBLE)
                tips_send_img_lay.setVisibility(View.INVISIBLE);
        }

    }


    protected PopupWindow popWin;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x110:
                    final String imgPath = (String) msg.obj;
                    tips_send_img_lay.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(decodeSampledBitmapFromPath(imgPath, imageView.getWidth(), imageView.getHeight()));
                    tips_send_img_lay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onCompressBitmap(imgPath);
                            tips_send_img_lay.setVisibility(View.INVISIBLE);
                        }
                    });
                    isTip = true;
                    break;

                case 0x01:// 小视频下载进度
                {
                    ChatItemReceiveMovieView movieView = (ChatItemReceiveMovieView) msg.obj;
                    movieView.roundProgress.setProgress(msg.arg2);
                    break;
                }

                case 0x02:// 小视频下载完成
                {
                    ChatItemReceiveMovieView movieView = (ChatItemReceiveMovieView) msg.obj;
                    movieView.isDowning = false;
                    movieView.roundProgress.setVisibility(View.INVISIBLE);
                    movieView.viv_image.setImageResource(R.mipmap.mfc);
                    Intent intent = new Intent(XchatActivity.this, MoviePlayerActivity.class);
                    intent.putExtra("path", movieView.file.getAbsolutePath());
                    startActivity(intent);
                    break;
                }

                case 0x81:// 小视频下载开始
                {
                    ChatItemReceiveMovieView movieView = (ChatItemReceiveMovieView) msg.obj;
                    movieView.roundProgress.setVisibility(View.VISIBLE);
                    movieView.viv_image.setImageDrawable(new ColorDrawable(0));
                    movieView.isDowning = true;
                    break;
                }

                case 0x82:
                    Toast.makeText(XchatActivity.this, "网络无连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int reqwidth, int reqheight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqwidth || height > reqheight) {
            int widthRadio = Math.round(width * 1.0f / reqwidth);
            int heightRadio = Math.round(height * 1.0f / reqheight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * show emotions view
     */
    public void showEmoLayout() {
        KeyBoardUtils.closeKeybord(edit_user_comment, XchatActivity.this);
        if (layout_more.getVisibility() == View.GONE) {
            layout_more.setVisibility(View.VISIBLE);
        }
        if (layout_emo.getVisibility() == View.GONE) {
            layout_emo.setVisibility(View.VISIBLE);
            edit_user_comment.setVisibility(View.VISIBLE);
            btn_speak.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            if (tips_send_img_lay.getVisibility() == View.VISIBLE)
                tips_send_img_lay.setVisibility(View.INVISIBLE);
        } else {
            layout_emo.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化Xlistview
     */
    private void intiXListView() {
        L.d(TAG, " intiXListView()");
        registerForContextMenu(mListView);

        // 不允许加载更多
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);// 下拉刷新
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();// 第一次进来就加载。
        mListView.setDividerHeight(0);
        // 加载数据
        // TODO
        mAdapter = new XChatListAdapter(XchatActivity.this);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.mXTouchDown = new XListView.OnXTouchListener() {
            @Override
            public void onTouchDown() {
                if (tips_send_img_lay.getVisibility() == View.VISIBLE)
                    tips_send_img_lay.setVisibility(View.INVISIBLE);
            }
        };
    }

    /**
     * 初始化底部的布局
     */
    public void initBottomView() {
        L.d(TAG, " initBottomView()");
        if (Member.isTypeApp(remoteUserName)) {
            chat_normal_layout.setVisibility(View.GONE);
            chat_appmenu_layout.setVisibility(View.VISIBLE);
//          chat_appmenu_layout.setOnOperationClickListener(this);
            String key = "GetAppOpertionList_" + remoteUserName;
            GetAppOperationListResponse response = (GetAppOperationListResponse) ACache
                    .get(this).getAsObject(key);
            if (response != null
                    && response.BaseResponse.Ret == BaseResponse.RET_SUCCESS
                    && response.operationCount > 0) {
//                chat_appmenu_layout.bind(remoteUserName, response.operationList);
                // TODO: 
                setCustomMenu(response.operationList);
            } else {
                chat_appmenu_layout.setVisibility(View.VISIBLE);
            }
        } else {
            chat_normal_layout.setVisibility(View.VISIBLE);
            ImageButton viewById = (ImageButton) chat_normal_layout.findViewById(R.id.btn_changedto_appmenu);
            viewById.setVisibility(View.GONE);
            chat_appmenu_layout.setVisibility(View.GONE);
        }

        initEmoView();
    }
//"{\"customemenu\": [{\"title\":\"关于居然\",\"sub\":[{\"title\":\"居然服务\"},{\"title\":\"居然简介\"},{\"title\":\"居然位置\"},{\"title\":\"顾客留言\"}]},{\"title\":\"居然动态\",\"sub\":[{\"title\":\"传递幸福\"},{\"title\":\"河西印象\"},{\"title\":\"居然新闻\"}]},{\"title\":\"导购推荐\",\"sub\":[]}]}";

    @ViewById
    LinearLayout layout_custommenu; //自定义菜单布局

    private void setCustomMenu(List<AppPushEntity.ObjectContentEntity.OperationsEntity>
                                       operationList) {
        if (operationList != null && operationList.size() > 0) {
            chat_appmenu_layout.setVisibility(View.VISIBLE);
            layout_custommenu.removeAllViews();
            for (final AppPushEntity.ObjectContentEntity.OperationsEntity item : operationList) {

//                final JSONObject ob = btnJson.getJSONObject(i);
                LinearLayout layout = (LinearLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_custommenu, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                layout.setLayoutParams(lp);
                TextView tv_custommenu_name = (TextView) layout.findViewById(R.id.tv_custommenu_name);
                tv_custommenu_name.setText(item.content);
                if (item.operationList != null && item.operationList.size() > 0) // 显示三角
                {
                    tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black, 0);
                } else // 隐藏三角
                {
                    tv_custommenu_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                layout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (item.operationList == null || item.operationList.size() == 0) {
//                            Toast.makeText(getApplicationContext(), "主菜单点击事件",
//                                    Toast.LENGTH_SHORT).show();
                            // TODO: 2016/4/7  
                            onOperationClick(item);
                        } else {
                            popupWindow_custommenu = new PopMenus(getApplicationContext(),
                                    item.operationList, v.getWidth() + 10, 0);
                            // TODO: 2016/4/7  
                            popupWindow_custommenu.setOnOperationClickListener(XchatActivity.this);
                            popupWindow_custommenu.showAtLocation(v);
                        }
                    }
                });
                layout_custommenu.addView(layout);
            }
        } else {
            chat_appmenu_layout.setVisibility(View.GONE);
        }
    }

    @ViewById(R.id.btn_mmfooter_listtotext)
    ImageButton btn_mmfooter_listtotext;

    @ViewById(R.id.btn_changedto_appmenu)
    ImageButton btn_changedto_appmenu;

    @Click({R.id.btn_mmfooter_listtotext, R.id.btn_changedto_appmenu})
    public void onChangLayoutClick(View v) {
        // TODO: 2016/4/6 切换动画 
        if (v == btn_mmfooter_listtotext) {
            chat_normal_layout.setVisibility(View.VISIBLE);
            chat_appmenu_layout.setVisibility(View.GONE);
        } else if (v == btn_changedto_appmenu) {
            chat_normal_layout.setVisibility(View.GONE);
            chat_appmenu_layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化表情布局
     */
    private void initEmoView() {
        L.d(TAG, " initEmoView()");
        // TODO
//		emos = FaceTextUtils.faceTexts;
//		List<View> views = new ArrayList<View>();
//		for (int i = 0; i < 2; ++i) {
//			views.add(getGridView(i));
//		}
//		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
        viewBiaoQing.setOnItemClickListener(mBiaoQingOnItemClickListenr);
    }

    private AdapterView.OnItemClickListener mBiaoQingOnItemClickListenr = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String item = (String) parent.getItemAtPosition(position);
            insertCharSequence(edit_user_comment, SmileyParser.getInstance()
                    .addSmileySpans(item));
        }
    };

    public static void insertCharSequence(EmoticonsEditText editText, CharSequence text) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.insert(index, text);
    }

    /*private View getGridView(int i) {

		View root = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) root.findViewById(R.id.gridview);

		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(21, emos.sizeBigger()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(this,
				R.layout.item_face_text, list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
						int start = edit_user_comment.getSelectionStart();
						CharSequence content = edit_user_comment.getText()
								.insert(start, key);
						edit_user_comment.setText(content);
						// 定位光标位置
						CharSequence info = edit_user_comment.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
				} catch (Exception e) {

				}

			}
		});
		return root;
	}*/
    public XmppMessage msg;

    @Override
    protected void onResume() {
        L.d(TAG, "onResume()  ");
        isTip = false;
        MediaManager.resume();

       /* if (chatType){
            // TODO: 2016/3/11  
            updateTitle();
        }*/
//        updateReadWithNoObserver();
//        getCurrentFocus().requestLayout();

        if (msg != null) {
            Uri uri = ContentUris.withAppendedId(
                    XmppMessageContentProvider.CONTENT_URI, msg.getId());
            getContentResolver().notifyChange(uri, null);
        }

        XmppDbManager.getInstance(this).updateReadX(localUserJid, remoteUserName, remoteUserNick, chatType.equalsIgnoreCase(MsgInFo.TYPE_CHAT), this);
        super.onResume();
    }

    private void updateTitle() {
        String displayName = XmppDbManager.getInstance(this).getChatDisplayName(
                localUserJid, remoteUserName);
        if (displayName != null) {
            title.setText(displayName);
            remoteUserNick = displayName;
        }
    }

    @Background
    void updateReadWithNoObserver() {
        getContentResolver().unregisterContentObserver(mContentObserver);
        XmppDbManager.getInstance(this).updateReadX(localUserJid, remoteUserName, remoteUserNick, chatType.equalsIgnoreCase(MsgInFo.TYPE_CHAT), this);
        getContentResolver().registerContentObserver(
                XmppMessageContentProvider.CONTENT_URI, true, mContentObserver);
    }


    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int sizeBigger;

    @Override
    protected void onPause() {
        super.onPause();

        if (tips_send_img_lay.getVisibility() == View.VISIBLE)
            tips_send_img_lay.setVisibility(View.INVISIBLE);
        L.d(TAG, "onPause()  ");
//        清除不了 
//        String username = remoteUserName.substring(0, remoteUserName.lastIndexOf("@"));
//        if (chatType) {
//
//        } else {
//            username = username.concat("@user");
//        }
//        
//        final String avataBiggerUrl = Utils.getAvataUrl(username, sizeBigger);
//        ImagerLoaderOptHelper.removeFromCache(avataBiggerUrl,new ImageSize(sizeBigger,sizeBigger));

        SQLiteDatabase sd = new VoiceDb(MyApplication.getInstance()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", VoiceDb.SEND_READ);
        sd.update("voice", values, "type=?", new String[]{VoiceDb.SEND_NO_READ + ""});
        sd.close();
    }

    @Override
    protected void onDestroy() {
        L.d(TAG, "onDestroy()  ");
        super.onDestroy();
        MediaManager.release();
        getContentResolver().unregisterContentObserver(mContentObserver);
        MyApplication.getInstance().removeActivity(this);
        doInBackgroundOperationCancle = true;
        BackgroundExecutor.cancelAll("doInBackgroundOperation", true);
        doInBackgroundGetAppOperationListCanceld = true;
        BackgroundExecutor.cancelAll("doInBackgroundGetAppOperationList", true);

        if (!TextUtils.isEmpty(edit_user_comment.getText()))
            new DraftConfig(XchatActivity.this).setStringConfig(remoteUserName + "@" + localUserJid, edit_user_comment.getText().toString());
        else
            new DraftConfig(XchatActivity.this).setStringConfig(remoteUserName + "@" + localUserJid, "");

        if (MyApplication.getInstance().getDownloadManager().map.size() != 0) {
            Intent intent = new Intent(XchatActivity.this, DownloadService.class);
            intent.setAction(DownloadService.ACTION_STOP);
            startService(intent);
        }
    }
    
	/*
     * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) {
	 * getMenuInflater().inflate(R.menu.activity_chat_context_menu, menu);
	 * 
	 * AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	 * XmppMessage message = mAdapter.readEntity((Cursor)
	 * mAdapter.getItem(info.position));
	 * 
	 * if (Msg.MSG_TYPE_NORMAL != message.getMold()) {
	 * menu.removeItem(R.id.copy_message); }
	 * 
	 * super.onCreateContextMenu(menu, v, menuInfo); }
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) {
	 * AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
	 * .getMenuInfo(); XmppMessage message = mAdapter.readEntity((Cursor)
	 * mAdapter .getItem(menuInfo.position));
	 * 
	 * switch (item.getItemId()) { case R.id.copy_message: if
	 * (message.getDirect() == MsgInFo.INOUT_IN) { Content content =
	 * Content.createContent(message.getExtRemoteUserName(), message.getBody());
	 * if (Member.isTypeQun(message.getExtRemoteUserName())) { ContentQun
	 * contentQun = (ContentQun) content;
	 * 
	 * ClipboardManager cm = (ClipboardManager)
	 * getSystemService(CLIPBOARD_SERVICE); cm.setText(contentQun.realContent);
	 * } else { ContentNormal contentNormal = (ContentNormal) content;
	 * 
	 * ClipboardManager cm = (ClipboardManager)
	 * getSystemService(CLIPBOARD_SERVICE); cm.setText(contentNormal.content); }
	 * } else { ClipboardManager cm = (ClipboardManager)
	 * getSystemService(CLIPBOARD_SERVICE); cm.setText(message.getBody()); }
	 * 
	 * return true; case R.id.delete_message: delMsg(message); return true;
	 * 
	 * default: break; }
	 * 
	 * return super.onContextItemSelected(item); }
	 */

    /*@ItemLongClick(R.id.mListView)
    public void onItemLongClick( Cursor cursor) {
        L.d(TAG, "onItemLongClick");

    }*/

    /**
     * 删除聊天记录
     *
     * @param xmppMessage
     */
    @Background(id = "test")
    // AA自己维护了一个后台的线程池
    void delMsg(XmppMessage xmppMessage) {
        XmppDbManager.getInstance(XchatActivity.this)
                .deleteMessage(xmppMessage);
        // BackgroundExecutor.cancelAll("test")// 取消
    }

    /**
     * 当加载更多布局打开时，按返回键 关闭
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.d(TAG, "onKeyDown()  " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_more.getVisibility() == View.VISIBLE) {
                layout_more.setVisibility(View.GONE);
                return false;
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // sendHeadLineMessage("聊天室");
            return false;

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 录音结束回调
     *
     * @param seconds
     * @param filePath
     */
    @Override
    public void onFinish(float seconds, String filePath) {
        L.d(TAG, "onFinish()  ");
//        ShowToast(filePath);
        File voiceFile = new File(filePath);
        long lenth = voiceFile.length();
        if (lenth > 7) {
            sendVoiceMsg(seconds, filePath);
        } else {
            Toast.makeText(getApplicationContext(), "录音发送失败!", Toast.LENGTH_SHORT).show();
        }
    }

    //小视频录制回调
    public void onRecordFinish(String path, int second) {
        File movieFile = new File(path);
        if (movieFile.length() > 7) {
            sendMovieMsg(second, path);
        }
    }

    /**
     * @param content
     */
    @Background
    public void sendTextMsg(String content) {
        L.d("sendTextMsg()");
        if (!TextUtils.isEmpty(content)) {
            XmppMessage msg = new XmppMessage();
            if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
                msg.setType(MsgInFo.TYPE_GROUPCHAT);
                msg.setExtGroupMemberUserName(localUserJid);
                //对应群聊时，发送人的nickname
                msg.setExtGroupMemberUserNick(MyApplication.getInstance().getLocalMember().NickName);
            } else if (Member.isTypeApp(remoteUserName)) {
                msg.setType(MsgInFo.TYPE_HEADLINE);
            } else {
                msg.setType(MsgInFo.TYPE_CHAT);
            }
            msg.setTo_(remoteUserName);
            msg.setFrom_(localUserJid);

            String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
            msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
            msg.setExtLocalUserName(localUserJid);

            msg.setExtRemoteUserName(remoteUserName);
            msg.setExtRemoteDisplayName(remoteUserNick);

            msg.setBody(content);
            msg.setMold(MsgInFo.MOLD_TEXT);
            msg.setDirect(MsgInFo.INOUT_OUT);
            msg.setStatus(MsgInFo.STATUS_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            long showTime = System.currentTimeMillis();
            msg.setCreateTime(showTime);
            msg.setShowTime(0L);
            if ((showTime - msgTime) > time_l) {
                msg.setShowTime(showTime);
                msgTime = showTime;
            }

            MsgService.getMsgWriter(this).sendMsg(msg);
        }
    }

    /**
     * 发送语音消息
     *
     * @param seconds
     * @param filePath
     */
    @Background
    public void sendVoiceMsg(float seconds, String filePath) {
        L.d(TAG, "sendVoiceMsg()  filePath");
        int voiceLength = (int) Math.ceil(seconds);
        XmppMessage msg = new XmppMessage();

        if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
            msg.setType(MsgInFo.TYPE_GROUPCHAT);
            msg.setExtGroupMemberUserName(localUserJid);
            //对应群聊时，发送人的nickname
            msg.setExtGroupMemberUserNick(MyApplication.getInstance().getLocalMember().NickName);
        } else if (Member.isTypeApp(remoteUserName)) {
            msg.setType(MsgInFo.TYPE_HEADLINE);
        } else {
            msg.setType(MsgInFo.TYPE_CHAT);
        }
        msg.setTo_(remoteUserName);
        msg.setFrom_(localUserJid);

        String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
        msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
        msg.setExtLocalUserName(localUserJid);

        msg.setExtRemoteUserName(remoteUserName);
        msg.setExtRemoteDisplayName(remoteUserNick);

        msg.setBody("[语音]");

        msg.setMold(MsgInFo.MOLD_VOICE);
        msg.setDirect(MsgInFo.INOUT_OUT);
        msg.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
        msg.setReadStatus(MsgInFo.READ_TRUE);
        msg.setFilePath(filePath);
        msg.setExtInternalFilePath(filePath);
        msg.setVoiceLength(voiceLength);
        long showTime = System.currentTimeMillis();
        msg.setCreateTime(showTime);
        msg.setShowTime(0L);
        if ((showTime - msgTime) > time_l) {
            msg.setShowTime(showTime);
            msgTime = showTime;
        }
        MessageUtils.sendMediaMsg(XchatActivity.this, msg);
    }


    /**
     * 发送小视频消息
     *
     * @param seconds
     * @param filePath
     */
    @Background
    public void sendMovieMsg(int seconds, String filePath) {
        L.d(TAG, "sendVoiceMsg()  filePath");
        int voiceLength = seconds;
        XmppMessage msg = new XmppMessage();

        if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
            msg.setType(MsgInFo.TYPE_GROUPCHAT);
            msg.setExtGroupMemberUserName(localUserJid);
            //对应群聊时，发送人的nickname
            msg.setExtGroupMemberUserNick(MyApplication.getInstance().getLocalMember().NickName);
        } else if (Member.isTypeApp(remoteUserName)) {
            msg.setType(MsgInFo.TYPE_HEADLINE);
        } else {
            msg.setType(MsgInFo.TYPE_CHAT);
        }
        msg.setTo_(remoteUserName);
        msg.setFrom_(localUserJid);

        String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
        msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
        msg.setExtLocalUserName(localUserJid);

        msg.setExtRemoteUserName(remoteUserName);
        msg.setExtRemoteDisplayName(remoteUserNick);

        msg.setBody("[小视频]");

        msg.setMold(MsgInFo.MOLD_MOVIE);
        msg.setDirect(MsgInFo.INOUT_OUT);
        msg.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
        msg.setReadStatus(MsgInFo.READ_TRUE);
        msg.setFilePath(filePath);
        msg.setExtInternalFilePath(filePath);
        msg.setVoiceLength(voiceLength);
        long showTime = System.currentTimeMillis();
        msg.setCreateTime(showTime);
        msg.setShowTime(0L);
        if ((showTime - msgTime) > time_l) {
            msg.setShowTime(showTime);
            msgTime = showTime;
        }
        MessageUtils.sendMediaMsg(XchatActivity.this, msg);
    }

    /**
     * 发送 图片消息
     *
     * @param filePath
     */
    @Background
    public void sendImageMsg(String filePath) {
        L.d(TAG, "sendImageMsg()  filePath:" + filePath);
        if (!TextUtils.isEmpty(filePath)) {
            XmppMessage msg = new XmppMessage();

            if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
                msg.setType(MsgInFo.TYPE_GROUPCHAT);
                msg.setExtGroupMemberUserName(localUserJid);
                //对应群聊时，发送人的nickname
                msg.setExtGroupMemberUserNick(MyApplication.getInstance().getLocalMember().NickName);
            } else if (Member.isTypeApp(remoteUserName)) {
                msg.setType(MsgInFo.TYPE_HEADLINE);
            } else {
                msg.setType(MsgInFo.TYPE_CHAT);
            }
            msg.setTo_(remoteUserName);
            msg.setFrom_(localUserJid);

            String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
            msg.setExtLocalDisplayName(LocalUserNickName); // NickName对应display
            msg.setExtLocalUserName(localUserJid);

            msg.setExtRemoteUserName(remoteUserName);
            msg.setExtRemoteDisplayName(remoteUserNick);

            msg.setBody("[图片]");
            msg.setMold(MsgInFo.MOLD_IMG);
            msg.setDirect(MsgInFo.INOUT_OUT);
            msg.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            msg.setFilePath("file://" + filePath);
            msg.setExtInternalFilePath("file://" + filePath);

            msg.setExtLocalUserName(localUserJid.concat("@").concat(
                    OpenfireConstDefine.OPENFIRE_SERVER_NAME));
            msg.setExtRemoteUserName(remoteUserName);

            msg.setExtLocalDisplayName(MyApplication.getInstance()
                    .getLocalMember().NickName);
            msg.setExtRemoteDisplayName(remoteUserNick);
            long showTime = System.currentTimeMillis();
            msg.setCreateTime(showTime);
            msg.setShowTime(0L);
            if ((showTime - msgTime) > time_l) {
                msg.setShowTime(showTime);
                msgTime = showTime;
            }
            MessageUtils.sendMediaMsg(XchatActivity.this, msg);
//            MsgService.getMsgWriter(this).sendMsg(msg);

        }
    }

    @Background
    void onCompressBitmap(String filePaht) {
        Bitmap thumbnailBitmap = ThumbnailUtils
                .createImageThumbnailYouXin(filePaht);
        L.d(TAG, "onCompressBitmap() thumbnailBitmap:" + thumbnailBitmap);
        if (thumbnailBitmap != null) {
            L.d(TAG,
                    "onCompressBitmap() thumbnailBitmap:["
                            + thumbnailBitmap.getWidth() + "x"
                            + thumbnailBitmap.getHeight() + "]");
            File imageOutFileDir = Utils.ensureIMSubDir(this,
                    Utils.FILE_PATH_SUB_DIR_IMAGE_OUT);
            // Utils.createNoMediaFile(imageOutFileDir);
            File imageOutFile = new File(imageOutFileDir,
                    System.currentTimeMillis() + ".jpg");
            ThumbnailUtils.compress(thumbnailBitmap, imageOutFile, 68);
            thumbnailBitmap.recycle();
            sendImageMsg(imageOutFile.getPath());
        }
    }

    private ContentObserver mContentObserver = new ContentObserver(mHandler) {
        public void onChange(boolean selfChange, Uri uri) {
            L.d(TAG, "onChange(boolean, Uri) uri:" + uri);

            mAsyncQueryHandler.startQuery(0, null,
                    XmppMessageContentProvider.CONTENT_URI, null,
                    XmppMessageDao.Properties.ExtLocalUserName.columnName
                            + " like ? AND "
                            + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                            + " like ? AND "
                            + XmppMessageDao.Properties.CreateTime.columnName
                            + " > ? ", new String[]{localUserJid.concat("%"),
                            remoteUserName.concat("%"), (limitTime - 1) + ""}, XmppMessageDao.Properties.CreateTime.columnName + " ASC ");
            updateReadWithNoObserver();

            if (!isDelMsgAction) {
                isDelMsgAction = false;
                mListView.smoothScrollToPosition(mListView.getCount() - 1);
            }
            boolean isQun = false;
            if (chatType.equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
                isQun = true;
            }

            if (isQun) {
//                TODO
                updateTitle();
            }
        }

    };

    /**
     * 选择图片
     */
    public void selectImageFromLocal() {
        L.d(TAG, "selectImageFromLocal()  ");
        Intent intent = MethodsCompat.getSelectImageFromSDIntent();
        closeFooterLayout();
        startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_LOCAL);
    }

    private void closeFooterLayout() {
        layout_add.setVisibility(View.GONE);
        if (tips_send_img_lay.getVisibility() == View.VISIBLE)
            tips_send_img_lay.setVisibility(View.INVISIBLE);
        layout_emo.setVisibility(View.GONE);
        layout_more.setVisibility(View.GONE);
    }

    @OnActivityResult(Constant.REQUEST_CODE_TAKE_LOCAL)
    public void resultForSelectImag(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();

                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            if (localSelectPath == null
                                    || localSelectPath.equals("null")) {
                                ShowToast("找不到您想要的图片");
                                return;
                            }

                            onCompressBitmap(localSelectPath);
                        }
                        cursor.close();

                    } else {
                        onCompressBitmap(selectedImage.getPath());
                    }
                }
            }
        }
    }

    /**
     * 启动相机拍照
     */
    public void selectImageFromCamera() {
        L.d(TAG, "selectImageFromCamera()  ");
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Constant.IMAGER_FROM_CAMERA);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


        closeFooterLayout();
        startActivityForResult(openCameraIntent,
                Constant.REQUEST_CODE_TAKE_CAMERA);
    }

    @OnActivityResult(Constant.REQUEST_CODE_TAKE_CAMERA)
    public void onResultForCamera(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
//            ShowToast("camera path:" + localCameraPath);
            // sendImageMsg(localCameraPath);
            onCompressBitmap(localCameraPath);
        }
    }

    @OnActivityResult(Constant.REQUEST_CODE_TAKE_MOVIE)
    public void onResultForMovie(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            onRecordFinish(data.getStringExtra("path"), data.getIntExtra("second", 0));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        L.d(TAG, "onTouchEvent() ");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (layout_more.getVisibility() == View.VISIBLE) {
                    closeFooterLayout();
                }
                KeyBoardUtils.closeKeybord(edit_user_comment, XchatActivity.this);

                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onRefresh() {
        L.d(TAG, " onRefresh()");
        // mListView.postDelayed
        mAsyncQueryHandler.startQuery(0, null,
                XmppMessageContentProvider.CONTENT_URI, null,
                XmppMessageDao.Properties.ExtLocalUserName.columnName
                        + " like ? AND "
                        + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                        + " like ? ", new String[]{localUserJid.concat("%"),
                        remoteUserName.concat("%")}, XmppMessageDao.Properties.CreateTime.columnName + " desc Limit " + (limit + msgCount) + " Offset 0");
    }

    @Override
    public void onLoadMore() {
        L.d(TAG, " onLoadMore()");
        mListView.stopLoadMore();
    }

    @OnActivityResult(ContactDetailActivity.FROM_WHERE_IS_GTOUP_CHAT)
    void onActivityResultGroupChat(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            String jid = data.getStringExtra(ContactDetailActivity.USERNAME_JID);
            String nickname = data.getStringExtra(ContactDetailActivity.USERNICKNAME);
            XchatActivity_.intent(this).remoteUserName(jid)
                    .remoteUserNick(nickname).start();
            finish();
        }
    }


    @Background(id = "doInBackgroundOperation", serial = "doInBackgroundOperation")
    void doInBackgroundOperation(String url) {
        L.d(TAG, "doInBackgroundOperation() url:" + url);

        operationRest.setRootUrl(url);
        // ServiceResult<Object> response = operationRest.get();
        ServiceRequest request = new ServiceRequest();
        request.baseRequest = CacheUtils.getBaseRequest(this);
        L.d(TAG, "doInBackgroundOperation() request:" + request);
        Response response = operationRest.post(request);
        L.d(TAG, "doInBackgroundOperation() response:" + response);

        onPostExecuteOpt(response);
    }


    @UiThread
    void onPostExecuteOpt(Response response) {
        L.d(TAG, "onPostExecuteOpt() response:" + response);

        if (doInBackgroundOperationCancle) {
            return;
        }

        if (response == null || response.BaseResponse == null) {
            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.BaseResponse.Ret != 0) {
            Utils.showToastShort(this, response.BaseResponse.ErrMsg);
        }
    }

    @Override
    public void onOperationClick(AppPushEntity.ObjectContentEntity.OperationsEntity operation) {
        L.d(TAG, "onOperationClick() operation:" + operation);
        if (AppPushEntity.ObjectContentEntity.OperationsEntity.MSG_TYPE_BROWSER.equals(operation
                .operationType)) {
            H5Activity_.intent(this).remoteDisplayName(remoteUserNick)
                    .url(dealUrl(operation.action)).start();
        } else if (AppPushEntity.ObjectContentEntity.OperationsEntity.MSG_TYPE_REST.equals
                (operation.operationType)) {
            doInBackgroundOperation(dealUrl(operation.action));
        }
    }

    private String dealUrl(String url) {
        try {
            BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
            UriComponents uc = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("uid", baseRequest.Uid)
                    .queryParam("token", baseRequest.Token).build();
            return uc.toUriString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onOperationsClick(List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations) {
// L.d(TAG, "onOperationsClick() operations:" + operations);
        OperationsMoreDialogFragment fragment = OperationsMoreDialogFragment_
                .builder().build();
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        operations = new ArrayList<AppPushEntity.ObjectContentEntity.OperationsEntity>(operations.subList(2,
                operations.size()));
        args.putSerializable("operations", (Serializable) operations);
        fragment.setArguments(args);
        fragment.setOnOperationClickListener(this);
        fragment.show(getSupportFragmentManager(),
                OperationsMoreDialogFragment.class.getName());
    }

    boolean doInBackgroundGetAppOperationListCanceld = false;

    @Background
    void doInBackgroundGetAppOperationList() {
        L.d(TAG, "doInBackgroundGetAppOperationList() remoteUserName:"
                + remoteUserName);

        BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
        GetAppOperationListRequest request = new GetAppOperationListRequest();
        request.BaseRequest = baseRequest;
        request.username = remoteUserName;

        L.d(TAG, "doInBackgroundGetAppOperationList() request:" + request);
        L.d(TAG, "getRootUrl" + myRestClient.getRootUrl());
        GetAppOperationListResponse response = myRestClient
                .getAppOperationList(request);
        L.d(TAG, "doInBackgroundGetAppOperationList() response:" + response);

        onPostExecuteGetAppOperationList(response);
    }

    @UiThread
    void onPostExecuteGetAppOperationList(GetAppOperationListResponse response) {
        L.d(TAG, "onPostExecuteGetAppOperationList() response:" + response);

        if (doInBackgroundGetAppOperationListCanceld) {
            return;
        }

        if (response == null) {
            Toast.makeText(this, "获取应用菜单失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            // Toast.makeText(this, response.BaseResponse.ErrMsg,
            // Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "获取应用菜单失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = "GetAppOpertionList_" + remoteUserName;
        GetAppOperationListResponse responseCache = (GetAppOperationListResponse) ACache
                .get(this).getAsObject(key);
        if (!isEqueals(response, responseCache)) {
            // TODO: 2016/4/7  
//            chat_appmenu_layout.bind(remoteUserName, response.operationList);
            setCustomMenu(response.operationList);
            ACache.get(this).put(key, response);
        }
    }

    private boolean isEqueals(GetAppOperationListResponse r1,
                              GetAppOperationListResponse r2) {
        String str1 = Utils.writeValueAsString(r1);
        String str2 = Utils.writeValueAsString(r2);
        if (str1 != null && str1.equals(str2)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
    }


}
