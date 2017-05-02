package com.wiz.dev.wiztalk.apppush.view;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.utils.L;
import com.pulltorefresh.lib.PullToRefreshBase;
import com.pulltorefresh.lib.PullToRefreshListView;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.AppDetailActivity_;
import com.wiz.dev.wiztalk.activity.MainActivity;
import com.wiz.dev.wiztalk.activity.XchatActivity_;
import com.wiz.dev.wiztalk.adapter.AppRecentChatAdapter;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;
import com.wiz.dev.wiztalk.apppush.loader.AppRecentChatLoader;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetApplicationListRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetApplicationListResponse;
import com.wiz.dev.wiztalk.fragment.FragmentBase;
import com.wiz.dev.wiztalk.fragment.IMainFragment;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.view.AppRecentChatItemView;
import com.wiz.dev.wiztalk.view.dialog.DialogTips;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import java.util.List;

@EFragment(R.layout.fragment_recent)
public class AppPushFragment extends FragmentBase implements LoaderManager.LoaderCallbacks<Cursor>,
        IMainFragment, SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener, SlideAndDragListView.OnListScrollListener {

    private static final int loader_num = 11;
//	private static final int loader_num = 0;

    private static final String TAG = AppPushFragment.class.getSimpleName();

    public static SlideAndDragListView mListView;

    @ViewById(R.id.pull_refresh_list)
    PullToRefreshListView mPullRefreshListView;
    String localUserJid;
    private Menu mMenu;
    @Bean
    AppRecentChatAdapter mRecentChatAdapter;

    @AfterInject
    public void afterInject() {
        //发送来的消息不带资源名称，通过它查找时，不能带资源名称
        localUserJid = MyApplication.getInstance().getOpenfireJid();
    }

    public void initMenu() {
        mMenu = new Menu(true, false);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) + 30)
                .setBackground(getDrawable(getActivity(), R.drawable.menu_read_selector))
                .setText("详情")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.WHITE)
                .setTextSize(12)
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) + 30)
                .setBackground(getDrawable(getActivity(), R.drawable.menu_del_selector))
                .setText("标为已读")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.WHITE)
                .setTextSize(12)
                .build());
    }

    public Drawable getDrawable(Context context, int res) {
        return context.getResources().getDrawable(res);
    }

    @AfterViews
    public void afterViews() {
        L.d(TAG, "afterViews() ");
        L.d(TAG, "afterViews() localUserJid:" + localUserJid);
        initMenu();

        mListView = (SlideAndDragListView) mPullRefreshListView.getRefreshableView();
        mListView.setDivider(getResources().getDrawable(R.drawable.listview_divider));
        mListView.setMenu(mMenu);
        mListView.setAdapter(mRecentChatAdapter);


        mListView.setOnListItemLongClickListener(this);
        mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
        mListView.setOnListScrollListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<SlideAndDragListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<SlideAndDragListView> refreshView) {
//                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
////				// Update the LastUpdatedLabel
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // Do work to refresh the list here.
                // new GetDataTask().execute();
                new AsyncTask<Void, Void, List<Member>>() {
                    Appmsgsrv8094 mIMInterfaceProxy;

                    @Override
                    protected void onPreExecute() {
                        mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
                        mIMInterfaceProxy.setRestErrorHandler(new RestErrorHandler() {
                            @Override
                            public void onRestClientExceptionThrown(NestedRuntimeException e) {
                            }
                        });
                    }

                    @Override
                    protected List<Member> doInBackground(Void... params) {
                        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());
                        GetApplicationListRequest request = new GetApplicationListRequest();
                        request.BaseRequest = baseRequest;
                        GetApplicationListResponse response = mIMInterfaceProxy
                                .getApplicationList(request);
                        if (response == null) {
                            return null;
                        }
                        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
                            Toast.makeText(getActivity(), response.BaseResponse.ErrMsg,
                                    Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        return response.memberList;
                    }

                    @Override
                    protected void onPostExecute(List<Member> members) {
                        if (members != null && members.size() > 0)
                            for (Member member : members) {
                                ((MainActivity)getActivity()).doInbackgroundInsert(member);
                            }
                        mPullRefreshListView.onRefreshComplete();
                    }
                }.execute();

            }
        });
        String titleName = getResources().getString(R.string.tab_app);
        initTopBarForOnlyTitle(titleName);
        getLoaderManager().initLoader(loader_num, null, this);

//        doInbackgroundInsert();

    }

