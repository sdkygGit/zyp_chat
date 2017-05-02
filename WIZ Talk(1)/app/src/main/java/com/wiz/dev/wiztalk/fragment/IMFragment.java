package com.wiz.dev.wiztalk.fragment;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.GroupListActivity_;
import com.wiz.dev.wiztalk.activity.GroupTipsActivity_;
import com.wiz.dev.wiztalk.activity.XchatActivity_;
import com.wiz.dev.wiztalk.adapter.RecentChatAdapter;
import com.wiz.dev.wiztalk.loader.RecentChatMsgLoader;
import com.wiz.dev.wiztalk.utils.DraftConfig;
import com.wiz.dev.wiztalk.view.HeaderLayout;
import com.wiz.dev.wiztalk.view.dialog.DialogTips;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;

import java.util.logging.LogRecord;

@EFragment(R.layout.fragment_recent_msg)
public class IMFragment extends FragmentBase implements LoaderManager.LoaderCallbacks<Cursor> ,IMainFragment{

    private static final int loader_num = 0;

    private static final String TAG = IMFragment.class.getSimpleName();

    @ViewById(R.id.list)
    ListView mListView;

    String localUserJid;

    Handler handler = new Handler();

    @Bean
    RecentChatAdapter mRecentChatAdapter;


    DraftConfig draftConfig;

    @AfterInject
    public void afterInject() {
        //发送来的消息不带资源名称，通过它查找时，不能带资源名称
        localUserJid = MyApplication.getInstance().getOpenfireJid();
        draftConfig = new DraftConfig(getContext());
    }

    @AfterViews
    public void afterViews() {
        L.d(TAG, "afterViews() ");
        L.d(TAG, "afterViews() localUserJid:" + localUserJid);
        mListView.setAdapter(mRecentChatAdapter);
        String titleName = getResources().getString(R.string.tab_msg);
        initTopBarForBoth2(titleName, -1,
                R.drawable.actionbar_facefriend_selector,
                new HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        GroupListActivity_.intent(getActivity()).start();
                    }
                });
        getLoaderManager().initLoader(loader_num, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = XmppDbManager.getInstance(getContext()).getRecentChatList(localUserJid);

                mRecentChatAdapter.changeCursor(cursor);
            }
        },1000);
    }

    @ItemClick(R.id.list)
    public void onItemClick(Cursor cursor) {
        XmppMessage msg = mRecentChatAdapter.readEntity(cursor);
        boolean flag = msg.getType().equals(MsgInFo.TYPE_CHAT)?false:true; //true: qun
        L.d(TAG,msg.toString());

//        不能根据msgtype;要根据是否含有 @App，进入消息只有两个，群聊，单聊，应用属于单聊
        if(msg.getType().equals(MsgInFo.TYPE_GROUP_OPERATE)){
            GroupTipsActivity_.intent(this).start();
        }else {
            String username = msg.getExtRemoteUserName();
            String displayname = msg.getExtRemoteDisplayName();
            if (flag) {
                XchatActivity_.intent(this).remoteUserName(username).remoteUserNick(displayname)
                        .chatType(MsgInFo.TYPE_GROUPCHAT).start();
            }else{
//              应用消息也是单聊
                XchatActivity_.intent(this).remoteUserName(username).remoteUserNick(displayname)
                        .chatType(MsgInFo.TYPE_CHAT).start();
            }
        } 
    }

    @ItemLongClick(R.id.list)
    void onItemLongClick(Cursor cursor) {
        final XmppMessage msg = mRecentChatAdapter.readEntity(cursor);
//        Toast.makeText(getActivity(), "msg id:" + msg.getId(), Toast.LENGTH_SHORT).show();

        DialogTips tips = new DialogTips(getActivity(), msg.getExtRemoteDisplayName(),
                "是否删除与该联系人的对话？", "确定",
                true,
                true);

        tips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRecentMsg(msg);
                draftConfig.setStringConfig(msg.getExtRemoteUserName()+"@" + MyApplication.getInstance().getOpenfireJid(),"");
            }
        });
        tips.show();
        tips = null;

    }

    @Background
    void deleteRecentMsg(XmppMessage msg) {
        XmppDbManager.getInstance(getActivity()).deleteChat(localUserJid, msg.getExtRemoteUserName());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new RecentChatMsgLoader(getActivity().getApplicationContext(), localUserJid);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecentChatAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecentChatAdapter.swapCursor(null);
    }

    @Override
    public void onTagChanged() {
        mRecentChatAdapter.notifyDataSetChanged();
    }
}
