package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.wiz.dev.wiztalk.DB.DaoSession;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.VoiceDb;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageDao;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.apppush.OnOperationClickListener;
import com.wiz.dev.wiztalk.apppush.view.ChatItemApp100View_;
import com.wiz.dev.wiztalk.apppush.view.ChatItemApp101View_;
import com.wiz.dev.wiztalk.apppush.view.ChatItemApp102View;
import com.wiz.dev.wiztalk.apppush.view.ChatItemApp102View_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveAppShare_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveDocShare_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveFile_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveImageView_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveLocation_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveMovieView_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveTextView_;
import com.wiz.dev.wiztalk.view.ChatItemReceiveVoiceView_;
import com.wiz.dev.wiztalk.view.ChatItemSendAppShare_;
import com.wiz.dev.wiztalk.view.ChatItemSendDocShare_;
import com.wiz.dev.wiztalk.view.ChatItemSendFile_;
import com.wiz.dev.wiztalk.view.ChatItemSendImageView_;
import com.wiz.dev.wiztalk.view.ChatItemSendLocation_;
import com.wiz.dev.wiztalk.view.ChatItemSendMovieView_;
import com.wiz.dev.wiztalk.view.ChatItemSendTextView_;
import com.wiz.dev.wiztalk.view.ChatItemSendVoiceView_;
import com.wiz.dev.wiztalk.view.ChatItemView;
import com.wiz.dev.wiztalk.view.XChatItemTipsView_;

/**
 * Created by Dong on 2015/10/13.
 */
public class XChatListAdapter extends CursorAdapter {

    private DaoSession mDaoSession;
    private XmppMessageDao messageDao;

    private XchatActivity mContext;

    public XChatListAdapter(XchatActivity context) {
        super(context, null, true);
        mContext = context;
        mDaoSession = MyApplication.getDaoSession(context);
        messageDao = mDaoSession.getXmppMessageDao();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int inOut = cursor.getInt(XmppMessageDao.Properties.Direct.ordinal);
        int mold = cursor.getInt(XmppMessageDao.Properties.Mold.ordinal);
        return createChatItem(inOut, mold, cursor);
    }

    public ChatItemView createChatItem(int outOrIn, int mold, Cursor cursor) {
        ChatItemView itemView = null;
        if (outOrIn == MsgInFo.INOUT_IN) {
            switch (mold) {
                case MsgInFo.MOLD_TEXT:
                    itemView = ChatItemReceiveTextView_.build(mContext);
                    break;
                case MsgInFo.MOLD_IMG:
                    itemView = ChatItemReceiveImageView_.build(mContext);
                    break;
                case MsgInFo.MOLD_VOICE:
                    itemView = ChatItemReceiveVoiceView_.build(mContext);
                    break;
                case MsgInFo.MOLD_TIPS:
                    itemView = XChatItemTipsView_.build(mContext);
                    break;
                case MsgInFo.MOLE_APP_101:
                    itemView = ChatItemApp101View_.build(mContext);
                    break;
                case MsgInFo.MOLE_APP_102:
                    ChatItemApp102View view = ChatItemApp102View_.build(mContext);
                    view.setOnOperationClickListener(mOnOperationClickListener);
                    itemView = view;
                    break;
                case MsgInFo.MOLE_APP_103:
                    itemView = ChatItemApp101View_.build(mContext);
                    break;
                case MsgInFo.MOLD_SHARE_APP:
                    itemView = ChatItemReceiveAppShare_.build(mContext);
                    break;

                case MsgInFo.MOLD_SHARE_DOC:
                    itemView = ChatItemReceiveDocShare_.build(mContext);
                    break;
                case MsgInFo.MOLD_LOCATION:
                    itemView = ChatItemReceiveLocation_.build(mContext);
                    break;
                case MsgInFo.MOLD_INIT_APP:
                    itemView = ChatItemApp100View_.build(mContext);
                    break;

                case MsgInFo.MOLD_MOVIE:
                    //小视频
                    itemView = ChatItemReceiveMovieView_.build(mContext);
                    break;
                case MsgInFo.MOLD_FILE:
                    //小视频
                    itemView = ChatItemReceiveFile_.build(mContext);
                    break;

            }
        } else {
            switch (mold) {
                case MsgInFo.MOLD_TEXT:
                    itemView = ChatItemSendTextView_.build(mContext);
                    break;
                case MsgInFo.MOLD_IMG:
                    itemView = ChatItemSendImageView_.build(mContext);
                    break;
                case MsgInFo.MOLD_VOICE:
                    itemView = ChatItemSendVoiceView_.build(mContext);
                    break;
                case MsgInFo.MOLD_TIPS:
                    itemView = XChatItemTipsView_.build(mContext);
                    break;

                case MsgInFo.MOLD_SHARE_APP:
                    itemView = ChatItemSendAppShare_.build(mContext);
                    break;

                case MsgInFo.MOLD_SHARE_DOC:
                    itemView = ChatItemSendDocShare_.build(mContext);
                    break;
                case MsgInFo.MOLD_LOCATION:
                    itemView = ChatItemSendLocation_.build(mContext);
                    break;

                case MsgInFo.MOLD_MOVIE:
                    //小视频
                    itemView = ChatItemSendMovieView_.build(mContext);
                    break;
                case MsgInFo.MOLD_FILE:
                    //小视频
                    itemView = ChatItemSendFile_.build(mContext);
                    break;
            }
        }

        return itemView;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        int inOut = cursor.getInt(XmppMessageDao.Properties.Direct.ordinal);
        int mold = cursor.getInt(XmppMessageDao.Properties.Mold.ordinal);
        return createItemViewType(inOut, mold);

    }

