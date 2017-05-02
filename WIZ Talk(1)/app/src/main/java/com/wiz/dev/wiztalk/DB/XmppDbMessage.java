package com.wiz.dev.wiztalk.DB;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.epic.traverse.push.model.Extension.ContentImageExtension;
import com.epic.traverse.push.model.Extension.ContentVoiceExtension;
import com.epic.traverse.push.model.Extension.MessageExtension;
import com.epic.traverse.push.util.L;
import com.google.gson.Gson;
import com.skysea.group.packet.MemberPacketExtension;
import com.skysea.group.packet.XPacket;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;

import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;


/**
 * 进行 openfire message 和 数据重存储的message的转换
 */
public class XmppDbMessage {

    private static final String TAG = "XmppDbMessage";

    public static final int INOUT_IN = 1;
    public static final int INOUT_OUT = 0;
    public static final int READ_TRUE = 1;
    public static final int READ_FALSE = 0;

    public static final int STATUS_MEDIA_UPLOAD_PENDING = 100;
    public static final int STATUS_MEDIA_UPLOAD_START = 101;
    public static final int STATUS_MEDIA_UPLOAD_CANCEL = 102;
    public static final int STATUS_MEDIA_UPLOAD_SUCCESS = 103;
    public static final int STATUS_MEDIA_UPLOAD_FAILURE = 104;
    public static final int STATUS_MEDIA_UPLOAD_LOADING = 105;

    public static final int STATUS_MEDIA_DOWNLOAD_PENDING = 150;
    public static final int STATUS_MEDIA_DOWNLOAD_START = 151;
    public static final int STATUS_MEDIA_DOWNLOAD_CANCEL = 152;
    public static final int STATUS_MEDIA_DOWNLOAD_SUCCESS = 153;
    public static final int STATUS_MEDIA_DOWNLOAD_FAILURE = 154;
    public static final int STATUS_MEDIA_DOWNLOAD_LOADING = 155;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SENDING = 2;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = -1;

