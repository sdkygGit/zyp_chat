package com.wiz.dev.wiztalk.DB;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.utils.RefreshUtils;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.Utils;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Dong on 2015/10/29.
 */
public class XmppDbManager {

    private static final String TAG = "XmppDbManager";

    private static XmppDbManager mInstance;

    private Context mContext;
    private DaoSession mDaoSession;
    private   XmppMessageDao messageDao;

    RefreshUtils mRefreshUtils;

    private XmppDbManager(Context context) {
        this.mContext = context;
        this.mDaoSession = MyApplication.getDaoSession(context);
        this.messageDao = this.mDaoSession.getXmppMessageDao();
        mRefreshUtils = new RefreshUtils();
    }

    public static XmppDbManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new XmppDbManager(context);
        }

        return mInstance;
    }

    /**
     * 处理在线消息
     *
     * @param message
     */
    public XmppMessage onOnlineMessage(Message message) {
        L.d(TAG, " onOnlineMessage :" + message.toString());
        // TODO filter自己发的群聊消息
        if (filterGroupMessage(message)) {
            return null;
        }

        if (message.getBody().toString().equalsIgnoreCase(SaveConfig.UPDATA_TO_READ_PWD)) {
            XmppDbManager.getInstance(mContext).updateToReadStatus(message);
            return null;
        }

        XmppMessage xmppMessage = XmppDbMessage
                .retriveXmppMessageFromMessage(message);
        xmppMessage.setDirect(MsgInFo.INOUT_IN);
        xmppMessage.setReadStatus(MsgInFo.READ_FALSE);


        // insert
        long id = insertMessage(xmppMessage);
        if (id != -1) {
            mRefreshUtils.INeedNotify(mContext, XmppMessageContentProvider.CONTENT_URI);
            // 发出通知
            mContext.getContentResolver().notifyChange(
                    XmppMessageContentProvider.CONTENT_URI, null);
            L.d(TAG, "insertMessage() notifyChange");
            return xmppMessage;
        }
        return null;
    }

    public ArrayList<XmppMessage> onOfflineMessage(List<Message> pmsgs) {

        if (pmsgs != null && pmsgs.size() > 0) {
            boolean isUpdataFinish = false;
            for (int i = 0; i < pmsgs.size(); i++) {
                Message message = pmsgs.get(i);
                if (message.getBody().toString().equalsIgnoreCase(SaveConfig.UPDATA_TO_READ_PWD)) {
                    if (!isUpdataFinish) {
                        int set = XmppDbManager.getInstance(mContext).updateToReadStatus(pmsgs.remove(i));
                        isUpdataFinish = true;
                    } else {
                        pmsgs.remove(i);
                    }
                }
            }
        }


        L.d(TAG, "onOfflineMessage() messages.sizeBigger():" + pmsgs.size());
        String localUserJid = MyApplication.getInstance().getOpenfireJid();
        ArrayList<XmppMessage> ids = new ArrayList<XmppMessage>();

        for (Iterator<Message> it = pmsgs.iterator(); it.hasNext(); ) {
            Message pushMessage = it.next();
            L.d(TAG, "onOfflineMessage() pushMessage" + pushMessage);
            // updateMid(pushMessage);
            if (filterGroupMessage(pushMessage)) {
                continue;
            }


            XmppMessage xmppMessage = XmppDbMessage
                    .retriveXmppMessageFromMessage(pushMessage);

            xmppMessage.setDirect(MsgInFo.INOUT_IN);
            xmppMessage.setReadStatus(MsgInFo.READ_FALSE);

//			insertVoiceDb(xmppMessage);

            long id = insertMessageOffline(localUserJid, xmppMessage);
            L.d(TAG, "onOfflineMessage() id:" + id);

            if (id != 0) {
                ids.add(xmppMessage);
            }

        }
        L.d(TAG, " ids sizeBigger:" + ids.size());
        if (ids.size() != 0) {
            // TODO
            mRefreshUtils.INeedNotify(mContext, XmppMessageContentProvider.CONTENT_URI);

            // 发出通知
            mContext.getContentResolver().notifyChange(XmppMessageContentProvider.CONTENT_URI, null);
        }
        return ids;
    }

    /**
     * 是否需要过滤消息  true：要过滤
     *
     * @param pushMessage
     * @return
     */
    private boolean filterGroupMessage(Message pushMessage) {
        // TODO filter自己发的群聊消息
        if (pushMessage.getType().toString().equals(MsgInFo.TYPE_GROUPCHAT)) {
            L.d(TAG, " onOfflineMessage filter msg");
            // from="273@group.admin-pc/xujianxue
            String openfireJid = MyApplication.getInstance().getOpenfireJid();
            String barJid = openfireJid.substring(0, openfireJid.lastIndexOf("@"));

            String from = pushMessage.getFrom();
            if (from.contains("/")) {
                from = from.substring(from.lastIndexOf("/") + 1, from.length());
            }
            if (from.equalsIgnoreCase(barJid)) {
                return true;
            }
        }
        return false;
    }

    private long insertMessageOffline(String localUserName, XmppMessage message) {
        L.d(TAG, " insertMessageOffline() msg :" + message);
//		QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
//		qb.where(XmppMessageDao.Properties.From_.eq(localUserName),
//				XmppMessageDao.Properties.Direct.eq(MsgInFo.INOUT_IN)
//				/*,
//		 XmppMessageDao.Properties.Sid.notEq(message.getSid()) */);
//		if (qb.count() == 0) {
        messageDao.insertInTx(message);
        return 1;
//		}
//		L.d(TAG, " insertMessageOffline() msg  filter" );
//		return 0;
    }

    /**
     * 插入xmpp msg
     *
     * @param message
     * @return
     */
    public long insertMessage(XmppMessage message) {


        L.d(TAG, " insertMessage() msg :" + message.toString());
        long id = -1;
        try {
            id = messageDao.insert(message);
            L.d(TAG, " insertMessage() msg id:" + id);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    /**
     * 插入消息，且立即就有发出更新广播
     *
     * @param message
     */
    public long insertMessageWithNotify(XmppMessage message) {
        L.d(TAG, " insertMessageWithNotify() msg :" + message.toString());
        long id = -1;
        try {
            id = messageDao.insert(message);
            L.d(TAG, " insertMessage() msg id:" + id);
            if (id != -1) {
//                Uri uri = ContentUris
//                        .withAppendedId(XmppMessageContentProvider.CONTENT_URI,
//                                message.getId());
//                mContext.getContentResolver().notifyChange(uri, null);

                mRefreshUtils.INeedNotify(mContext, XmppMessageContentProvider.CONTENT_URI);
                // 发出通知
                mContext.getContentResolver().notifyChange(
                        XmppMessageContentProvider.CONTENT_URI, null);
                return id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;

    }

    /**
     * 因为前面加了2个字段
     */
    public static final int GET_CHATLIST_COLUMNS_OFFSET = 2;

    /**
     * 获取最近时间的聊天项
     *
     * @param localUserJid
     * @return
     */
    public Cursor getRecentChatList(String localUserJid) {
        L.d(TAG, " getRecentChatList() localUserJid :" + localUserJid);

        localUserJid = localUserJid.concat("%");

        String[] columns = new String[messageDao.getAllColumns().length
                + GET_CHATLIST_COLUMNS_OFFSET];
        columns[0] = "count("
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName + ")";
        columns[1] = "sum(" + XmppMessageDao.Properties.ReadStatus.columnName
                + "=" + MsgInFo.READ_FALSE + ")";
        System.arraycopy(messageDao.getAllColumns(), 0, columns,
                GET_CHATLIST_COLUMNS_OFFSET, messageDao.getAllColumns().length);

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
        String groupBy = XmppMessageDao.Properties.ExtRemoteUserName.columnName;
        String orderBy = textColumn + " DESC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? ";
        // selection += " AND " + XmppMessageDao.Properties.Type.columnName +
        // " like "
        // + MsgInFo.TYPE_CHAT;
        selection += " AND " + XmppMessageDao.Properties.Type.columnName +
                "!= ?";
        String[] selectionArgs = {localUserJid, MsgInFo.TYPE_HEADLINE};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), columns, selection, selectionArgs,
                groupBy, null, orderBy);

        cursor.setNotificationUri(mContext.getContentResolver(),
                XmppMessageContentProvider.CONTENT_URI);
        return cursor;
    }

    /**
     * 获得应用推送的消息
     *
     * @param localUserJid, appid
     *                      相当于聊天的remoteusername
     * @return
     */
    public Cursor getAppRecentMessage(String localUserJid) {
        L.d(TAG, " getAppRecentMessage() localUserJid :" + localUserJid);

        localUserJid = localUserJid.concat("%");

        String[] columns = new String[messageDao.getAllColumns().length
                + GET_CHATLIST_COLUMNS_OFFSET];
        columns[0] = "count("
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName + ")";
        columns[1] = "sum(" + XmppMessageDao.Properties.ReadStatus.columnName
                + "=" + MsgInFo.READ_FALSE + ")";
        System.arraycopy(messageDao.getAllColumns(), 0, columns,
                GET_CHATLIST_COLUMNS_OFFSET, messageDao.getAllColumns().length);

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
        String groupBy = XmppMessageDao.Properties.ExtRemoteUserName.columnName;
        String orderBy = textColumn + " DESC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ?";
        selection += " AND " + XmppMessageDao.Properties.Type.columnName +
                " like ? ";
//				+ MsgInFo.TYPE_HEADLINE;
//		 selection += " AND " + XmppMessageDao.Properties.Type.columnName +
//		 "!="+ MsgInFo.TYPE_GROUP_OPERATE;
        String[] selectionArgs = {localUserJid, MsgInFo.TYPE_HEADLINE};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), columns, selection, selectionArgs,
                groupBy, null, orderBy);

        cursor.setNotificationUri(mContext.getContentResolver(),
                XmppMessageContentProvider.CONTENT_URI);
        return cursor;
    }

    /**
     * 获取群操作的信息
     *
     * @param localUserJid
     * @return
     */
    public Cursor getGroupTipsList(String localUserJid) {
        L.d(TAG, " getGroupTipsList() localUserJid :" + localUserJid);

        localUserJid = localUserJid.concat("%");

        String[] columns = new String[messageDao.getAllColumns().length];

        System.arraycopy(messageDao.getAllColumns(), 0, columns,
                0, messageDao.getAllColumns().length);

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
//		String groupBy = XmppMessageDao.Properties.To_.columnName;
        String orderBy = textColumn + " DESC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ?";
        // selection += " AND " + XmppMessageDao.Properties.Type.columnName +
        // " like "
        // + MsgInFo.TYPE_CHAT;
        selection += " AND " + XmppMessageDao.Properties.Type.columnName +
                " = ?";
        String[] selectionArgs = {localUserJid, MsgInFo.TYPE_GROUP_OPERATE};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), columns, selection, selectionArgs,
                null, null, orderBy);

        cursor.setNotificationUri(mContext.getContentResolver(),
                XmppMessageContentProvider.CONTENT_URI);
        return cursor;
    }

    /**
     * 获取未读聊天项,用做是否发出通知的
     *
     * @param localUserJid
     * @return
     */
    public Cursor getUnReadMessage(String localUserJid) {
        L.d(TAG, " getUnReadMessage() localUserName :" + localUserJid);
        localUserJid = localUserJid.concat("%");

        String[] columns = new String[messageDao.getAllColumns().length + GET_CHATLIST_COLUMNS_OFFSET];
        columns[0] = "count("
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName + ")";
        columns[1] = "sum(" + XmppMessageDao.Properties.ReadStatus.columnName
                + "=" + MsgInFo.READ_FALSE + ")";
        System.arraycopy(messageDao.getAllColumns(), 0, columns,
                GET_CHATLIST_COLUMNS_OFFSET, messageDao.getAllColumns().length);

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
        String groupBy = XmppMessageDao.Properties.ExtRemoteUserName.columnName;
        String orderBy = textColumn + " DESC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ?";
        selection += " AND " + XmppMessageDao.Properties.ReadStatus.columnName
                + "=" + MsgInFo.READ_FALSE;
        // + MsgInFo.TYPE_CHAT;
        // selection += " AND " + MessageDao.Properties.MsgType.columnName +
        // "!="
        // + Msg.MSG_TYPE_TIPS;
        String[] selectionArgs = {localUserJid};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), columns, selection, selectionArgs,
                groupBy, null, orderBy);

