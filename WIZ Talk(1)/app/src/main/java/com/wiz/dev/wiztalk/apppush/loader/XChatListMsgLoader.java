package com.wiz.dev.wiztalk.apppush.loader;


import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;


/**
 * Created by Dong on 2015/10/13.
 */
public class XChatListMsgLoader extends CursorLoader {

    private Context mContext;

    private String localUserName;
    private String remoteUserName;
    private boolean isQunType;

    public XChatListMsgLoader(Context context,String localUserName,String remoteUserName,boolean isQunType) {
        super(context);
        this.mContext = context;
        this.localUserName=localUserName;
        this.remoteUserName = remoteUserName;
        this.isQunType = isQunType;
    }

    @Override
    public Cursor loadInBackground() {
        return XmppDbManager.getInstance(mContext).getChatList(localUserName,
                remoteUserName,isQunType? MsgInFo.TYPE_GROUPCHAT:MsgInFo.TYPE_CHAT);
    }
}