    //Integers must be in the range 0 to {@link #getViewTypeCount} - 1
    public static final int VIEW_TYPE_COUNT = 22;

    public static final int VIEW_TYPE_IN_NORMAL = 1;
    public static final int VIEW_TYPE_IN_VOICE = 2;
    public static final int VIEW_TYPE_IN_IMAGE = 3;
    public static final int VIEW_TYPE_OUT_IMAGE = 4;
    public static final int VIEW_TYPE_OUT_VOICE = 5;
    public static final int VIEW_TYPE_OUT_NORMAL = 0;
    public static final int VIEW_TYPE_IN_TIPS = 6;


    public static final int VIEW_TYPE_NULL = 7;
    public static final int VIEW_TYPE_IN_APP_101 = 8;
    public static final int VIEW_TYPE_IN_APP_102 = 9;
    public static final int VIEW_TYPE_IN_APP_103 = 10;

    public static final int VIEW_TYPE_IN_APP_SHARE = 11;
    public static final int VIEW_TYPE_IN_DOC_SHARE= 12;
    public static final int VIEW_TYPE_OUT_APP_SHARE = 13;
    public static final int VIEW_TYPE_OUT_DOC_SHARE = 14;

    public static final int VIEW_TYPE_OUT_LOCATION = 15;
    public static final int VIEW_TYPE_IN_LOCATION = 16;
    public static final int VIEW_TYPE_IN_INIT_APP = 17;

    public static final int VIEW_TYPE_OUT_MOVIE = 18;
    public static final int VIEW_TYPE_IN_MOVIE = 19;
    public static final int VIEW_TYPE_OUT_FILE = 20;
    public static final int VIEW_TYPE_IN_FILE = 21;

