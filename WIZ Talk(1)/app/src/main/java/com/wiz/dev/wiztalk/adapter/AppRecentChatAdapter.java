package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.wiz.dev.wiztalk.DB.DaoSession;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageDao;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.view.AppRecentChatItemView;
import com.wiz.dev.wiztalk.view.AppRecentChatItemView_;
import com.wiz.dev.wiztalk.view.RecentChatItemView_;

import org.androidannotations.annotations.EBean;

/**
 * 最近聊天项的adapter
 * Created by Dong on 2015/10/13.
 */
@EBean
public class AppRecentChatAdapter extends CursorAdapter {

    private static final String TAG = AppRecentChatAdapter.class.getSimpleName();

    private DaoSession mDaoSession;
    private XmppMessageDao messageDao;
    Context context;

    public AppRecentChatAdapter(Context context) {
        super(context, null, true);
        this.context = context;
        mDaoSession = MyApplication.getDaoSession(context);
        messageDao = mDaoSession.getXmppMessageDao();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return AppRecentChatItemView_.build(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int unRead = cursor.getInt(1);
        AppRecentChatItemView itemView = (AppRecentChatItemView) view;
        XmppMessage msg = readEntity(cursor);
        itemView.bind(msg, unRead);

    }

    public XmppMessage readEntity(Cursor cursor) {
        //TODO
        XmppMessage entity = messageDao.readEntity(cursor, XmppDbManager
                .GET_CHATLIST_COLUMNS_OFFSET);
        return entity;
    }
}
