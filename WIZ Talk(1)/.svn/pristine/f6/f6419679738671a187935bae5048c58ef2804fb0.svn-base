package com.epic.traverse.push.smack;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.epic.traverse.push.model.Extension.ContentImageExtension;
import com.epic.traverse.push.model.Extension.ContentVoiceExtension;
import com.epic.traverse.push.model.Extension.MessageExtension;
import com.epic.traverse.push.model.provider.ContentImageExtensionProvider;
import com.epic.traverse.push.model.provider.ContentVoiceExtensionProvider;
import com.epic.traverse.push.model.provider.MessageExtensionProvider;
import com.epic.traverse.push.util.HttpEngine;
import com.epic.traverse.push.util.L;
import com.skysea.group.Group;
import com.skysea.group.GroupService;
import com.skysea.group.provider.GroupPacketProvider;
import com.skysea.group.provider.NotifyPacketExtensionProvider;
import com.skysea.group.provider.SearchProvider;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
import org.jxmpp.util.XmppStringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.MathContext;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XmppManager 工具类
 */
public class XmppManager {

	private final static int SERVER_PORT = 5222;
	// public final static String SERVER_HOST = "10.180.120.63";
	// public final static String SERVER_NAME = "10.180.120.63";
	// public final static String RESOUCE_TAG = "Android";
	public final static String BROADCAST_TAG = "broadcast";

//	public final static String SERVER_HOST = "10.180.120.63";//10.180.128.126  10.180.128.127 192
	// .168.31.194
//	public final static String SERVER_NAME = "skysea.com";//skysea.com  //admin-pc
//	public final static String RESOUCE_TAG = "Android";

//	private static String PLUGIN_URL = "http://" + SERVER_HOST
//			+ ":9090/plugins/restapi/v1";

	private static XmppManager mInstance = new XmppManager();

	private XMPPTCPConnection connection = null;

	private GroupService groupService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	synchronized public static XmppManager getInstance() {
		return mInstance;
	}

    
	public XMPPTCPConnection getConnection() {
		if (connection == null || !connection.isConnected()) {
            return null;
        }
		return connection;
	}

    /**
     * 获得groupService
     * @param serverName  域名
     * @return
     */
	public GroupService getGroupService(String serverName) {
//        TODO
		/*if (groupService == null) { // group.skysea.com
			if (connection == null || !connection.isConnected()) {
				connection = getConnection();
			}
			groupService = new GroupService(connection, "group." + SERVER_NAME);
		}*/
		groupService = new GroupService(connection, "group.".concat(serverName));
		return groupService;
	}

    /**
     *  打开连接
     * @param host  
     * @param serverName 域名
     * @param port
     * @param resourceTag
     * @return
     */
	public XMPPTCPConnection openConnection(String host,String serverName,int port,String resourceTag) {
		L.d(" openConnection() ");
		try {
			if (null == connection || !connection.isAuthenticated()) {
				XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration
						.builder()
						// .setUsernameAndPassword("67890", "1")
						.setResource(resourceTag)
						.setServiceName(serverName)
						.setHost(host)
						.setSendPresence(false)
						.setPort(port)
						.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
						.setDebuggerEnabled(true);

				// SmackConfiguration.setDefaultPingInterval(30);
				connection = new XMPPTCPConnection(configBuilder.build());
				connection.connect();// 连接到服务器
				// 配置各种Provider，老版本如果不配置，则会无法解析数据
				// configureConnection(ProviderManager.getInstance());
				configureConnection2();
			}
		} catch (Exception e) {
			L.e("xmppmanager  connection falied");
			e.printStackTrace();
			connection = null;
		}
		return connection;
	}

