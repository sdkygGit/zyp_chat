package com.wiz.dev.wiztalk.service.imp;

import com.epic.traverse.push.services.XmppService;
import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.skysea.group.Group;
import com.skysea.group.GroupService;
import com.skysea.group.MemberInfo;
import com.skysea.group.packet.NotifyPacketExtension;
import com.skysea.group.packet.notify.GroupDestroyedNotify;
import com.skysea.group.packet.notify.MemberApplyResultNotify;
import com.skysea.group.packet.notify.MemberApplyToJoinNotify;
import com.skysea.group.packet.notify.MemberExitedNotify;
import com.skysea.group.packet.notify.MemberInviteNotify;
import com.skysea.group.packet.notify.MemberJoinedNotify;
import com.skysea.group.packet.notify.MemberKickedNotify;
import com.skysea.group.packet.notify.Notify;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefineExtranet;
import com.wiz.dev.wiztalk.service.listener.ChatPushMessageFilter;
import com.wiz.dev.wiztalk.service.listener.ChatPushMessageListener;
import com.wiz.dev.wiztalk.utils.SaveConfig;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.HashMap;

/**
 * Created by Dong on 2015/11/24.
 */
public class XmppServiceImp extends XmppService {

    SaveConfig saveConfig;

    @Override
    protected String getLocalUserJid() {
        return MyApplication.getInstance().getOpenfireJid();
    }

    @Override
    protected String getLocalUserPwd() {
        return MyApplication.getInstance().getOpenfirePwd();
    }

    @Override
    protected String getLocalUserNick() {
        return MyApplication.getInstance().getLocalMember().NickName == null ? "Anonymous" :
                MyApplication
                        .getInstance().getLocalMember().NickName;
    }

    @Override
    protected String getLocalUserEmail() {
        return MyApplication.getInstance().getLocalMember().Email == null ? "Anonymous" :
                MyApplication
                        .getInstance().getLocalMember().Email;
    }

    @Override
    protected boolean isLogout() {
        return MyApplication.getInstance().isLogout();
    }

    @Override
    protected String getOpenfireHost() {
        if (saveConfig.getStringConfig("httpConfig").equals("true"))
            return OpenfireConstDefine.OPENFIRE_SERVER_HOST;
        else
            return OpenfireConstDefineExtranet.OPENFIRE_SERVER_HOST;
    }

    @Override
    protected int getOpenfirePort() {
        if (saveConfig.getStringConfig("httpConfig").equals("true"))
            return OpenfireConstDefine.OPENFIRE_SERVER_PORT;
        else
            return OpenfireConstDefineExtranet.OPENFIRE_SERVER_PORT;
    }

    @Override
    protected String getOpenfireServiceName() {
        return OpenfireConstDefine.OPENFIRE_SERVER_NAME;
    }

    @Override
    protected String getOpenfireResourceTag() {
        return OpenfireConstDefine.OPENFIRE_SERVER_RESOURCE_TAG;
//        return com.yxst.epic.unifyplatform.utils.Utils.getDeviceId(this);
    }

    @Override
    public void onCreate() {
        saveConfig = new SaveConfig(this);
        addPushMessageListener(new ChatPushMessageListener(this),
                new ChatPushMessageFilter());
        super.onCreate();
    }

    @Override
    public void dispatchCreate(String jid, DataForm createFrom) {
        L.d(TAG, "dispatchCreate groupJid :" + jid);
    }

    //还差
    public void dispatchInviteResult(String jid) {
    }

