package com.skysea.group;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import com.skysea.group.packet.GroupSearch;
import com.skysea.group.packet.QueryPacket;
import com.skysea.group.packet.XPacket;

/**
 * 圈子服务提供程序。
 * Created by zhangzhi on 2014/9/18.
 */
public final class GroupService {
    public final static String GROUP_OWNER_NAMESPACE = "http://skysea.com/protocol/group#owner";
    public final static String GROUP_NAMESPACE = "http://skysea.com/protocol/group";
    public final static String GROUP_MEMBER_NAMESPACE = "http://skysea.com/protocol/group#member";
    public final static String GROUP_USER_NAMESPACE = "http://skysea.com/protocol/group#user";
    private final XMPPConnection connection;
    private final String domain;
    private final EventDispatcher eventDispatcher;

    static {
        ProviderManager.addLoader(new GroupProviderLoader());
    }

    public GroupService(XMPPConnection connection, String domain){
        if(connection == null) { throw new NullPointerException("connection is null."); }
        if(domain == null) { throw  new NullPointerException("domain is null."); }
        this.connection = connection;
        this.domain = domain;
        this.eventDispatcher = new EventDispatcher(connection, domain);
    }


    /**
     * 获得服务域名。
     * @return
     */
    public String getServiceDomain() {
        return domain;
    }

    /**
     * 获得xmpp连接对象。
     * @return
     */
    public XMPPConnection getConnection() {
        return connection;
    }

    /**
     * 创建一个圈子。
     * @param form 创建圈子所需的数据表单对象。
     * @return 创建成功的圈子对象。
     * @throws SmackException.NotConnectedException
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException.NoResponseException
     */
    public Group create(DataForm form) throws
            SmackException.NotConnectedException,
            XMPPException.XMPPErrorException,
            SmackException.NoResponseException {
        if(form == null) { throw new NullPointerException("form is null."); }


        XPacket packet = new XPacket(GROUP_NAMESPACE);
        packet.setDataForm(form);
        packet.setType(IQ.Type.set);

        // 根据表单返回圈子对象实例。
        Form resultForm = Form.getFormFrom(request(packet));
        String jid = resultForm.getField("jid").getValues().get(0);

        /* 触发创建事件 */
        eventDispatcher.dispatchCreate(jid, form);
        return createGroup(jid);
    }

    /**
     * 搜索圈子列表。
     * @param search 圈子搜索信息对象。
     * @return 搜索结果对象。
     */
    public GroupSearch search(GroupSearch search) throws
            SmackException.NotConnectedException,
            XMPPException.XMPPErrorException,
            SmackException.NoResponseException {
        if(search == null) { throw new NullPointerException("search is null."); }

        search.setType(IQ.Type.set);

        // 直接返回Group搜索对象
        return new GroupSearch((UserSearch)request(search));
    }

    /**
     * 获得用户已经加入的圈子列表。
     * @return 圈子列表表单对象。
     */
    public DataForm getJoinedGroups() throws
            SmackException.NotConnectedException,
            XMPPException.XMPPErrorException,
            SmackException.NoResponseException {

        QueryPacket packet = new QueryPacket(GROUP_USER_NAMESPACE, "groups");

        packet = (QueryPacket)request(packet);
        return packet.getDataForm();
    }

    /**
     * 获得某个圈子对象实例。
     * @param jid 圈子JID。
     * @return
     */
    public Group getGroup(String jid) {
        if(jid == null || jid.length() == 0) {
            throw new IllegalArgumentException("jid is null or empty.");
        }

        return createGroup(jid);
    }

    /**
     * 添加圈子事件监听器。
     * @param listener 事件监听器。
     */
    public void addGroupEventListener(GroupEventListener listener) {
        if(listener == null) { throw new NullPointerException("listener is null."); }
        eventDispatcher.addEventListener(listener);
    }

    /**
     * 产出圈子事件监听器。
     * @param listener 事件监听器。
     */
    public void removeGroupEventListener(GroupEventListener listener) {
        if(listener == null) { throw new NullPointerException("listener is null."); }
        eventDispatcher.removeEventListener(listener);
    }

    /**
     * 创建圈子对象实例。
     * @param jid
     * @return
     */
    private Group createGroup(String jid) {
        return new Group(connection, jid);
    }

    /**
     * 向服务发送请求消息，并等待响应返回。
     * @param packet
     * @return
     * @throws SmackException.NotConnectedException
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException.NoResponseException
     */
    private Stanza request(IQ packet) throws
            SmackException.NotConnectedException,
            XMPPException.XMPPErrorException,
            SmackException.NoResponseException {
    	System.out.println("reqeust:"+packet);
        packet.setTo(domain);
        return connection.createPacketCollectorAndSend(packet).nextResultOrThrow();
    }
}