	/**
	 * 为了解析自定义的扩展信息
	 */
	private void configureConnection2() {
//		自己定义的消息格式
		ProviderManager.addExtensionProvider(MessageExtension.NAME,
				MessageExtension.NAME_SPACE, new MessageExtensionProvider());
		ProviderManager.addExtensionProvider(ContentImageExtension.NAME,
				ContentImageExtension.NAME_SPACE,
				new ContentImageExtensionProvider());
		ProviderManager.addExtensionProvider(ContentVoiceExtension.NAME,
				ContentVoiceExtension.NAME_SPACE,
				new ContentVoiceExtensionProvider());

		// providers.add(new IQProviderInfo("x", GroupService.GROUP_NAMESPACE,
		// groupPacketProvider));
		// providers.add(new IQProviderInfo("query",
		// GroupService.GROUP_NAMESPACE, groupPacketProvider));
		// providers.add(new IQProviderInfo("query",
		// GroupService.GROUP_USER_NAMESPACE, groupPacketProvider));
		// providers.add(new IQProviderInfo("query", "jabber:iq:search", new
		// SearchProvider()));
		
		
		//配置自定义group的消息格式，
		GroupPacketProvider groupPacketProvider = new GroupPacketProvider();
		ProviderManager.addIQProvider("x", GroupService.GROUP_NAMESPACE,
				groupPacketProvider);
		ProviderManager.addIQProvider("query", GroupService.GROUP_NAMESPACE,
				groupPacketProvider);
		ProviderManager.addIQProvider("query",
				GroupService.GROUP_USER_NAMESPACE, groupPacketProvider);
		ProviderManager.addIQProvider("query", "jabber:iq:search",
				new SearchProvider());

		NotifyPacketExtensionProvider notifyProvider = new NotifyPacketExtensionProvider();
		// providers.add(new ExtensionProviderInfo("x",
		// GroupService.GROUP_MEMBER_NAMESPACE, notifyProvider));
		// providers.add(new ExtensionProviderInfo("x",
		// GroupService.GROUP_NAMESPACE, notifyProvider));
		// providers.add(new ExtensionProviderInfo("x",
		// GroupService.GROUP_OWNER_NAMESPACE, notifyProvider));
		// providers.add(new ExtensionProviderInfo("x",
		// GroupService.GROUP_USER_NAMESPACE, notifyProvider));

		ProviderManager.addExtensionProvider("x",
				GroupService.GROUP_MEMBER_NAMESPACE, notifyProvider);
		ProviderManager.addExtensionProvider("x", GroupService.GROUP_NAMESPACE,
				notifyProvider);
		ProviderManager.addExtensionProvider("x",
				GroupService.GROUP_OWNER_NAMESPACE, notifyProvider);
		ProviderManager.addExtensionProvider("x",
				GroupService.GROUP_USER_NAMESPACE, notifyProvider);
	}