    @Override
    public void dispatchInvite(String jid, MemberInviteNotify notify) {
        L.d(TAG, "dispatchInvite groupJid :" + jid);
        String fromJid = notify.getFrom();
        if (fromJid.contains("@")) {
            fromJid = fromJid.substring(0, fromJid.lastIndexOf("@"));
        }
//		MemberInfo memberInfo = notify.getMembers().get(0);

        if (isMySelfSendMessage(fromJid)) {
            return;
        }

        try {
            //TODO 为了获得群成员的nickname
            GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            Group group = gService.getGroup(jid);

            DataForm dataInfo = group.getInfo();
            String fromNick = fromJid;
            for (DataForm.Item item : group.getMembers().getItems()) {
                HashMap<String, String> rows = mapFields(item);
                if (rows.get("username").equalsIgnoreCase(fromJid)) {
                    fromNick = rows.get("nickname");
                }
            }

            String groupNickname = dataInfo.getField("name").getValues().get(0);
            createGroupOperateMessageAndInsert(jid, groupNickname, "邀请你加群", MsgInFo.OPERATE_INVITE,
                    fromJid,
                    fromNick);

        } catch (Exception e) {
            e.printStackTrace();
            createGroupOperateMessageAndInsert(jid, jid, "邀请你加群", MsgInFo.OPERATE_INVITE, fromJid, fromJid);
        }
    }

    private boolean isMySelfSendMessage(String fromJid) {
        String barJid = MyApplication.getInstance().getOpenfireJid();
        barJid = barJid.substring(0, barJid.lastIndexOf("@"));
        if (barJid.equalsIgnoreCase(fromJid)) {
            return true;
        }
        return false;
    }


    @Override
    public void dispatchKicked(String jid, MemberKickedNotify notify) {
        L.d(TAG, "dispatchKicked groupJid :" + jid);
        String fromJid = notify.getFrom();
        if (fromJid.contains("@")) {
            fromJid = fromJid.substring(0, fromJid.lastIndexOf("@"));
        }
        MemberInfo memberInfo = notify.getMemberInfo();

        if (isMySelfSendMessage(fromJid)) {
            return;
        }

        String localJid = MyApplication.getInstance().getOpenfireJid();
        localJid = localJid.substring(0, localJid.lastIndexOf("@"));

        String remoteUserJid = jid.substring(jid.lastIndexOf("@") + 1, jid.length());

        if (localJid.equalsIgnoreCase(memberInfo.getUserName())) {
            try {
                //TODO 为了获得群成员的昵称
                GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
                Group group = gService.getGroup(jid);

                DataForm dataInfo = group.getInfo();
                String fromNick = fromJid;
                for (DataForm.Item item : group.getMembers().getItems()) {
                    HashMap<String, String> rows = mapFields(item);
                    if (rows.get("username").equalsIgnoreCase(fromJid)) {
                        fromNick = rows.get("nickname");
                    }
                }

                String groupNickname = dataInfo.getField("name").getValues().get(0);
                createGroupOperateMessageAndInsert(jid, groupNickname, "已将你移除群", MsgInFo
                        .OPERATE_TIPS, fromJid, fromNick);

            } catch (Exception e) {
                e.printStackTrace();
                createGroupOperateMessageAndInsert(jid, jid, "已将你移除群", MsgInFo.OPERATE_TIPS, fromJid, fromJid);
            } finally {
                XmppDbManager.getInstance(getApplicationContext()).deleteChat(MyApplication.getInstance().getOpenfireJid(),
                        jid);
            }
        } else {
            XmppMessage xmsg = null;
            try {
                GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
                Group group = gService.getGroup(jid);
                DataForm dataInfo = group.getInfo();
                String groupNickname = dataInfo.getField("name").getValues().get(0);
                xmsg = createTipsMessage(jid, groupNickname, " " + memberInfo.getNickname() + " 被管理员移出本群！");

            } catch (Exception e) {
                e.printStackTrace();
                xmsg = createTipsMessage(jid, jid, " " + memberInfo.getNickname() + " 被管理员移出本群！");
            } finally {
                XmppDbManager.getInstance(getApplicationContext()).insertMessageWithNotify(xmsg);
            }
        }
    }

