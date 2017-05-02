package com.wiz.dev.wiztalk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.AppDetailActivity_;
import com.wiz.dev.wiztalk.activity.PluginDetailActivity_;
import com.wiz.dev.wiztalk.activity.XchatActivity_;
import com.wiz.dev.wiztalk.adapter.RecentChatAdapter;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetApplicationListRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetApplicationListResponse;
import com.wiz.dev.wiztalk.loader.RecentChatMsgLoader;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/1/23.
 */

public class PluginFragment extends Fragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener, SlideAndDragListView.OnListScrollListener, RestErrorHandler, LoaderManager.LoaderCallbacks<Cursor>, IMainFragment {
    private static final String TAG = PluginFragment.class.getSimpleName();

    private Menu mMenu;
    public static List<Member> mAppList;

    private SlideAndDragListView<Member> mListView;

    Appmsgsrv8094 mIMInterfaceProxy;

    String localUserJid;

    private static final int loader_num = 0;

    List<Member> doInBack() {
        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());
        GetApplicationListRequest request = new GetApplicationListRequest();
        request.BaseRequest = baseRequest;
        Log.d(TAG, "doInBackground() request:" + request);
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

    Intent intent2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plugin, null);
        mListView = (SlideAndDragListView) view.findViewById(R.id.lv_edit);
//        initData();
        initMenu();
        initUiAndListener();

        IntentFilter filter = new IntentFilter();

        filter.addAction("msg");
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {


                if (mListView.getCount() > 0) {

                    //                for(int i=0;i<mListView.getCount();i++){
                    View view = mListView.getChildAt(mListView.getCount() - 1);
//                    TextView name = (TextView) view.findViewById(R.id.name);

//                    if(name.getText().toString().equals(intent.getStringExtra("name"))){
                    int unread = intent.getIntExtra("unread", 0);

                    if(unread >0){
                        TextView app_recent_unread = (TextView) view.findViewById(R.id.app_recent_unread);
                        app_recent_unread.setText(unread + "");
                    }

                    View rightLay = view.findViewById(R.id.asdlayout_right);
                    rightLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            XchatActivity_.intent(getActivity()).remoteUserName(intent.getStringExtra("getExtRemoteUserName"))
                                    .remoteUserNick(intent.getStringExtra("getExtRemoteDisplayName"))
                                    .chatType(MsgInFo.TYPE_CHAT).start();
                        }
                    });


//                    }


//                }

                } else {
                    intent2 = intent;
                }

            }
        }, filter);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, List<Member>>() {
            @Override
            protected List<Member> doInBackground(Void... params) {

                return doInBack();
            }

            @Override
            protected void onPostExecute(List<Member> members) {
                mAppList.clear();
                mAppList.addAll(members);
                mAdapter.notifyDataSetChanged();

            }
        }.execute();
    }

    public Drawable getDrawable(Context context, int res) {
        return context.getResources().getDrawable(res);
    }

    public void initMenu() {
        mMenu = new Menu(true, true);
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

    public void initUiAndListener() {
        mRecentChatAdapter = new RecentChatAdapter(getActivity());
        localUserJid = MyApplication.getInstance().getOpenfireJid();
        mAppList = new ArrayList<Member>();
        mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
        mIMInterfaceProxy.setRestErrorHandler(this);
        mListView.setMenu(mMenu);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnListItemLongClickListener(this);
        mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
        mListView.setOnListScrollListener(this);
        getLoaderManager().initLoader(loader_num, null, this);
    }

    private MyAdapter mAdapter;

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.plugin_item, null);
                cvh.desc = (TextView) convertView.findViewById(R.id.desc);
                cvh.leftLay = convertView.findViewById(R.id.asdlayout_left);
                cvh.name = (TextView) convertView.findViewById(R.id.name);
                cvh.rightLay = convertView.findViewById(R.id.asdlayout_right);
                cvh.unread = (TextView) convertView.findViewById(R.id.app_recent_unread);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            Member item = (Member) this.getItem(position);
            cvh.name.setText(item.NickName);
            cvh.desc.setText(item.Description);
            cvh.leftLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isOpenMenu) {
                        Intent i = new Intent();
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setClassName(getActivity(), "com.example.hekang.wizplant.activity.Activity_seach");
                        startActivity(i);
                    } else {
                        mListView.closeSlidedItem();
                        isOpenMenu = false;
                    }

                }
            });

//            if(intent2 != null){
//                cvh.rightLay.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!isOpenMenu){
//                            XchatActivity_.intent(getActivity()).remoteUserName(intent2.getStringExtra("getExtRemoteUserName"))
//                                    .remoteUserNick(intent2.getStringExtra("getExtRemoteDisplayName"))
//                                    .chatType(MsgInFo.TYPE_CHAT).start();
//                        }else{
//                            mListView.closeSlidedItem();
//                            isOpenMenu = false;
//                        }
//                    }
//                });
//                cvh.unread.setText(intent2.getIntExtra("unread", 0) + "");
//            }
            return convertView;
        }


        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView name;
            public TextView desc, unread;
            public View leftLay, rightLay;

        }

    }

    ;


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

    boolean isOpenMenu;

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
                        Member member = mAppList.get(itemPosition);
                        AppDetailActivity_.intent(this).remoteUserName(member.UserName).nickName(member.NickName)
                                .follow(member.Follow)
                                .startForResult(0x111);
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "标为已读", Toast.LENGTH_SHORT).show();
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

    RecentChatAdapter mRecentChatAdapter;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new AppRecentChatLoader(getActivity().getApplicationContext(), localUserJid);
        return new RecentChatMsgLoader(getActivity().getApplicationContext(), localUserJid);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mAdapter.changeCursor(data);
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