    /**
     * 获取 MessageExtension
     *
     * @param message xmpp
     * @return
     */
    public static MessageExtension getMessageExtension(Message message) {
        MessageExtension extension = null;
        try {
            extension = message.getExtension(MessageExtension.NAME, MessageExtension
                    .NAME_SPACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extension;
    }

    /**
     * 获取 MessageExtension
     *
     * @param message xmpp
     * @return
     */
    public static XPacket getXpacketExtension(Message
                                                      message) {
        XPacket extension = null;
        try {
            // TODO: 2016/3/10
            extension = message.getExtension("x", "http://skysea.com/protocol/group#member");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return extension;
    }

    /**
     * 获取 MessageExtension 的mold
     *
     * @param message xmpp
     * @return
     */
    public static int getMoldForMessageExtension(Message
                                                         message) {
        MessageExtension messageExtension = getMessageExtension(message);
        if (messageExtension != null) {
            return messageExtension.getMold();
        }
        return -1;
    }

    /**
     * 获取 ContentImageExtension
     *
     * @param message xmpp
     * @return
     */
    public static ContentImageExtension getContentImageExtension(Message
                                                                         message) {
        ContentImageExtension extension = null;
        try {
            extension = message.getExtension(ContentImageExtension.NAME, ContentImageExtension
                    .NAME_SPACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extension;
    }


    /**
     * 获取 ContentVoiceExtension
     *
     * @param message xmpp
     * @return
     */
    public static ContentVoiceExtension getContentVoiceExtension(Message
                                                                         message) {
        ContentVoiceExtension extension = null;
        try {
            extension = message.getExtension(ContentVoiceExtension.NAME, ContentVoiceExtension
                    .NAME_SPACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extension;
    }

    /**
     * 获取 ContentVoiceExtension getVoiceLength
     *
     * @param message xmpp
     * @return
     */
    public static long getVoiceLengthForMessage(Message
                                                        message) {
        ContentVoiceExtension messageExtension = getContentVoiceExtension(message);
        if (messageExtension != null) {
            return messageExtension.getVoiceLength();
        }
        return -1;
    }

    /**
     * 获取 ContentVoiceExtension
     *
     * @param message xmpp
     * @return
     */
    public static MemberPacketExtension getMemberPacketExtension(Message
                                                                         message) {
        MemberPacketExtension extension = null;
        try {
            extension = message.getExtension(MemberPacketExtension.ELEMENT_NAME, MemberPacketExtension
                    .NAMESPACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extension;
    }

    /**
     * 获取 图片或语音的fileUrl
     *
     * @param message
     * @return
     */
    public static String getFilePathForMessage(Message message) {
        String filePath = null;
        int mold = getMoldForMessageExtension(message);
        if (mold == MsgInFo.MOLD_IMG) {
            ContentImageExtension contentImageExtension = getContentImageExtension(message);
            if (contentImageExtension != null) {
                filePath = contentImageExtension.getFilePath() + "&" + contentImageExtension.getFileExtention();
            }
        } else if (mold == MsgInFo.MOLD_VOICE) {
            ContentVoiceExtension contentVoiceExtension = getContentVoiceExtension(message);
            if (contentVoiceExtension != null) {
                filePath = contentVoiceExtension.getFilePath() + "&" + contentVoiceExtension.getFileExtention();
            }
        } else if (mold == MsgInFo.MOLD_MOVIE) {
            ContentVoiceExtension contentVoiceExtension = getContentVoiceExtension(message);
            if (contentVoiceExtension != null) {
                filePath = contentVoiceExtension.getFilePath() + "&" + contentVoiceExtension.getFileExtention();
            }
        } else if (mold == MsgInFo.MOLD_FILE) {
            ContentVoiceExtension contentVoiceExtension = getContentVoiceExtension(message);
            if (contentVoiceExtension != null) {
                filePath = contentVoiceExtension.getFilePath() + "&" + contentVoiceExtension.getFileExtention();
            }
        }
        return filePath;
    }


    public static long insertMsgMillisecond = 0;

    /**
     * 收到消息时，从 openfire的message对象得到 存储到数据的xmppmessage对象 ,不包含
     *
     * @param message
     * @return
     */
    public static XmppMessage retriveXmppMessageFromMessage(Message message) {

        XmppMessage xMsg = new XmppMessage();
        //not null // TODO: 2015/11/27
        xMsg.setSid(message.getStanzaId());
        xMsg.setTo_(message.getTo());
        xMsg.setFrom_(message.getFrom());
        xMsg.setType(message.getType().toString());
        xMsg.setDirect(MsgInFo.INOUT_IN);
        xMsg.setCreateTime(System.currentTimeMillis());
        xMsg.setShowTime(0L);
        xMsg.setRecVoiceReadStatus(MsgInFo.READ_FALSE);
        String to = message.getTo(); //去除资源符号
        if (message.getTo().contains("/")) {
            to = to.substring(0, to.lastIndexOf("/"));
        }
        xMsg.setExtLocalUserName(to);
        String form = message.getFrom();
        if (form.contains("/")) {
            form = form.substring(0, form.lastIndexOf("/"));
        }
        xMsg.setExtRemoteUserName(form);
        xMsg.setExtLocalDisplayName(MyApplication.getInstance().getLocalMember().NickName);

        if (!message.getType().toString().equalsIgnoreCase(MsgInFo.TYPE_HEADLINE)) {
            xMsg.setBody(message.getBody());
        }

        MessageExtension me = getMessageExtension(message);

        if (me != null) {
            xMsg.setMold(me.getMold());
            xMsg.setCreateTime(me.getCreateTime());
            xMsg.setShowTime(Long.valueOf(me.getSenderNickname()));

//			send msg:XmppMessage [id=93, sid=null, to_=122@group.admin-pc, from_=xujianxue@admin-pc, type=groupchat, 
//			body=UI, direct=0, createTime=1455438981612, mold=0, voiceLength=null, filePath=null, status=2, readStatus=1, 
//			extLocalUserName=xujianxue@admin-pc, extRemoteUserName=122@group.admin-pc,
//			extLocalDisplayName=group-1455438967377, extRemoteDisplayName=group-1455438967377]
            xMsg.setExtRemoteDisplayName(me.getDisplayName());

            if (me != null && (me.getMold() == MsgInFo.MOLD_IMG || me.getMold() == MsgInFo.MOLD_VOICE || me.getMold() == MsgInFo.MOLD_MOVIE || me.getMold() == MsgInFo.MOLD_FILE)) {
                xMsg.setFilePath(getFilePathForMessage(message));
                xMsg.setStatus(MsgInFo.STATUS_MEDIA_DOWNLOAD_PENDING);
            }

            ContentVoiceExtension contentVoiceExtension = getContentVoiceExtension(message);

            if (contentVoiceExtension != null &&
                    contentVoiceExtension.getVoiceLength() >= 0) {
                xMsg.setStatus(MsgInFo.STATUS_MEDIA_DOWNLOAD_PENDING);
                xMsg.setVoiceLength((int) contentVoiceExtension.getVoiceLength());
            }

//			TODO 对于群 displayname只有xujianxue，解析消息得到，然后通过extlocalUserDisplayname显示
            MemberPacketExtension memberPacketExtension = getMemberPacketExtension(message);
            if (memberPacketExtension != null &&
                    memberPacketExtension.getMemberInfo() != null && !TextUtils.isEmpty(memberPacketExtension.getMemberInfo().getNickname())) {
                xMsg.setExtLocalDisplayName(memberPacketExtension.getMemberInfo().getNickname());
            }
        } else {
            xMsg.setMold(MsgInFo.MOLD_TEXT);
            //TODO  对于时间的显示 delay节点
            xMsg.setCreateTime(System.currentTimeMillis());
        }
        //单独处理应用推送的消息
        if (message.getType().toString().equalsIgnoreCase(MsgInFo.TYPE_HEADLINE)) {
            String contentStr = message.getBody();

            try {
                contentStr = URLDecoder.decode(contentStr, Charset.forName("utf-8").name());
                L.d("Dong", contentStr);

                JSONObject jsonObj = new JSONObject(contentStr);

                String appStr = jsonObj.getString("app");

				/*JSONObject appObject = new JSONObject(appStr);
				String msgType = (String) appObject.get("msgType");*/

                AppPushEntity entity = new Gson().fromJson(appStr, AppPushEntity.class);

                String msgType = String.valueOf(entity.getMsgType());
                xMsg.setType(MsgInFo.TYPE_HEADLINE);

                if (msgType.equalsIgnoreCase(MsgInFo.APP_TYPE_102)) {

                    xMsg.setPushToken(entity.getBaseRequest().getToken());
                    xMsg.setPushContent(entity.getContent());
                    xMsg.setPushMsgType(entity.getMsgType());
                    xMsg.setPushStatusId(entity.getStatusId());
                    xMsg.setPushtoUserNames(entity.getToUserNames().toString());
                    xMsg.setPushObjectContentAppId(entity.getObjectContent().getAppId());
                    xMsg.setPushObjectContentBody(entity.getObjectContent().getBody().toString());
                    xMsg.setPushObjectContentOperations(entity.getObjectContent().getOperations().toString());
                    xMsg.setPushObjectContentPubTime(entity.getObjectContent().getHead().getPubTime());
                    xMsg.setPushSessions(entity.getSessions().toString());
                    xMsg.setPushexpire(entity.getExpire());
                    //设置
                    xMsg.setBody(entity.getContent());
                    xMsg.setCreateTime(entity.getObjectContent().getHead().getPubTime());
                    xMsg.setMold(entity.getMsgType());//MsgInFo.MOLE_APP_102
//            xMsg.setCreateTime(me.getCreateTime());

                    //根据token 设置显示名称 // TODO: 2016/4/7
                    xMsg.setExtRemoteDisplayName(entity.getBaseRequest().getToken());
                    // TODO: 2016/3/20 remoteusername 需要处理  暂时使用了appid 101,102来分类消息
                    xMsg.setExtRemoteUserName(String.valueOf(entity.getObjectContent().getAppId())
                            .concat(MsgInFo.APP_SUFIXX));
                } else if (msgType.equalsIgnoreCase(MsgInFo.APP_TYPE_103)) {//

                    xMsg.setPushToken(entity.getBaseRequest().getToken());
                    xMsg.setPushContent(entity.getContent());
                    xMsg.setPushMsgType(entity.getMsgType());
                    xMsg.setPushtoUserNames(entity.getToUserNames().toString());
                    xMsg.setPushObjectContentAppId(entity.getObjectContent().getAppId());
                    xMsg.setPushObjectContentPubTime(entity.getObjectContent().getHead().getPubTime());
                    xMsg.setPushSessions(entity.getSessions().toString());
                    xMsg.setPushexpire(entity.getExpire());
                    //设置
                    xMsg.setBody(entity.getContent());
                    xMsg.setCreateTime(entity.getObjectContent().getHead().getPubTime());
                    xMsg.setMold(entity.getMsgType());//MsgInFo.MOLE_APP_102
//            xMsg.setCreateTime(me.getCreateTime());

                    //根据token 设置显示名称 // TODO: 2016/4/7
                    xMsg.setExtRemoteDisplayName(entity.getBaseRequest().getToken());
                    // TODO: 2016/3/20 remoteusername 需要处理  暂时使用了appid 101,102来分类消息
                    xMsg.setExtRemoteUserName(String.valueOf(entity.getObjectContent().getAppId())
                            .concat(MsgInFo.APP_SUFIXX));
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return xMsg;
    }

    /**
     * true :是
     *
     * @param pushMessage
     * @return
     */
    public static boolean isGroupChatMessage(Message pushMessage) {
        if (pushMessage != null) {
            if (pushMessage.getType().toString().equals(MsgInFo.TYPE_GROUPCHAT))
                return true;
        }
        return false;
    }

    /**
     * true :in
     *
     * @param pushMessage Message
     * @return
     */
    private static boolean isMessageIn(Message pushMessage) {
        if (pushMessage != null) {
            String localJid = MyApplication.getInstance().getOpenfireJid();
            String from = pushMessage.getFrom();
            if (from.contains("/")) { //去除资源符号
                from = from.substring(0, from.lastIndexOf("/"));
            }
            if (from != null) {
                return !from.equals(localJid);
            }
        }
        return false;
    }

    /**
     * true :in
     *
     * @param message XmppMessage
     * @return
     */
    public static boolean isMessageIn(XmppMessage message) {
        if (message != null) {
            String localUserName = MyApplication.getInstance().getOpenfireJid();
            String from = message.getFrom_();
            if (message.getFrom_().contains("/")) {//群消息
                from = from.substring(0, from.lastIndexOf("/")).toLowerCase();
            }
            if (from != null) {
                return !from.equalsIgnoreCase(localUserName);
            }
        }
        return false;
    }

    /**
     * true :是
     *
     * @param pushMessage XmppMessage
     * @return
     */
    public static boolean isGroupChatMessage(XmppMessage pushMessage) {
        if (pushMessage != null) {
            if (pushMessage.getType().toString().equals(MsgInFo.TYPE_GROUPCHAT))
                return true;
        }
        return false;
    }

    /**
     * 从数据库xmppMessage对象得到openfire能够发送的message ，不包含上传的返回的信息，用于消息发送 出去
     *
     * @param xmppMessage
     * @return
     */
    public static Message retriveMessageFromXmppMessage(XmppMessage xmppMessage) {

        Message message = new Message();
        message.setFrom(xmppMessage.getFrom_());
        message.setTo(xmppMessage.getTo_());
        message.setBody(xmppMessage.getBody());
        if (xmppMessage.getType().equals(MsgInFo.TYPE_CHAT)) {
            message.setType(Message.Type.chat);
        }
        if (xmppMessage.getType().equals(MsgInFo.TYPE_GROUPCHAT)) {
            message.setType(Message.Type.groupchat);
        } else {
            message.setType(Message.Type.normal);
        }
        MessageExtension me = new MessageExtension();
        me.setMold(xmppMessage.getMold());
        me.setCreateTime(xmppMessage.getCreateTime());
        me.setShowTime(xmppMessage.getShowTime());
        if (xmppMessage.getType().equals(MsgInFo.TYPE_GROUPCHAT)) {
//			displayname 群昵称
            me.setDisplayName(xmppMessage.getExtRemoteDisplayName());//getExtRemoteDisplayName
            //群聊时，发送人的nickname
            me.setSenderNickname(xmppMessage.getExtLocalDisplayName());
        } else {
//			TODO to 出去   DisplayName 谁发的就显示谁 ,群做如上处理是为了显示方便
            me.setDisplayName(xmppMessage.getExtLocalDisplayName());
            me.setReceiverNickName(xmppMessage.getExtRemoteDisplayName());
        }

        message.addExtension(me);

        if (xmppMessage.getMold() == MsgInFo.MOLD_IMG) {
            ContentImageExtension cie = new ContentImageExtension();
            cie.setMold(xmppMessage.getMold());
            cie.setFilePath(xmppMessage.getFilePath().split("&")[0]);
            cie.setFileExtention(xmppMessage.getFilePath().split("&")[1]);
            message.addExtension(cie);
        }
        if (xmppMessage.getMold() == MsgInFo.MOLD_VOICE) {
            ContentVoiceExtension cve = new ContentVoiceExtension();
            cve.setMold(xmppMessage.getMold());
            cve.setVoiceLength(xmppMessage.getVoiceLength());
            cve.setFilePath(xmppMessage.getFilePath().split("&")[0]);
            cve.setFileExtention(xmppMessage.getFilePath().split("&")[1]);
            message.addExtension(cve);
        }

        if (xmppMessage.getMold() == MsgInFo.MOLD_MOVIE) {
            ContentVoiceExtension cve = new ContentVoiceExtension();
            cve.setMold(xmppMessage.getMold());
            cve.setVoiceLength(xmppMessage.getVoiceLength());
            cve.setFilePath(xmppMessage.getFilePath().split("&")[0]);
            cve.setFileExtention(xmppMessage.getFilePath().split("&")[1]);
            message.addExtension(cve);
        }
        if (xmppMessage.getMold() == MsgInFo.MOLD_FILE) {
            ContentVoiceExtension cve = new ContentVoiceExtension();
            cve.setMold(xmppMessage.getMold());
            cve.setVoiceLength(xmppMessage.getVoiceLength());
            cve.setFilePath(xmppMessage.getFilePath().split("&")[0]);
            cve.setFileExtention(xmppMessage.getFilePath().split("&")[1]);
            message.addExtension(cve);
        }
        return message;
    }

    /**
     * get  the remote username from message that is signal chat message or group message
     *
     * @param message
     * @return xxx@user
     */
    @NonNull
    public static String getRemoteUsername(XmppMessage message) {
        String username = null;
        if (message.getType().equalsIgnoreCase(MsgInFo.TYPE_CHAT)) {
            username = message.getExtRemoteUserName().split("@")[0];
            username = username.concat(Member.SUFFIX_USER);
        } else {
            //3@group.skysea.com/8a34f8c9491f3d8101493bb934bb34c2
            username = message.getFrom_();
            if (username.contains("/")) {
                username = username.substring(username.lastIndexOf("/") + 1, username.length
                        ()).toLowerCase();
                username = username.concat(Member.SUFFIX_USER);
            }
        }
        return username;
    }

}