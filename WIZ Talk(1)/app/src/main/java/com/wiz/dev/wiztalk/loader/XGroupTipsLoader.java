package com.wiz.dev.wiztalk.loader;



import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.wiz.dev.wiztalk.DB.XmppDbManager;

/**
 * group tips
 * Created by Dong on 2015/10/13.
 */
public class XGroupTipsLoader extends CursorLoader {

    private String localUserJid ;
    private Context mContext;
    
    public XGroupTipsLoader(Context context,String _localUserJid) {
        super(context);
        this.mContext = context;
        this.localUserJid = _localUserJid;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = XmppDbManager.getInstance(mContext).getGroupTipsList(localUserJid);
        return cursor;
    }
}