    public int createItemViewType(int inOut, int mold) {
        int viewType = -1;
        if (inOut == MsgInFo.INOUT_IN) {
            switch (mold) {
                case MsgInFo.MOLD_TEXT:
                    viewType = VIEW_TYPE_IN_NORMAL;
                    break;
                case MsgInFo.MOLD_IMG:
                    viewType = VIEW_TYPE_IN_IMAGE;
                    break;
                case MsgInFo.MOLD_VOICE:
                    viewType = VIEW_TYPE_IN_VOICE;
                    break;
                case MsgInFo.MOLD_TIPS:
                    viewType = VIEW_TYPE_IN_TIPS;
                    break;
                case MsgInFo.MOLE_APP_101:
                    viewType = VIEW_TYPE_IN_APP_101;
                    break;
                case MsgInFo.MOLE_APP_102:
                    viewType = VIEW_TYPE_IN_APP_102;
                    break;
                case MsgInFo.MOLE_APP_103:
                    viewType = VIEW_TYPE_IN_APP_103;
                    break;

                case MsgInFo.MOLD_SHARE_APP:
                    viewType = VIEW_TYPE_IN_APP_SHARE;
                    break;

                case MsgInFo.MOLD_SHARE_DOC:
                    viewType = VIEW_TYPE_IN_DOC_SHARE;
                    break;

                case MsgInFo.MOLD_LOCATION:
                    viewType = VIEW_TYPE_IN_LOCATION;
                    break;

                case MsgInFo.MOLD_INIT_APP:
                    viewType = VIEW_TYPE_IN_INIT_APP;
                    break;

                case MsgInFo.MOLD_MOVIE:

                    viewType = VIEW_TYPE_IN_MOVIE;
                    break;
                case MsgInFo.MOLD_FILE:

                    viewType = VIEW_TYPE_IN_FILE;
                    break;
            }
        } else {
            switch (mold) {
                case MsgInFo.MOLD_TEXT:
                    viewType = VIEW_TYPE_OUT_NORMAL;
                    break;
                case MsgInFo.MOLD_IMG:
                    viewType = VIEW_TYPE_OUT_IMAGE;
                    break;
                case MsgInFo.MOLD_VOICE:
                    viewType = VIEW_TYPE_OUT_VOICE;
                    break;
                case MsgInFo.MOLD_TIPS:
                    viewType = VIEW_TYPE_IN_TIPS;
                    break;

                case MsgInFo.MOLD_SHARE_APP:
                    viewType = VIEW_TYPE_OUT_APP_SHARE;
                    break;

                case MsgInFo.MOLD_SHARE_DOC:
                    viewType = VIEW_TYPE_OUT_DOC_SHARE;
                    break;
                case MsgInFo.MOLD_LOCATION:
                    viewType = VIEW_TYPE_OUT_LOCATION;
                    break;

                case MsgInFo.MOLD_MOVIE:

                    viewType = VIEW_TYPE_OUT_MOVIE;
                    break;
                case MsgInFo.MOLD_FILE:

                    viewType = VIEW_TYPE_OUT_FILE;
                    break;
            }
        }

        return viewType;
    }

    private OnOperationClickListener mOnOperationClickListener;

    public void setOnOperationClickListener(OnOperationClickListener l) {
        mOnOperationClickListener = l;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        XmppMessage msg = readEntity(cursor);
        ChatItemView itemView = (ChatItemView) view;
//        insertVoiceDb(msg);
        itemView.bind(msg);

    }

    public void updateItemView(ChatItemView view,XmppMessage message){
        if(view == null)
            return;
        view.bind(message);
    }


//    public static void insertVoiceDb(XmppMessage xmppMessage) {
//        if (xmppMessage != null && xmppMessage.getMold() == MsgInFo.MOLD_VOICE) {
//            SQLiteDatabase db = new VoiceDb(mContext).getWritableDatabase();
//            if (xmppMessage.getDirect() == MsgInFo.INOUT_IN) {
//                Cursor cursor = db.query("voice", null, "filePath=?", new String[]{xmppMessage.getFilePath()}, null, null, null);
//                if (!cursor.moveToFirst()) {
//                    try {
////                        db.execSQL("INSERT INTO voice('filePath','type') VALUES ('" + xmppMessage.getFilePath() + "','" + VoiceDb.REC_NO_READ + "');");
//                        db.execSQL("INSERT INTO voice('filePath','type','create_time') VALUES ('" + xmppMessage.getFilePath() + "','" + VoiceDb.REC_NO_READ + "','" + xmppMessage.getCreateTime() + "');");
//
//                    } catch (Exception e) {
//                    }
//                }
//                cursor.close();
//            }
//
//            db.close();
//        }
//    }

    public XmppMessage readEntity(Cursor cursor) {
        XmppMessage entity = messageDao.readEntity(cursor, 0);
        return entity;
    }
}