	/**
	 * 关闭连接
	 */
	public void closeConnection() {
		if (connection != null) {
			// 移除连接监听
//			 connection.removeConnectionListener(connectionListener);
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
		L.d("XmppManager", "关闭连接");
	}

	/**
	 * 添加一个用户到
	 * 
	 * @param groupName
	 * @return
	 *//*
	public boolean addUserToSharedGroup(String bareJid, String groupName) {

		String url = PLUGIN_URL + "/users/" + bareJid + "/groups/" + groupName;
		String result = HttpEngine.doPost(url, null, "POST");
		if (result.equals("201"))// 201成功
			return true;
		return false;
	}
*/

	/**
	 * 注册
	 * 
	 * @param account
	 *            注册帐号
	 * @param password
	 *            注册密码
	 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
	 */
	public String regist(String account, String password)
			throws NotConnectedException {
//		if (getConnection() == null)
//			return "0";
		/*Registration reg = new Registration();
		reg.setType(IQ.Type.set);
		reg.setTo(getConnection().getServiceName());
		// 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。
		// TODO: 2015/11/20
		// reg.setUsername(account);
		// reg.setPassword(password);
		// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		// reg.addAttribute("android", "geolo_createUser_android");
		*//*
		 * PacketFilter filter = new AndFilter(new PacketIDFilter(
		 * reg.getPacketID()), new PacketTypeFilter(IQ.class)); PacketCollector
		 * collector = getConnection().createPacketCollector( filter);
		 *//*
		StanzaFilter filter1 = new AndFilter(new StanzaIdFilter(
				reg.getStanzaId()), new StanzaTypeFilter(IQ.class));
		PacketCollector collector = getConnection().createPacketCollector(
				filter1);
		// getConnection().sendStanza(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getDefaultPacketReplyTimeout());
		// Stop queuing results停止请求results（是否成功的结果）
		collector.cancel();
		if (result == null) {
			Log.e("regist", "No response from server.");
			return "0";
		} else if (result.getType() == IQ.Type.result) {
			Log.v("regist", "regist success.");
			return "1";
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e("regist", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return "2";
			} else {
				Log.e("regist", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return "3";
			}
		}*/

		AccountManager accountManager = AccountManager.getInstance(connection);
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "liulu");
		map.put("email", "liulu@im.yn.csg");
		try {
			boolean flag = accountManager.supportsAccountCreation();
			Log.d("TAG", "flag:" + flag);
			accountManager.createAccount(account, password,map);
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 更改用户状态
	 */
	public void setPresence(int code)
			throws NotConnectedException {
		XMPPTCPConnection con = getConnection();
		if (con == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			con.sendStanza(presence);
			Log.v("state", "设置在线");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			con.sendStanza(presence);
			Log.v("state", "设置Q我吧");
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			con.sendStanza(presence);
			Log.v("state", "设置忙碌");
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			con.sendStanza(presence);
			Log.v("state", "设置离开");
			break;
		case 4:
			Roster roster = Roster.getInstanceFor(con);
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setMode(Presence.Mode.xa);
				presence.setFrom(con.getUser());
				presence.setTo(entry.getUser());
				con.sendStanza(presence);
				Log.v("state", presence.toXML().toString());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);

			presence.setFrom(con.getUser());
			// TODO
			 presence.setTo(XmppStringUtils.parseLocalpart(con.getUser()));
			L.d("localuser :" + con.getUser());
			presence.setTo(parseResource(con.getUser()));
			L.d("localuser :" + con.getUser());
			con.sendStanza(presence);
			Log.v("state", "设置隐身");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			con.sendStanza(presence);
			Log.v("state", "设置离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 断开连接 ，并设置下线
	 */
	public void exit(){
        try {
            connection.disconnect();
            connection.instantShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
     *  发送group 消息
     * @param jid
     * @param message
     * @param serverName 用户构建groupservice
     * @return
     * @throws NotConnectedException
     * @throws IllegalArgumentException
     * @throws NoResponseException
     * @throws XMPPErrorException
     */
	public boolean sendMsg2Group(String jid, Message message ,String serverName)
			throws NotConnectedException ,IllegalArgumentException, NoResponseException, XMPPErrorException{
		L.d(" sendMsg2Qun");
		L.d(" sendMsg2Qun isConnected:" + connection.isConnected());
		if (connection == null || !connection.isConnected()) {
			return false;
		}
		
		Group group = getGroupService(serverName).getGroup(jid);
		if (group == null) {
			return false;
		}
		group.send(message);
		return true;
	}

	/**
	 * 发送单聊消息
	 * 
	 * @param message
	 * @return true 成功，false：失败
	 * @throws NotConnectedException
	 */
	public boolean sendMessage(Message message) throws NotConnectedException {
		L.d(" sendMessage");
		L.d(" sendMessage isConnected:" + getConnection().isConnected());

		if (connection == null) {
			return false;
		}
		ChatManager chatManager = ChatManager.getInstanceFor(getConnection());
		Chat chat = chatManager.createChat(message.getTo());
		chat.sendMessage(message);
		chat.close();
		return true;
	}

	/**
	 * 向聊天室发送消息
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendQunMessage(Message message) {
		L.d(" sendQunMessage");
		L.d(" sendQunMessage isConnected:" + getConnection().isConnected());
		L.d(" sendQunMessage qun roomName:" + message.getTo());
		try {
			if (getConnection() == null) {
				return false;
			}
			String roomName = message.getTo().split("@")[0];
			MultiUserChat muc = joinMultiUserChat(message.getFrom(), roomName,
					"1");
			muc.sendMessage(message);
			return true;
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取所有组
	 * 
	 * @return 所有组集合
	 */
	public List<RosterGroup> getGroups() {
		if (getConnection() == null)
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = Roster.getInstanceFor(
				getConnection()).getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext()) {
			grouplist.add(i.next());
		}
		return grouplist;
	}

	/**
	 * 获取某个组里面的所有好友
	 *
	 * @param groupName
	 * @return
	 */
	public List<RosterEntry> getEntriesByGroup(String groupName) {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = Roster.getInstanceFor(getConnection())
				.getGroup(groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * 获取所有好友信息
	 *
	 * @return
	 */
	public List<RosterEntry> getAllEntries() {
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = Roster.getInstanceFor(
				getConnection()).getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * 获取用户VCard信息
	 *
	 * @param user
	 * @return
	 * @throws XMPPException
	 */
	public VCard getUserVCard(String user) {
		if (getConnection() == null)
			return null;
		VCard vcard = new VCard();
		try {
			vcard.load(getConnection(), user);
		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		}
		return vcard;
	}

	/**
	 * 获取用户头像信息
	 *
	 * @param user
	 * @return
	 */
	public Drawable getUserImage(String user) {
		if (getConnection() == null)
			return null;
		ByteArrayInputStream bais = null;
		try {
			/*
			 * // 加入这句代码，解决No VCard for
			 * ProviderManager.getInstance().addIQProvider("vCard",
			 * "vcard-temp", new
			 * org.jivesoftware.smackx.provider.VCardProvider());
			 */
			if (user == "" || user == null || user.trim().length() <= 0) {
				return null;
			}
			VCard vCard = VCardManager.getInstanceFor(getConnection())
					.loadVCard(user + "@" + getConnection().getServiceName());
			if (vCard == null || vCard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vCard.getAvatar());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return FormatTools.getInstance().InputStream2Drawable(bais);
	}

	/**
	 * 添加一个分组
	 *
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(String groupName) {
		if (getConnection() == null)
			return false;
		try {
			Roster.getInstanceFor(getConnection()).createGroup(groupName);
			L.v("addGroup", groupName + "创建成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 添加好友 无分组
	 * 
	 * @param userName
	 * @param name
	 * @return
	 */
	public boolean addUser(String userName, String name) {
		if (getConnection() == null)
			return false;
		try {
			Roster.getInstanceFor(getConnection()).createEntry(userName, name,
					null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 添加好友 进一个分组
	 *
	 * @param userName
	 * @param name
	 * @param groupName
	 * @return
	 */
	public boolean addRosterUserAndGroup(String userName, String name,
			String groupName) {
		if (getConnection() == null)
			return false;
		try {
			Presence subscription = new Presence(Presence.Type.subscribed);
			subscription.setTo(userName);
			userName += "@" + getConnection().getServiceName();
			getConnection().sendStanza(subscription);
			Roster.getInstanceFor(getConnection()).createEntry(userName, name,
					new String[] { groupName });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除好友
	 *
	 * @param userName
	 * @return
	 */
	public boolean removeRosterUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@")) {
				entry = Roster.getInstanceFor(getConnection()).getEntry(
						userName);
			} else {
				entry = Roster.getInstanceFor(getConnection()).getEntry(
						userName + "@" + getConnection().getServiceName());
			}
			if (entry == null)
				entry = Roster.getInstanceFor(getConnection()).getEntry(
						userName);
			Roster.getInstanceFor(getConnection()).removeEntry(entry);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 查询用户
	 *
	 * @param userName
	 * @return
	 * @throws XMPPException
	 */
	public List<HashMap<String, String>> searchRosterUsers(String userName) {
		if (getConnection() == null)
			return null;
		HashMap<String, String> user = null;
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		try {
			ServiceDiscoveryManager sdm = ServiceDiscoveryManager
					.getInstanceFor(getConnection());
			UserSearchManager usm = new UserSearchManager(getConnection());

			Form searchForm = usm.getSearchForm(getConnection()
					.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("userAccount", true);
			answerForm.setAnswer("userPhote", userName);
			ReportedData data = usm.getSearchResults(answerForm, "search"
					+ getConnection().getServiceName());

			Iterator<ReportedData.Row> it = data.getRows().iterator();
			ReportedData.Row row = null;
			while (it.hasNext()) {
				user = new HashMap<String, String>();
				row = it.next();
                user.put("userAccount", row.getValues("userAccount").get(0));
                user.put("userAccount", row.getValues("userAccount").get(0));
                user.put("userPhote", row.getValues("userPhote").get(0));
                user.put("userPhote", row.getValues("userPhote").get(0));
				results.add(user);
				// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * 修改心情
	 *
	 * @param status
	 */
	public void changeStateMessage(String status) {
		if (getConnection() == null)
			return;
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		try {
			getConnection().sendStanza(presence);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改用户头像
	 *
	 * @param file
	 */
	public boolean changeImage(File file) {
		if (getConnection() == null)
			return false;
		try {
			VCard vcard = VCardManager.getInstanceFor(getConnection())
					.loadVCard();
			byte[] bytes;
			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeHex(bytes);
			vcard.setAvatar(bytes);
			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
					+ encodedImage + "</BINVAL>", true);

			ByteArrayInputStream bais = new ByteArrayInputStream(
					vcard.getAvatar());
			FormatTools.getInstance().InputStream2Bitmap(bais);
			VCardManager.getInstanceFor(getConnection()).saveVCard(vcard);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 文件转字节
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 删除当前用户
	 * @return
	 */
	public boolean deleteAccount() {
		if (getConnection() == null ) {
			return false;
		}
		try {
			if (connection.isConnected()) {
				AccountManager.getInstance(getConnection()).deleteAccount();
				connection.disconnect();
				return true;
			}
			
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 修改密码
	 *
	 * @return
	 */
	public boolean changePassword(String pwd) {
		if (getConnection() == null)
			return false;

		try {
			AccountManager.getInstance(getConnection()).changePassword(pwd);
			return true;
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 初始化会议室列表
	 */
	public List<HostedRoom> getHostRooms() {
		if (getConnection() == null)
			return null;
		Collection<HostedRoom> hostrooms = null;
		List<HostedRoom> roominfos = new ArrayList<HostedRoom>();
		try {
			hostrooms = MultiUserChatManager.getInstanceFor(getConnection())
					.getHostedRooms(getConnection().getServiceName());
			for (HostedRoom entry : hostrooms) {
				roominfos.add(entry);
				Log.i("room",
						"名字：" + entry.getName() + " - ID:" + entry.getJid());
			}
			Log.i("room", "服务会议数量:" + roominfos.size());
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		}
		return roominfos;
	}

	/**
	 * 
	 * @param user
	 *            创建者
	 * @param roomName
	 *            room名字
	 * @param password
	 *            房间密码
	 * @return
	 */
	public MultiUserChat createRoom(String user, String roomName,
			String password) {
		if (getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			muc = MultiUserChatManager.getInstanceFor(getConnection())
					.getMultiUserChat(
							roomName + "@conference."
									+ getConnection().getServiceName());
			// 创建一个MultiUserChat
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields().iterator(); fields
					.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.Type.hidden.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
			List<String> owners = new ArrayList<String>();
			owners.add(getConnection().getUser());// 用户JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
						true);
				// 设置进入密码
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (SmackException e) {
			e.printStackTrace();
		}
		return muc;
	}

	/**
	 * 加入会议室
	 *
	 * @param user
	 *            昵称
	 * @param password
	 *            会议室密码
	 * @param roomName
	 *            会议室名
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomName,
			String password) {
		if (getConnection() == null)
			return null;
		try {
			// 使用XMPPConnection创建一个MultiUserChat窗口
			MultiUserChat muc = MultiUserChatManager.getInstanceFor(
					getConnection()).getMultiUserChat(
					roomName + "@conference."
							+ getConnection().getServiceName());
			// 聊天室服务将会决定要接受的历史记录数量
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// 用户加入聊天室
			muc.join(user, password, history,
					SmackConfiguration.getDefaultPacketReplyTimeout());
			Log.i("MultiUserChat", "会议室【" + roomName + "】加入成功........");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "会议室【" + roomName + "】加入失败........");
			return null;
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		}
		// TODO 发送广播的方式
		/*
		 * Message packet = new Message();
		 * packet.setTo("groupName@broadcast.10.180.120.63");
		 * connection.sendStanza(packet);
		 */
		return null;
	}

	/**
	 * 查询会议室成员名字
	 *
	 * @param muc
	 */
	public List<String> findMulitUser(MultiUserChat muc) {
		if (getConnection() == null)
			return null;
		List<String> listUser = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants().iterator();
		// 遍历出聊天室人员名称
		while (it.hasNext()) {
			// 聊天室成员名字
			// TODO
			// String name = XmppStringUtils.parseResource(it.next());
			String name = parseResource(it.next());
			listUser.add(name);
		}
		return listUser;
	}

	String parseResource(String jid) {
		return jid.split("@")[0];
	}

	/**
	 * 获取离线消息
	 *
	 * @return
	 */
	public List<Message> getOfflineMessage() {
		if (getConnection() == null)
			return null;
		List<Message> offlineMsgs = new ArrayList<Message>();
		try {
			OfflineMessageManager om = new OfflineMessageManager(
					getConnection());
			/*
			 * if (om.getMessageCount() >= 0) { return offlineMsgs;//返回0 }
			 */
			List<Message> messages = om.getMessages();
			for (Message m : messages) {
				if (m.getBody() != null) {
					offlineMsgs.add(m);
				}
			}
			om.deleteMessages();
			return offlineMsgs;
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}

		return offlineMsgs;
	}

	/**
	 * 判断OpenFire用户的状态 strUrl : url格式 -
	 * http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 -
	 * 用户离线 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
	 *//*
	public int IsUserOnLine(String user, int i) {
		String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?"
				+ "jid=" + user + "@" + SERVER_NAME + "&type=xml";
		int shOnLineState = 0; // 不存在
		try {
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(
						oConn.getInputStream()));
				if (null != oIn) {
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag" + strFlag);
					if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
						shOnLineState = 2;
					}
					if (strFlag.indexOf("type=\"error\"") >= 0) {
						shOnLineState = 0;
					} else if (strFlag.indexOf("priority") >= 0
							|| strFlag.indexOf("id=\"") >= 0) {
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shOnLineState;
	}*/

	/**
	 * 加入providers的函数 ASmack在/META-INF缺少一个smack.providers 文件
	 *
	 * @param pm
	 */
	public void configureConnection(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		// pm.addExtensionProvider("x", "jabber:x:roster",
		// new RosterExchangeProvider());

		// Message Events
		// pm.addExtensionProvider("x", "jabber:x:event",
		// new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	/**
	 * 判断OpenFire用户的状态 strUrl : url格式 -
	 * http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 -
	 * 用户离线 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
	 *//*
	public int IsUserOnLine(String user) {
		String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?"
				+ "jid=" + user + "@" + SERVER_NAME + "&type=xml";
		int shOnLineState = 0; // 不存在
		try {
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(
						oConn.getInputStream()));
				if (null != oIn) {
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag" + strFlag);
					if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
						shOnLineState = 2;
					}
					if (strFlag.indexOf("type=\"error\"") >= 0) {
						shOnLineState = 0;
					} else if (strFlag.indexOf("priority") >= 0
							|| strFlag.indexOf("id=\"") >= 0) {
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shOnLineState;
	}*/

}