    @Override
    public void dispatchExited(String jid, MemberExitedNotify notify) {
        String reason = notify.getReason();
        MemberInfo memberInfo = notify.getMemberInfo();

        if (isMySelfSendMessage(memberInfo.getUserName())) {
            //自己退出群
            XmppDbManager.getInstance(getApplicationContext()).deleteChat(
                    MyApplication.getInstance().getOpenfireJid(), jid);
            return;
        }

        XmppMessage xmsg = null;
        try {
            GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            Group group = gService.getGroup(jid);
            DataForm dataInfo = group.getInfo();
            String groupNickname = dataInfo.getField("name").getValues().get(0);
            xmsg = createTipsMessage(jid, groupNickname, memberInfo.getNickname() + " 退出本群！");

        } catch (Exception e) {
            e.printStackTrace();
            xmsg = createTipsMessage(jid, jid, memberInfo.getNickname() + " 退出本群！");
        } finally {
            XmppDbManager.getInstance(getApplicationContext()).insertMessageWithNotify(xmsg);
        }
    }

    @Override
    public void dispatchJoined(String jid, MemberJoinedNotify notify) {
        MemberInfo memberInfo = notify.getMemberInfo();
        if (isMySelfSendMessage(memberInfo.getUserName())) {
            return;
        }
        XmppMessage xmsg = null;
        try {
            GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            Group group = gService.getGroup(jid);
            DataForm dataInfo = group.getInfo();
            String groupNickname = dataInfo.getField("name").getValues().get(0);
            xmsg = createTipsMessage(jid, groupNickname, memberInfo.getNickname() + " 加入本群！");

        } catch (Exception e) {
            e.printStackTrace();
            xmsg = createTipsMessage(jid, jid, memberInfo.getNickname() + " 加入本群！");
        } finally {
            XmppDbManager.getInstance(getApplicationContext()).insertMessageWithNotify(xmsg);
        }
    }

    //TODO 测试
    @Override
    public void dispatchApply(String jid, MemberApplyToJoinNotify notify) {
        String reason = notify.getReason();
        MemberInfo memberInfo = notify.getMemberInfo();

        try {
            GroupService gService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            Group group = gService.getGroup(jid);

            DataForm dataInfo = group.getInfo();

            String groupNickname = dataInfo.getField("name").getValues().get(0);
            createGroupOperateMessageAndInsert(jid, groupNickname, "申请加入群 " +
                    "", MsgInFo.OPERATE_APPLY, memberInfo.getUserName(), memberInfo.getNickname());

        } catch (Exception e) {
            e.printStackTrace();
            createGroupOperateMessageAndInsert(jid, jid, "申请加入群 ", MsgInFo.OPERATE_APPLY
                    , memberInfo.getUserName(), memberInfo.getNickname());
        } finally {
            XmppDbManager.getInstance(getApplicationContext()).deleteChat(MyApplication.getInstance().getOpenfireJid(), jid);
        }

    }

    @Override
    public void dispatchDestroy(String jid, GroupDestroyedNotify notify) {
        String fromJid = notify.getFrom();

        //删除 群消息
        XmppDbManager.getInstance(getApplicationContext()).deleteChat(
                MyApplication.getInstance().getOpenfireJid(), jid);

        //TODO 自己处理的
        if (isMySelfSendMessage(fromJid)) {
            return;
        }

        createGroupOperateMessageAndInsert(jid, jid, "管理员解散群", MsgInFo.OPERATE_TIPS, fromJid, fromJid);

    }

    @Override
    public void dispatchApplyResult(String jid, MemberApplyResultNotify notify) {
        String from = notify.getFrom();
        String reason = notify.getReason();
        boolean result = notify.getResult();

    }

    /**
     * 放到消息界面的提示消息
     *
     * @param groupJid
     * @param groupNickName
     * @param tips
     * @return
     */
    private XmppMessage createTipsMessage(String groupJid, String groupNickName, String tips) {
        XmppMessage msg = new XmppMessage();
        msg.setFrom_(groupJid);
        msg.setTo_(MyApplication.getInstance().getOpenfireJid());

        msg.setBody(tips);
        msg.setMold(MsgInFo.MOLD_TIPS);
        msg.setType(MsgInFo.TYPE_GROUPCHAT);
        msg.setDirect(MsgInFo.INOUT_IN);
        msg.setStatus(MsgInFo.STATUS_PENDING);
        msg.setReadStatus(MsgInFo.READ_TRUE);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setExtLocalDisplayName(MyApplication.getInstance().getLocalMember().NickName);
        msg.setExtLocalUserName(MyApplication.getInstance().getOpenfireJid());

        msg.setExtRemoteUserName(groupJid);

        msg.setExtRemoteDisplayName(groupNickName);

        return msg;
    }