//		cursor.setNotificationUri(mContext.getContentResolver(),
//				XmppMessageContentProvider.CONTENT_URI);
        return cursor;
    }

    /**
     * 获取未读信息 数，用做主页消息 角标  ，
     *
     * @param localUserName
     * @return
     */
    public long getChatListUnreadCount(String localUserName) {
        QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
        qb.where(XmppMessageDao.Properties.Direct.eq(MsgInFo.INOUT_IN),
                XmppMessageDao.Properties.ExtLocalUserName.like(localUserName),
                XmppMessageDao.Properties.ReadStatus.eq(MsgInFo.READ_FALSE),
                XmppMessageDao.Properties.Type.notEq(MsgInFo.TYPE_HEADLINE)/*,
                new WhereCondition.PropertyCondition(MessageDao.Properties.ExtRemoteUserName, " NOT LIKE ?", "%@app")*/);
        return qb.count();
    }

    /**
     * 获取未读信息 数，用做主页消息 角标  ，
     *
     * @param localUserName
     * @return
     */
    public long getAppPushChatListUnreadCount(String localUserName) {
        QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
        qb.where(XmppMessageDao.Properties.Direct.eq(MsgInFo.INOUT_IN),
                XmppMessageDao.Properties.ExtLocalUserName.like(localUserName),
                XmppMessageDao.Properties.ReadStatus.eq(MsgInFo.READ_FALSE),
                XmppMessageDao.Properties.Type.eq(MsgInFo.TYPE_HEADLINE)/*,
                new WhereCondition.PropertyCondition(MessageDao.Properties.ExtRemoteUserName, " NOT LIKE ?", "%@app")*/);
        return qb.count();
    }

    /**
     * 得到某个 群 消息的remotedisplayname
     *
     * @param localUserJid
     * @param remoteUserJid
     * @return
     */
    public String getChatDisplayName(String localUserJid, String remoteUserJid) {
        localUserJid = localUserJid.concat("%");
        remoteUserJid = remoteUserJid.concat("%");
        QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
        qb.where(XmppMessageDao.Properties.ExtLocalUserName.like(localUserJid),
                XmppMessageDao.Properties.ExtRemoteUserName.like(remoteUserJid),
                XmppMessageDao.Properties.Type.eq(MsgInFo.TYPE_GROUPCHAT));
        qb.orderCustom(XmppMessageDao.Properties.CreateTime, "DESC");
        qb.limit(1);
        XmppMessage message = qb.unique();
        if (message != null) {
            return message.getExtRemoteDisplayName();
        }
        return null;
    }

    /**
     * update the readStatus property of message with someone
     *
     * @param localUserJid
     * @param remoteUserJid
     * @return
     */
    public int updateRead(String localUserJid, String remoteUserJid) {
        L.d(TAG, " updateread()");
        remoteUserJid = remoteUserJid.concat("%");
        localUserJid = localUserJid.concat("%");
        L.d(TAG, "  updateread() remoteUserName:" + remoteUserJid);
        L.d(TAG, "  updateread() localUserName:" + localUserJid);

        String whereClause = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? AND ";

        whereClause += XmppMessageDao.Properties.Direct.columnName + "=? ";
        whereClause += " AND "
                + XmppMessageDao.Properties.ReadStatus.columnName + "=? ";

        String[] whereArgs = new String[]{localUserJid, remoteUserJid,
                String.valueOf(MsgInFo.INOUT_IN),
                String.valueOf(MsgInFo.READ_FALSE)};

        ContentValues values = new ContentValues();

        values.put(XmppMessageDao.Properties.ReadStatus.columnName,
                MsgInFo.READ_TRUE);
        values.put(XmppMessageDao.Properties.ToReadStatus.columnName,
                MsgInFo.READ_TRUE);

        int num = messageDao.getDatabase().update(messageDao.getTablename(),
                values, whereClause, whereArgs);
        if (num > 0) {
            mContext.getContentResolver().notifyChange(
                    XmppMessageContentProvider.CONTENT_URI, null);


            XmppMessage msg = new XmppMessage();
//        //设置为聊天类型
            msg.setType(MsgInFo.TYPE_CHAT);
            msg.setTo_(remoteUserJid);
            msg.setFrom_(localUserJid);
            msg.setExtLocalDisplayName(MyApplication.getInstance().getLocalMember().NickName); // NickName对应display
            msg.setExtLocalUserName(localUserJid);
            msg.setExtRemoteUserName(remoteUserJid);
            msg.setExtRemoteDisplayName(remoteUserJid);
            msg.setBody(SaveConfig.UPDATA_TO_READ_PWD);
            msg.setMold(MsgInFo.MOLD_TEXT);
            msg.setDirect(MsgInFo.INOUT_OUT);
            msg.setStatus(MsgInFo.STATUS_PENDING);
            msg.setReadStatus(MsgInFo.READ_TRUE);
            msg.setCreateTime(System.currentTimeMillis());
            MsgService.getMsgWriter(mContext).sendMsg(msg);
        }


        return num;
    }



    /**
     * update the readStatus property of message with someone
     *
     * @param localUserJid
     * @param remoteUserJid
     * @return
     */
    public int updateReadX(String localUserJid, String remoteUserJid,String nickName,boolean isChat,XchatActivity xchatActivity) {
        L.d(TAG, " updateread()");
        remoteUserJid = remoteUserJid.concat("%");
        localUserJid = localUserJid.concat("%");
        L.d(TAG, "  updateread() remoteUserName:" + remoteUserJid);
        L.d(TAG, "  updateread() localUserName:" + localUserJid);

        String whereClause = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? AND ";

        whereClause += XmppMessageDao.Properties.Direct.columnName + "=? ";
        whereClause += " AND "
                + XmppMessageDao.Properties.ReadStatus.columnName + "=? ";

        String[] whereArgs = new String[]{localUserJid, remoteUserJid,
                String.valueOf(MsgInFo.INOUT_IN),
                String.valueOf(MsgInFo.READ_FALSE)};

        ContentValues values = new ContentValues();

        values.put(XmppMessageDao.Properties.ReadStatus.columnName,
                MsgInFo.READ_TRUE);
        values.put(XmppMessageDao.Properties.ToReadStatus.columnName,
                MsgInFo.READ_TRUE);

        int num = messageDao.getDatabase().update(messageDao.getTablename(),
                values, whereClause, whereArgs);
        if (num > 0 ) {
//            mContext.getContentResolver().notifyChange(
//                    XmppMessageContentProvider.CONTENT_URI, null);
            if(isChat)
                xchatActivity.sendTextMsg(SaveConfig.UPDATA_TO_READ_PWD);
        }

        return num;
    }



    /**
     * update the readStatus property of message with someone
     *
     * @return
     */
    public int updateToReadStatus(Message msg) {

        String form = msg.getFrom();
        if(form.contains("/")){
            form = form.substring(0, form.lastIndexOf("/"));
        }
        String remoteUserJid = form.concat("%");
        String localUserJid = MyApplication.getInstance().getOpenfireJid().concat("%");
        L.d(TAG, "  updateread() remoteUserName:" + remoteUserJid);
        L.d(TAG, "  updateread() localUserName:" + localUserJid);

        String whereClause = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? AND ";

        whereClause += XmppMessageDao.Properties.Direct.columnName + "=? ";
        whereClause += " AND "
                + XmppMessageDao.Properties.ToReadStatus.columnName + "=? ";

        String[] whereArgs = new String[]{localUserJid, remoteUserJid,
                String.valueOf(MsgInFo.INOUT_OUT),
                String.valueOf(MsgInFo.READ_FALSE)};

        ContentValues values = new ContentValues();
        values.put(XmppMessageDao.Properties.ToReadStatus.columnName,
                MsgInFo.READ_TRUE);

        int num = messageDao.getDatabase().update(messageDao.getTablename(),
                values, whereClause, whereArgs);
        if (num > 0) {
            mContext.getContentResolver().notifyChange(
                    XmppMessageContentProvider.CONTENT_URI, null);
        }
        return num;
    }


    public Cursor queryAllToReadStatus(String localUserJid, String remoteUserJid) {

        remoteUserJid = remoteUserJid.concat("%");
        localUserJid = localUserJid.concat("%");
        L.d(TAG, "  updateread() remoteUserName:" + remoteUserJid);
        L.d(TAG, "  updateread() localUserName:" + localUserJid);

        String whereClause = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? AND ";

        whereClause += XmppMessageDao.Properties.Direct.columnName + "=? ";
        whereClause += " AND "
                + XmppMessageDao.Properties.ToReadStatus.columnName + "=? ";

        String[] whereArgs = new String[]{localUserJid, remoteUserJid,
                String.valueOf(MsgInFo.INOUT_IN),
                String.valueOf(MsgInFo.READ_FALSE)};

        return messageDao.getDatabase().query(messageDao.getTablename(), null, whereClause, whereArgs, null, null, null);
    }


    /**
     * update the readStatus property of message with someone
     *
     * @return
     */
    public void queryToReadStatusAndSendNotify(XmppMessage xMessage) {
        XmppMessage msg = new XmppMessage();
        String localUserJid = MyApplication.getInstance().getOpenfireJid();
        String jid = xMessage.getExtRemoteUserName().split("@")[0];
        jid = jid.toLowerCase();
        jid = jid.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
        //设置为聊天类型
        msg.setType(MsgInFo.TYPE_CHAT);
        msg.setTo_(jid);
        msg.setFrom_(localUserJid);
        msg.setExtLocalDisplayName(MyApplication.getInstance().getLocalMember().NickName); // NickName对应display
        msg.setExtLocalUserName(localUserJid);
        msg.setExtRemoteUserName(jid);
        msg.setExtRemoteDisplayName(xMessage.getExtRemoteDisplayName());
        msg.setBody(xMessage.getSid());
        msg.setMold(MsgInFo.MOLD_TEXT);
        msg.setDirect(MsgInFo.INOUT_OUT);
        msg.setStatus(MsgInFo.STATUS_PENDING);
        msg.setReadStatus(MsgInFo.READ_TRUE);
        msg.setCreateTime(System.currentTimeMillis());
        MsgService.getMsgWriter(mContext).sendMsg(msg);

//        String whereClause = XmppMessageDao.Properties.Sid.columnName + "=? ";
//        String[] whereArgs = new String[]{xMessage.getSid()};
//
//        ContentValues values = new ContentValues();
//        values.put(XmppMessageDao.Properties.ToReadStatus.columnName,
//                MsgInFo.READ_TRUE);
//
//        int num = messageDao.getDatabase().update(messageDao.getTablename(),
//                values, whereClause, whereArgs);
//        if (num > 0) {
//            mContext.getContentResolver().notifyChange(
//                    XmppMessageContentProvider.CONTENT_URI, null);
//        }

        xMessage.setToReadStatus(MsgInFo.READ_TRUE);
        messageDao.update(xMessage);
    }

    /**
     * 更新了群的昵称之后需要将 之前的消息的昵称也更改一下。
     *
     * @param remoteUserJid 形容：xx@group.skysea.com
     * @param topic         群的新昵称
     * @return
     */
    public int updateQunTopic(String remoteUserJid, String topic) {
        L.d(TAG, " updateQunTopic()");
        L.d(TAG, " updateQunTopic()  groupJid:" + remoteUserJid);
        ContentValues values = new ContentValues();
        //因为，这个字段被借去显示 groupNickname了，extReomteDisplayname,显示了群通知
        values.put(XmppMessageDao.Properties.ExtRemoteDisplayName.columnName, topic);

        String whereClause = XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ?";
//				+ XmppMessageDao.Properties.Type.columnName
//				+ "=?" ;
//				+ "=? AND " + MessageDao.Properties.ExtRead.columnName + "=?";
        String[] whereArgs = new String[]{remoteUserJid};

        int num = messageDao.getDatabase().update(messageDao.getTablename(),
                values, whereClause, whereArgs);

        mContext.getContentResolver().notifyChange(
                XmppMessageContentProvider.CONTENT_URI, null);

        return num;
    }

    /**
     * 获取和某一个人聊天项
     *
     * @param localUserJid
     * @param remoteUserJid
     * @param type          聊天项的类型
     */
    public Cursor getChatList(String localUserJid, String remoteUserJid,
                              String type) {
        L.d(TAG, " getChatList() ");

        remoteUserJid = remoteUserJid.concat("%");
        localUserJid = localUserJid.concat("%");
        L.d(TAG, "  getChatList() localUserJid:" + localUserJid);
        L.d(TAG, "  getChatList() remoteUserJid:" + remoteUserJid);

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
        String orderBy = textColumn + " ASC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? ";
//				+ XmppMessageDao.Properties.Type.columnName
//				+ "= ? ";
//				+XmppMessageDao.Properties.Type.columnName
//				+" = ? ";
//		todo 注释了上面的条件
        String[] selectionArgs = {localUserJid, remoteUserJid/*,type*/};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), messageDao.getAllColumns(),
                selection, selectionArgs, null, null, orderBy);

        cursor.setNotificationUri(mContext.getContentResolver(),
                XmppMessageContentProvider.CONTENT_URI);
        return cursor;
    }

    /**
     * 获取某一个应用的消息
     *
     * @param localUserJid
     * @param appId        应用的id
     *                     聊天项的类型
     */
    public Cursor getAppPushChatList(String localUserJid, String appId) {
        L.d(TAG, " getAppPushChatList() ");

        localUserJid = localUserJid.concat("%");
        L.d(TAG, "  getAppPushChatList() localUserJid:" + localUserJid);
        L.d(TAG, "  getAppPushChatList() appId:" + appId);

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
        String orderBy = textColumn + " ASC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
//				+ XmppMessageDao.Properties.ExtRemoteUserName.columnName
//				+ " like ? AND "
                + XmppMessageDao.Properties.PushObjectContentAppId.columnName
                + " = ?";
        String[] selectionArgs = {localUserJid, appId};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), messageDao.getAllColumns(),
                selection, selectionArgs, null, null, orderBy);

        cursor.setNotificationUri(mContext.getContentResolver(),
                XmppMessageContentProvider.CONTENT_URI);
        return cursor;
    }

    public int deleteChat(String localUserJid, String remoteUserJid) {

        if (TextUtils.isEmpty(remoteUserJid)
                || TextUtils.isEmpty(remoteUserJid)) {
            return -1;
        }

        remoteUserJid = remoteUserJid.concat("%");
        localUserJid = localUserJid.concat("%");

        L.d(TAG, "  deleteChat() remoteUserJid:" + remoteUserJid);
        L.d(TAG, "  deleteChat() localUserJid:" + localUserJid);

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? ";
//		 selection += XmppMessageDao.Properties.Type.columnName +
//		 " != ? "   ;

        String[] selectionArgs = {localUserJid, remoteUserJid /*,MsgInFo.TYPE_GROUP_OPERATE*/};

        int num = messageDao.getDatabase().delete(messageDao.getTablename(),
                selection, selectionArgs);

        mContext.getContentResolver().notifyChange(
                XmppMessageContentProvider.CONTENT_URI, null);

        return num;
    }


    /**
     * 获取聊天图片信息
     *
     * @param localUserJid
     * @param remoteUserJid
     * @return
     */
    public Cursor getChatMessagesInThePictures(String localUserJid,
                                               String remoteUserJid) {
        remoteUserJid = remoteUserJid.concat("%");
        localUserJid = localUserJid.concat("%");

        String textColumn = XmppMessageDao.Properties.CreateTime.columnName;
        String orderBy = textColumn + " ASC";

        String selection = XmppMessageDao.Properties.ExtLocalUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.ExtRemoteUserName.columnName
                + " like ? AND "
                + XmppMessageDao.Properties.Mold.columnName
                + "=" + String.valueOf(MsgInFo.MOLD_IMG);
        String[] selectionArgs = {localUserJid, remoteUserJid};

        Cursor cursor = messageDao.getDatabase().query(
                messageDao.getTablename(), messageDao.getAllColumns(),
                selection, selectionArgs, null, null, orderBy);

        return cursor;
    }

    /**
     * 删除某一个信息
     *
     * @param message
     */
    public void deleteMessage(XmppMessage message) {

        messageDao.delete(message);

        mContext.getContentResolver().notifyChange(
                XmppMessageContentProvider.CONTENT_URI, null);
    }

    public XmppMessage getXmppMessageBySid(@NonNull String stanzaId) {
        L.d(TAG, "getLastMid()");
        L.d(TAG, "getLastMid() sid:" + stanzaId);
        QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
        // qb.where(XmppMessageDao.Properties.From_.eq(localUserName)
        // , XmppMessageDao.Properties.Sid.eq(stanzaId));
        qb.where(XmppMessageDao.Properties.Sid.eq(stanzaId));
        qb.limit(1);
        return qb.unique();
    }

    public List<XmppMessage> getMessagesVoiceNotDownloadSuccess(
            String localUserName) {
        QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
        qb.where(
                XmppMessageDao.Properties.ExtLocalUserName.like(localUserName),
                XmppMessageDao.Properties.Mold.eq(MsgInFo.MOLD_VOICE),
                XmppMessageDao.Properties.Direct.eq(MsgInFo.INOUT_IN),
                XmppMessageDao.Properties.Status
                        .notEq(MsgInFo.STATUS_MEDIA_DOWNLOAD_SUCCESS));
        qb.orderCustom(XmppMessageDao.Properties.CreateTime, "DESC");
        return qb.list();
    }

    public void updateWithNotify(XmppMessage message) {
        messageDao.update(message);

        // 发出通知
        Uri uri = ContentUris.withAppendedId(
                XmppMessageContentProvider.CONTENT_URI, message.getId());
        mContext.getContentResolver().notifyChange(uri, null);
    }

    public void notification(Context context) {
        // if (Utils.isSouldNotification(context)) {
        String localUserJid = MyApplication.getInstance().getOpenfireJid();
        L.d(TAG, "notification :localUserJid:" + localUserJid);

        Cursor cursor = XmppDbManager.getInstance(context).getUnReadMessage(
                localUserJid);
        int count = cursor.getCount();

        DaoSession daoSession = MyApplication.getDaoSession(context);
        XmppMessageDao messageDao = daoSession.getXmppMessageDao();

        if (count > 0) {
            int contactCount = 0;
            int messageCount = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int c0 = cursor.getInt(0); //2
                int c1 = cursor.getInt(1); //2
                int currentContactUnreadCount = cursor.getInt(1);
                if (currentContactUnreadCount > 0) {
                    contactCount += 1;
                }
                messageCount += currentContactUnreadCount;
                cursor.moveToNext();
            }
            cursor.moveToFirst();
            XmppMessage message = messageDao.readEntity(cursor,
                    GET_CHATLIST_COLUMNS_OFFSET);
            L.d(TAG,
                    "notification:getExtLocalUserName:" + message.getExtLocalUserName());
//							+ message.getExtRemoteUserName()
//							+ message.getExtRemoteDisplayName());
            L.d(TAG, "notification:getExtRemoteUserName:" + message.getExtRemoteUserName());
            L.d(TAG, "notification:getExtRemoteUserName:" + message.getExtRemoteUserName());

            if (Utils.isSouldNotification(context)) {
                if (contactCount == 1) {
                    // cursor.moveToFirst();
                    // Message message = messageDao.readEntity(cursor,
                    // DBManager.GET_CHATLIST_COLUMNS_OFFSET);

                    // Content content = Content.createContent(
                    // message.getExtRemoteUserName(), message.getBody());
                    String contentStr = null;
                    if (message.getType().equals(MsgInFo.TYPE_GROUPCHAT)) {
                        contentStr = "群消息";
                    } else if (message.getType().equals(MsgInFo.TYPE_HEADLINE)) {
                        contentStr = "应用通知";
                    } else {
                        contentStr = message.getBody();

                        if (contentStr.split("@").length == 2 && contentStr.split("@")[0].equals(ConstDefine.location_pwd))
                            contentStr = "[位置]";
                        else if(contentStr.split("#")[0].equals(SaveConfig.SHARE_PWD))
                            contentStr = "[分享]";
                    }

                    String title = message.getExtRemoteDisplayName();
                    if (messageCount > 1) {
                        title += "(" + messageCount + "条新消息)";
                    }
                    Utils.showNotification(context, title, contentStr);
                } else if (contactCount > 1) {
                    Utils.showNotification(context, contactCount + "个联系人",
                            messageCount + "条新消息");
                }
            } else if (messageCount > 0
                    && !message.getFrom_().contains(localUserJid)) {
                Utils.playRingtone(context);
            }
        }
        cursor.close();
    }

    /**
     * 删除remoteUsername 不是groupJid的数据
     *
     * @param groupJid
     */
    public void deleteOldGroupMsg(Object[] groupJid) {
        L.d(TAG, " deleteOldGroupMsg () groupJid:" + groupJid);
        /*String whereClause = XmppMessageDao.Properties.ExtRemoteUserName.columnName + " != ? AND "
                + XmppMessageDao.Properties.Type.columnName + " = ?";
        String[] whereArgs = {groupJid ,MsgInFo.TYPE_GROUPCHAT};

        messageDao.getDatabase().delete(messageDao.getTablename(), whereClause, whereArgs);*/

        QueryBuilder<XmppMessage> qb = messageDao.queryBuilder();
        qb.where(
                XmppMessageDao.Properties.ExtRemoteUserName.notIn(groupJid),
                XmppMessageDao.Properties.Type.eq(MsgInFo.TYPE_GROUPCHAT));
        List<XmppMessage> results = qb.list();
        int counts = results.size();
        for (XmppMessage message : results) {
            messageDao.delete(message);
        }
        if (counts > 0) {
            mContext.getContentResolver().notifyChange(
                    XmppMessageContentProvider.CONTENT_URI, null);
        }
    }
}
