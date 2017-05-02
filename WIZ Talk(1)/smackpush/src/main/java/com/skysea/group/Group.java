package com.skysea.group;

import com.skysea.group.packet.QueryPacket;
import com.skysea.group.packet.XPacket;
import com.skysea.group.packet.operate.*;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * 圈子对象类，代表某具体的圈子。
 * Created by zhangzhi on 2014/9/18.
 */
public final class Group {
    private final XMPPConnection connection;
    private String jid;

    Group(XMPPConnection connection, String jid) {
        assert connection != null;
        assert jid != null;

        this.connection = connection;
        this.jid = jid;
    }


    /**
     * 获得圈子jid。
     * @return
     */
    public String getJid() {
        return jid;
    }

    /**
     * 获得圈子详情。
     * @return 圈子详情表单对象。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public DataForm getInfo() throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        QueryPacket packet = new QueryPacket(GroupService.GROUP_NAMESPACE, "info");

        packet = (QueryPacket)request(packet);
        return packet.getDataForm();
    }

    /**
     * 更新圈子信息。
     * @param form 圈子更新表单对象。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void updateInfo(DataForm form) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        if(form == null){ throw new NullPointerException("form is null."); }

        XPacket packet = new XPacket(GroupService.GROUP_NAMESPACE, form);
        request(packet);
    }

    /**
     * 销毁圈子。
     * @param reason 销毁的原因。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void destroy(String reason) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        HasReasonOperate ope = HasReasonOperate.newInstanceForDestroyGroup(reason);
        XPacket packet = new XPacket(GroupService.GROUP_OWNER_NAMESPACE, ope);
        request(packet);
    }

    /**
     * 获得圈子成员列表。
     * @return
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public DataForm getMembers() throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        QueryPacket packet = new QueryPacket(GroupService.GROUP_NAMESPACE, "members");

        packet = (QueryPacket)request(packet);
        return packet.getDataForm();
    }


    /**
     * 申请加入圈子。
     * @param nickname 圈子昵称
     * @param reason 申请验证消息。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void applyToJoin(String nickname, String reason) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        ApplyOperate ope = new ApplyOperate();
        ope.setNickname(nickname);
        ope.setReason(reason);

        XPacket packet = new XPacket(GroupService.GROUP_USER_NAMESPACE, ope);
//        request(packet);
        requestWithoutResult(packet);
    }

    /**
     * 邀请用户加入圈子。
     * @param username 被邀请的用户名。
     * @param nickname 被邀请的用户昵称。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void inviteToJoin(String username, String nickname) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {
        if(username == null) { throw new NullPointerException("username is null."); }
        if(username.length() == 0) { throw new IllegalArgumentException("username is invalid."); }

        InviteOperate ope = new InviteOperate();
        ope.addMember(username, nickname);

        XPacket packet = new XPacket(GroupService.GROUP_MEMBER_NAMESPACE, ope);
        request(packet);
    }

    /**
     * 处理成员加入申请。
     * @param username 申请人用户名。
     * @param nickname 申请人昵称。
     * @param agree 是否同意申请。
     * @param reason 同意/拒绝的原因。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void processInvite( String username, String nickname, boolean agree, String reason) throws
             SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

    	ProcessInviteOperate ope = new ProcessInviteOperate(username, nickname, agree);
        ope.setReason(reason);
        XPacket packet = new XPacket(GroupService.GROUP_USER_NAMESPACE, ope);
//        request(packet);
        requestWithoutResult(packet);
    }
    /**
     * 针对上面的额方法，没有返回值的。
     * @param packet
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    private void requestWithoutResult(IQ packet) throws SmackException.NotConnectedException {
        packet.setTo(jid);
        connection.sendStanza(packet);
    }
    
    

    /**
     * 处理成员加入申请。
     * @param id 申请事务Id。
     * @param username 申请人用户名。
     * @param nickname 申请人昵称。
     * @param agree 是否同意申请。
     * @param reason 同意/拒绝的原因。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void processApply(String id, String username, String nickname, boolean agree, String reason) throws
             SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        ProcessApplyOperate ope = new ProcessApplyOperate(id, username, nickname, agree);
        ope.setReason(reason);
        XPacket packet = new XPacket(GroupService.GROUP_OWNER_NAMESPACE, ope);
        request(packet);
    }

    /**
     * 退出圈子。
     * @param reason 退出的原因。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void exit(String reason) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {

        HasReasonOperate ope = HasReasonOperate.newInstanceForExitGroup(reason);
        XPacket packet = new XPacket(GroupService.GROUP_MEMBER_NAMESPACE, ope);
        request(packet);
    }

    /**
     * 将用户踢出圈子。
     * @param username 被踢出的用户名。
     * @param reason 踢出的原因。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void kick(String username, String reason) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {
        if(username == null) { throw new NullPointerException("username is null."); }
        if(username.length() == 0) { throw new IllegalArgumentException("username is invalid."); }

        KickOperate ope = new KickOperate(username);
        ope.setReason(reason);
        XPacket packet = new XPacket(GroupService.GROUP_OWNER_NAMESPACE, ope);
        request(packet);
    }

    /**
     * 向圈子发送数据包。
     * @param packet 数据包对象实例。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException 
     * @throws NoResponseException 
     */
    public void send(Stanza packet) throws SmackException.NotConnectedException, NoResponseException, XMPPErrorException {
        if (packet == null) { throw new NullPointerException("packet is null."); }

        packet.setTo(jid);
//        connection.createPacketCollectorAndSend((IQ) packet).nextResultOrThrow();
        connection.sendStanza(packet);
        
    }
    
    

    /**
     * 修改圈子昵称。
     * @param newNickname 新的昵称。
     * @throws SmackException.NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     */
    public void changeNickname(String newNickname) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {
        if(newNickname == null) { throw new NullPointerException("newNickname is null."); }
        if(newNickname.length() == 0) { throw new IllegalArgumentException("newNickname is invalid."); }

        ChangeProfileOperate ope = new ChangeProfileOperate();
        ope.setNickname(newNickname);

        XPacket packet = new XPacket(GroupService.GROUP_MEMBER_NAMESPACE, ope);
        request(packet);
    }

    private Stanza  request(IQ packet) throws
            SmackException.NotConnectedException,
            XMPPErrorException,
            NoResponseException {
        packet.setTo(jid);
       return connection.createPacketCollectorAndSend(packet).nextResultOrThrow();
    }
}