    private static HashMap<String, String> mapFields(DataForm.Item item) {
        HashMap<String, String> maps = new HashMap<String, String>(item.getFields().size());
        for (FormField field : item.getFields()) {
            maps.put(field.getVariable(), field.getValues().get(0));
        }
        return maps;
    }

    /**
     * @param groupJid
     * @param groupNickname
     * @param body
     * @param operateType
     * @param operateUsername
     * @param operateNickname
     * @return
     */
    private XmppMessage createGroupOperateMessageAndInsert(String groupJid, String groupNickname, String body,
                                                           int operateType, String
                                                                   operateUsername, String
                                                                   operateNickname) {


        XmppMessage msg = new XmppMessage();
        msg.setTo_(groupJid);
        msg.setFrom_(MyApplication.getInstance().getOpenfireJid());
        msg.setType(MsgInFo.TYPE_GROUP_OPERATE);

        msg.setBody(body);
        msg.setMold(MsgInFo.MOLD_TEXT);
        msg.setDirect(MsgInFo.INOUT_IN);
        msg.setStatus(MsgInFo.STATUS_PENDING);
        msg.setReadStatus(MsgInFo.READ_FALSE);
        msg.setCreateTime(System.currentTimeMillis());

        msg.setExtLocalUserName(MyApplication.getInstance().getOpenfireJid());
        // TODO: 2016/3/10  又被借着去显示群名称了
        msg.setExtLocalDisplayName(groupNickname);

        msg.setExtGroupOperateType(operateType);
        msg.setExtGroupOperateUserName(operateUsername);
        msg.setExtGroupOperateUserNick(operateNickname);

        msg.setExtRemoteUserName(groupJid.substring(groupJid.lastIndexOf("@") + 1, groupJid.length()));
        // TODO: 2016/3/10  这里写死了，是为了在sql查询时，groupby 
        msg.setExtRemoteDisplayName("群通知");


        L.d(TAG, msg.toString());
        XmppDbManager.getInstance(getApplicationContext()).insertMessageWithNotify(msg);
        return msg;
    }

    @Override
    public void processPacket(Stanza packet) throws NotConnectedException {

        Notify notify = getNotifyFromExtensions(packet);
        if (notify != null) {
            L.d(TAG, "dispatch groupJid :" + packet.getFrom());
            dispatch(packet.getFrom(), notify);
        }
    }

    public Notify getNotifyFromExtensions(Stanza packet) {

        for (ExtensionElement extension : packet.getExtensions()) {
            if (extension instanceof NotifyPacketExtension) {
                return ((NotifyPacketExtension) extension).getNotify();
            }
        }
        return null;
    }

    void dispatch(String jid, Notify notify) {
        switch (notify.getType()) {
            case MEMBER_JOINED:
                dispatchJoined(jid, (MemberJoinedNotify) notify);
                break;
            case MEMBER_EXITED:
                dispatchExited(jid, (MemberExitedNotify) notify);
                break;
            case MEMBER_KICKED:
                dispatchKicked(jid, (MemberKickedNotify) notify);
                break;
            case MEMBER_PROFILE_CHANGED:
                // dispatchProfile(jid, (MemberProfileChangedNotify) notify);
                break;
            case MEMBER_APPLY_TO_JOIN:
                dispatchApply(jid, (MemberApplyToJoinNotify) notify);
                break;
            case MEMBER_APPLY_TO_JOIN_RESULT:
                dispatchApplyResult(jid, (MemberApplyResultNotify) notify);
                break;
            case GROUP_DESTROY:
                dispatchDestroy(jid, (GroupDestroyedNotify) notify);
            case MEMBER_INVITE:
                dispatchInvite(jid, (MemberInviteNotify) notify);
                break;
            default:
                break;
        }
    }

}
