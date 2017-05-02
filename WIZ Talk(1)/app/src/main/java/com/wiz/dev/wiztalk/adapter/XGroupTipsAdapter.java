package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.wiz.dev.wiztalk.DB.DaoSession;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageDao;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.view.GroupOperateView;
import com.wiz.dev.wiztalk.view.GroupOperateView_;

import org.androidannotations.annotations.EBean;

/**
 * Created by Dong on 2015/10/13.
 */
@EBean
public class XGroupTipsAdapter extends CursorAdapter{

    private DaoSession mDaoSession;
    private XmppMessageDao messageDao;
    

    public XGroupTipsAdapter(Context context) {
        super(context, null,true);
        mDaoSession = MyApplication.getDaoSession(context);
        messageDao = mDaoSession.getXmppMessageDao();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return GroupOperateView_.build(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        XmppMessage msg = readEntity(cursor); 
        GroupOperateView itemView = (GroupOperateView)view;
        itemView.bind(msg);
    }

    public XmppMessage readEntity(Cursor cursor) {
        XmppMessage entity = messageDao.readEntity(cursor,0);
        return entity;
    }
}