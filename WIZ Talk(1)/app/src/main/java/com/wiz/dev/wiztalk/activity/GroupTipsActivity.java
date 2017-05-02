package com.wiz.dev.wiztalk.activity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.XGroupTipsAdapter;
import com.wiz.dev.wiztalk.loader.XGroupTipsLoader;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.view.MediaManager;

@EActivity(R.layout.activity_grouptips)
public class GroupTipsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int Loader_num = 2;

    private static final String TAG = GroupTipsActivity.class.getSimpleName();

    @ViewById(R.id.list)
    ListView mListView;

    String localUserJid;

    @Bean
    XGroupTipsAdapter mGroupTipsAdapter;

    @AfterInject
    public void afterInject() {
        // 发送来的消息不带资源名称，通过它查找时，不能带资源名称
        localUserJid = MyApplication.getInstance().getOpenfireJid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @AfterViews
    void afterViews() {
        L.d(TAG, "afterViews() ");
        initTopBarForLeft(getString(R.string.group_tips_activity_bar_title));

        mGroupTipsAdapter = new XGroupTipsAdapter(this);
        mListView.setAdapter(mGroupTipsAdapter);

        getLoaderManager().initLoader(Loader_num, null, this);
        getContentResolver().registerContentObserver(
                XmppMessageContentProvider.CONTENT_URI, true, mContentObserver);
    }

    void destroyed() {
        L.d(TAG, "group destroyed！");
    }

    private ContentObserver mContentObserver = new ContentObserver(
            new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            L.d(TAG, "onChange(boolean, Uri) uri:" + uri);
            mListView.smoothScrollToPosition(mListView.getCount() - 1);
        }

        ;
    };

    @Override
    protected void onResume() {
        L.d(TAG, "onResume()  ");
        //TODO testing
        updateReadWithNoObserver();
        super.onResume();
    }

    @Background
    void updateReadWithNoObserver() {
        getContentResolver().unregisterContentObserver(mContentObserver);
        XmppDbManager.getInstance(this).updateRead(localUserJid, "group.".concat(OpenfireConstDefine
                .OPENFIRE_SERVER_NAME));
        getContentResolver().registerContentObserver(
                XmppMessageContentProvider.CONTENT_URI, true, mContentObserver);
    }

    @ItemClick(R.id.list)
    public void onItemClick(Cursor cursor) {
//        XmppMessage{id=103, sid='null', to_='19@group.skysea.com', 
// from_='00059f7f10594bec8a4d29abd1e29e52@skysea.com', 
// type='group_operate', body='邹明申请加入群 ', direct=1, createTime=1458034567500, mold=0, 
// voiceLength=null, filePath='null', status=0, readStatus=1, 
// extLocalUserName='00059f7f10594bec8a4d29abd1e29e52@skysea.com',
// extRemoteUserName='group.skysea.com', extLocalDisplayName='test3',
// extRemoteDisplayName='群通知', extGroupOperateType=3, extGroupOperateIsdeal=4, 
// extGroupOperateUserName='42039767747a40b6b98a7a11c0c26a12', 
// extGroupOperateUserNick='邹明', extGroupMemberUserName='null', extGroupMemberUserNick='null'}

        XmppMessage msg = mGroupTipsAdapter.readEntity(cursor);
      
        if (msg.getExtGroupOperateType() == MsgInFo.OPERATE_TIPS) {
            //管理员删除群    自己被移除群
            Toast.makeText(GroupTipsActivity.this, "该群已经不存在\n无法进入", Toast.LENGTH_SHORT)
                    .show();

        } else if (msg.getExtGroupOperateType() == MsgInFo.OPERATE_INVITE) {//邀请
            if (msg.getExtGroupOperateIsdeal() != null && msg.getExtGroupOperateIsdeal()
                    == MsgInFo.OPERATE_INVITE_STATUS) {
                XchatActivity_.intent(this)
                        .chatType(MsgInFo.TYPE_GROUPCHAT)
                        .remoteUserName(msg.getTo_())
                        .remoteUserNick(msg.getExtLocalDisplayName()).start();
                this.finish();
            } else {
                Toast.makeText(GroupTipsActivity.this, "请先加入该群\n之后再进行该操作", Toast.LENGTH_SHORT)
                        .show();
            }
            
        } else if (msg.getExtGroupOperateType() == MsgInFo.OPERATE_APPLY) {//申请
            XchatActivity_.intent(this)
                    .chatType(MsgInFo.TYPE_GROUPCHAT)
                    .remoteUserName(msg.getTo_())
                    .remoteUserNick(msg.getExtLocalDisplayName()).start();
            this.finish();     
        }
    }

    @Override
    protected void onDestroy() {
        L.d(TAG, "onDestroy()  ");
        super.onDestroy();
        MediaManager.release();
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new XGroupTipsLoader(getApplicationContext(), localUserJid);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGroupTipsAdapter.changeCursor(data);
        mGroupTipsAdapter.getCount();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mGroupTipsAdapter.swapCursor(null);
    }


}