//    @ItemClick(R.id.list)
//    public void onItemClick(Cursor cursor) {
//
//        XmppMessage msg = mRecentChatAdapter.readEntity(cursor);
//        /*ChatActivity_.intent(getActivity())
//                .localUserName(MyApplication.getInstance().getLocalUserName())
//				.remoteUserName(msg.getExtRemoteUserName())
//				.remoteDisplayName(msg.getExtRemoteDisplayName()).start();*/
//        XchatActivity_.intent(this).remoteUserName(msg.getExtRemoteUserName())
//                .remoteUserNick(msg.getExtRemoteDisplayName())
//                .chatType(MsgInFo.TYPE_CHAT).start();
//    }

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
        return new AppRecentChatLoader(getActivity().getApplicationContext(), localUserJid);

//		return new RecentChatMsgLoader(getActivity().getApplicationContext(), localUserJid);
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

    @Background
    void doInbackgroundInsert(Member member) {
        if (XmppDbManager.getInstance(getActivity()).getChatList(MyApplication.getInstance().getOpenfireJid(),
                member.Uid.concat(Member.SUFFIX_APP), MsgInFo.TYPE_CHAT).moveToFirst())
            return;
        XmppMessage msg = new XmppMessage();

        msg.setType(MsgInFo.TYPE_HEADLINE);
//		msg.setExtGroupMemberUserName(localUserJid);
        //对应群聊时，发送人的nickname
//        msg.setExtGroupMemberUserNick(member.NickName);
        msg.setTo_(localUserJid);
        msg.setFrom_("应用推送@skysea.com");

        String LocalUserNickName = MyApplication.getInstance().getLocalMember().NickName;
        msg.setExtLocalDisplayName(member.NickName); // NickName对应display
        msg.setExtLocalUserName(localUserJid);

        msg.setExtRemoteUserName(member.UserName);//根据appid分类
        msg.setExtRemoteDisplayName(member.NickName);

//        msg.setMold(MsgInFo.MOLD_TEXT);

        msg.setDirect(MsgInFo.INOUT_IN);
        msg.setStatus(MsgInFo.STATUS_SUCCESS);
        msg.setReadStatus(MsgInFo.READ_TRUE);
        msg.setCreateTime(System.currentTimeMillis());

        String str = "{\n" +
                "    \"baseRequest\": {\n" +
                "        \"uid\": null,\n" +
                "        \"deviceID\": null,\n" +
                "        \"token\": \"sw_token\"\n" +
                "    },\n" +
                "    \"content\": \"我新启了【  刘学是个超级帅哥5】\",\n" +
                "    \"msgType\": 102,//可以根据这个来分类消息\n" +
                "    \"statusId\":\"999\",\n" +
                "    \"toUserNames\": [\n" +
                "        \"15AADCDFB8A24F91895C3190C8E82C99@user\"\n" +
                "    ],\n" +
                "    \"objectContent\": {\n" +
                "                        \"body\": [\n" +
                "                            {\n" +
                "                                \"content\": \"发送人    ： 总文书（刘路）\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"content\": \"发送部门：保密文档科\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"content\": \"阶段名称：编号登记\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"content\": \"文件类型：公司行政收文\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"content\": \"紧急程度：加急\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"content\": \"待办类型：待阅\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "        \"appId\": \"2\",\n" +
                "        \"operations\": [\n" +
                "            {\n" +
                "                \"content\": \"查看详细\",\n" +
                "                \"operationType\": \"1\",\n" +
                "                \"action\": \"http://10.180.120.157:8089/appyixin/phone/sw/144025ECBB6D0368E0530A6F0ACC3027?workItemId=152707816&processId=8a34f8114c6f3899014cd9f935e71f97&security=0&emergencyLevel=9\"\n" +
                "            },\n" +
                "                        {\n" +
                "                \"content\": \"查看详细2\",\n" +
                "                \"operationType\": \"1\",\n" +
                "                \"action\": \"http://10.180.120.157:8089/appyixin/phone/sw/144025ECBB6D0368E0530A6F0ACC3027?workItemId=152707816&processId=8a34f8114c6f3899014cd9f935e71f97&security=0&emergencyLevel=9\"\n" +
                "            },\n" +
                "             {\n" +
                "                \"content\": \"查看详细3\",\n" +
                "                \"operationType\": \"1\",\n" +
                "                \"action\": \"http://10.180.120.157:8089/appyixin/phone/sw/144025ECBB6D0368E0530A6F0ACC3027?workItemId=152707816&processId=8a34f8114c6f3899014cd9f935e71f97&security=0&emergencyLevel=9\"\n" +
                "            },    {\n" +
                "                \"content\": \"查看详细4\",\n" +
                "                \"operationType\": \"1\",\n" +
                "                \"action\": \"http://10.180.120.157:8089/appyixin/phone/sw/144025ECBB6D0368E0530A6F0ACC3027?workItemId=152707816&processId=8a34f8114c6f3899014cd9f935e71f97&security=0&emergencyLevel=9\"\n" +
                "            },\n" +
                "             {\n" +
                "                \"content\": \"查看详细5\",\n" +
                "                \"operationType\": \"1\",\n" +
                "                \"action\": \"http://10.180.120.157:8089/appyixin/phone/sw/144025ECBB6D0368E0530A6F0ACC3027?workItemId=152707816&processId=8a34f8114c6f3899014cd9f935e71f97&security=0&emergencyLevel=9\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"content\": \"查看详细6\",\n" +
                "                \"operationType\": \"1\",\n" +
                "                \"action\": \"http://10.180.120.157:8089/appyixin/phone/sw/144025ECBB6D0368E0530A6F0ACC3027?workItemId=152707816&processId=8a34f8114c6f3899014cd9f935e71f97&security=0&emergencyLevel=9\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"head\": {\n" +
                "            \"pubTime\": 1429586130000,\n" +
                "            \"content\": \"我新启了【  刘学是个超级帅哥5】\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"sessions\": [\n" +
                "        \"all\"\n" +
                "    ],\n" +
                "    \"expire\": 3600000\n" +
                "}\n" +
                "\n";

        AppPushEntity entity = new Gson().fromJson(str, AppPushEntity.class);

        msg.setPushToken(member.NickName);
        msg.setPushContent(member.Description);
        msg.setPushMsgType(entity.getMsgType());
        msg.setPushStatusId("9999");
        msg.setPushtoUserNames(localUserJid);
        msg.setPushObjectContentAppId(member.Uid);
        msg.setPushObjectContentBody(entity.getObjectContent().getBody().toString());
        msg.setPushObjectContentOperations(entity.getObjectContent().getOperations().toString());
        msg.setPushObjectContentPubTime(entity.getObjectContent().getHead().getPubTime());
        msg.setPushSessions("[all]");
        msg.setPushexpire(entity.getExpire());

        //设置
        msg.setBody(member.Description);
        msg.setCreateTime(entity.getObjectContent().getHead().getPubTime());
        msg.setMold(MsgInFo.MOLE_APP_101);//MsgInFo.MOLE_APP_102
//            msg.setCreateTime(me.getCreateTime());


        XmppDbManager.getInstance(getActivity()).insertMessageWithNotify(msg);
    }

    @Override
    public void onListItemLongClick(View view, int position) {
//        boolean bool = mListView.startDrag(position);
//        Toast.makeText(SimpleActivity.this, "onItemLongClick   position--->" + position + "   drag-->" + bool, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onListItemLongClick   " + position);
    }

    @Override
    public void onDragViewStart(int position) {
        Log.i(TAG, "onDragViewStart   " + position);
    }

    @Override
    public void onDragViewMoving(int position) {
//        Toast.makeText(DemoActivity.this, "onDragViewMoving   position--->" + position, Toast.LENGTH_SHORT).show();
        Log.i("yuyidong", "onDragViewMoving   " + position);
    }

    @Override
    public void onDragViewDown(int position) {
        Log.i(TAG, "onDragViewDown   " + position);
    }

    @Override
    public void onListItemClick(View v, int position) {

    }

    public static boolean isOpenMenu;

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        Log.i(TAG, "onSlideOpen   " + position);
        isOpenMenu = true;
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        Log.i(TAG, "onSlideClose   " + position);
        isOpenMenu = false;
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        ViewGroup view = (ViewGroup) mListView.getChildAt(itemPosition);
                        for (int i = 0; i < view.getChildCount(); i++) {
                            if (view.getChildAt(i) instanceof AppRecentChatItemView) {
                                AppRecentChatItemView itemView = (AppRecentChatItemView) view.getChildAt(i);
                                AppDetailActivity_.intent(this).remoteUserName(itemView.message.getExtRemoteUserName().split("@")[0].concat(Member.SUFFIX_APP))
                                        .nickName(itemView.message.getExtRemoteDisplayName())
                                        .follow("1")
                                        .startForResult(0x111);
                                break;
                            }
                        }
                        break;
                    case 1:

                        ViewGroup view2 = (ViewGroup) mListView.getChildAt(itemPosition);
                        for (int i = 0; i < view2.getChildCount(); i++) {
                            if (view2.getChildAt(i) instanceof AppRecentChatItemView) {
                                AppRecentChatItemView itemView = (AppRecentChatItemView) view2.getChildAt(i);
                                XmppDbManager.getInstance(getActivity()).updateRead(localUserJid, itemView.message.getExtRemoteUserName());
                                break;
                            }
                        }

                        break;
                }
        }
        return Menu.ITEM_SCROLL_BACK;
    }

    @Override
    public void onItemDelete(View view, int position) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_IDLE:
                break;
            case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_FLING:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }


}
