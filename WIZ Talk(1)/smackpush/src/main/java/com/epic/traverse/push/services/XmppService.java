package com.epic.traverse.push.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.epic.traverse.push.util.NetWorkUtils;
import com.skysea.group.GroupService;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dong on 2015/11/23.
 */
@SuppressLint("NewApi")
public abstract class XmppService extends Service implements GroupOperateEventListener{

	public static final String TAG = XmppService.class.getSimpleName();

	private XMPPTCPConnection mConnection;
	private MyConnectionListener mConnectionListener;
	OfflineMessageManager om;
	protected GroupService groupService;

	/**
	 * 开启推送服务Service
	 *
	 * @param context
	 */
	public static void startService(Context context) {
		Log.d(TAG, "startService() ");
		Intent intent = getXmppServiceIntent(context);
		if (intent != null) {
			context.startService(intent);
		}
	}

	/**
	 * 停止
	 *
	 * @param context
	 */
	public static void stopService(Context context) {
		Intent intent = getXmppServiceIntent(context);
		if (intent != null) {
			context.stopService(intent);
		}
	}

	private static Intent getXmppServiceIntent(Context context) {
		Intent intent = new Intent(XmppService.class.getName());
		L.d(TAG, XmppService.class.getName());
		intent.setPackage(context.getPackageName());

		List<ResolveInfo> resolveInfos = context.getPackageManager()
				.queryIntentServices(intent, 0);

		L.d(TAG, "List<ResolveInfo>:" + resolveInfos);

		if (resolveInfos != null) {
			for (ResolveInfo info : resolveInfos) {
				Intent i = new Intent();
				i.setClassName(info.serviceInfo.packageName,
						info.serviceInfo.name);
				return i;
			}
		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		L.d(TAG, "onCreate() ");
		startServiceRepeatly(this);
		super.onCreate();
	}

	boolean isconflict =false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if(isconflict){
			return START_NOT_STICKY;
		}
		L.d(TAG, "onStartCommand() " + this);
		final Context context = this;

		dealXmpp(this);

		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	private String mTagLocalOld;

	private void dealXmpp(final Context context) {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				L.d(TAG, "onStartCommand() isXmppConnectionRunning():"
						+ isXmppConnectionRunning());

				boolean isNetworkAvailable = NetWorkUtils
						.isNetworkAvailable(context);
				L.d(TAG, "onStartCommand() isNetworkAvailable:"
						+ isNetworkAvailable);

				// 先去登录pass平台，就可以获取到这个值，而且一定是正确的 ,需要比较userName的old和new
				String userName = getLocalUserJid();
				String userPwd = getLocalUserPwd();

                String openfireHost = getOpenfireHost();
                int port = getOpenfirePort();
                String serviceName = getOpenfireServiceName();
                String openfireResourceTag = getOpenfireResourceTag();

                L.d(TAG, "onStartCommand() tagLocalNew:" + userName);

				String tagLocalOld = mTagLocalOld;
				mTagLocalOld = userName;

				// 没有登录的用户
				if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)
						|| !isNetworkAvailable || !tagLocalOld.equals(userName)) {
					destroyXmppConnection();
				}

				if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPwd)
						&& !isXmppConnectionRunning() && isNetworkAvailable) {

					destroyXmppConnection();

					XmppConnectionInit(userName, userPwd,openfireHost,serviceName,port,
                            openfireResourceTag);
					// 注册在线消息监听
					registerOnlineMessageListener();
					// 注册应答邀请加入muc聊天室
					// registerInviteResponseMessageListener();
					// 注册加好友请求，主要是为了创建组，发送群消息
					// registerAddRosterMessageListener();

				}
			}
		});
	}

	/**
	 * 注册加好友请求，主要是为了创建组，发送群消息 自动应答加好友请求
	 */
	private void registerAddRosterMessageListener() {
		Roster.getInstanceFor(mConnection).setSubscriptionMode(
				SubscriptionMode.accept_all);
	}

	private void startServiceRepeatly(Context context) {
		Intent intent = getXmppServiceIntent(context);
		AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent mPendingIntent = PendingIntent.getService(this, 0,
		// intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		long now = System.currentTimeMillis();
		mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 20 * 1000,
				mPendingIntent);
	}

	/**
	 * 推送服务是否正常运行，条件是 连接状态，登陆过
	 *
	 * @return
	 */
	private boolean isXmppConnectionRunning() {
		if (mConnection != null) {
			L.d(TAG, "connection  isConnected:" + mConnection.isConnected());

		} else {
			L.d(TAG, "connection is null");
		}

		// KL.d(" isXmppConnectionRunning connect:" +
		// connection.isConnected());
		// KL.d(" isXmppConnectionRunning checkPingPong:" + checkPingPong());
		// KL.d(" isXmppConnectionRunning isAuthenticated:" +
		// connection.isAuthenticated());
		// 链接着的，并且是登录过的
		return mConnection != null && mConnection.isConnected() /*
																 * &&
																 * checkPingPong
																 * ()
																 */
				&& mConnection.isAuthenticated();
	}

	/**
	 * 销毁推送服务
	 */
	private void destroyXmppConnection() {
		L.d(TAG, "destroyXmppConnection()");
		try {
			if (mConnection != null) {
				mConnection.disconnect();
				mConnection = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 初始化推送服务，包括登录
     * @param userJid
     * @param userPwd
     */
	private void XmppConnectionInit(String userJid, String userPwd,String host,String serverName,
            int port,String resourceTag) {
		L.d(TAG, "XmppConnectionInit");
		L.d(TAG, "username:" + userJid);
		L.d(TAG, "userPwd:" + userPwd);
		mConnection = XmppManager.getInstance().openConnection(host,serverName,port,resourceTag);
//		mConnection = XmppManager.getInstance().getConnection();
		// 如果客户端被服务端踢了的话，connection 是空的
		if (mConnection == null || !mConnection.isConnected()) {
			return;
		}
		// 因为和pass平台是一样的用户名和密码，所以这儿传递的用户信息一定是正确的
		try {
			String jid = userJid;
//			if (userJid.contains("@")) {
//					jid = userJid.substring(0, userJid.lastIndexOf("@"));
//			}
//			XmppManager.getInstance().regist(jid, "1");
//			mConnection.login(userJid, "1", getOpenfireResourceTag());
			mConnection.login(userJid, userPwd, getOpenfireResourceTag());
			// 为groupservice注册 groupEventListener
			// TODO
			// groupService = XmppManager.getInstance().getGroupService();
//			groupService = new GroupService(mConnection, "group."
//					+ XmppManager.SERVER_NAME);

			// 添加链接监听
			mConnectionListener = new MyConnectionListener();
			mConnection.addConnectionListener(mConnectionListener);

			mConnection.addAsyncStanzaListener(this, new AndFilter(
				new StanzaTypeFilter(Message.class),
                    new DomainFilter("group.".concat(getOpenfireServiceName()))));

			// 处理离线消息
			List<Message> messages = getOfflineMessage();
			// TODO: 2015/11/24 需要从sp中取值设置
			// 设置在线状态
			// XmppManager.getInstance().setPresence(0);
			Presence presence = new Presence(Presence.Type.available);
			mConnection.sendStanza(presence);
			XmppService.this.onOfflineMessage(messages);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof org.jivesoftware.smack.sasl.SASLErrorException) {

				try {
					String jid = userJid;
					if (userJid.contains("@")) {
						jid = userJid.substring(0, userJid.lastIndexOf("@"));
					}
					register(jid,userPwd,getLocalUserNick(),getLocalUserEmail());
					mConnection.login(userJid, userPwd, getOpenfireResourceTag());


					// 处理离线消息
					List<Message> messages = getOfflineMessage();
					// TODO: 2015/11/24 需要从sp中取值设置
					// 设置在线状态
					// XmppManager.getInstance().setPresence(0);
					Presence presence = new Presence(Presence.Type.available);
					mConnection.sendStanza(presence);
					XmppService.this.onOfflineMessage(messages);

                    //解决一个账号登录几个设备的问题
					XmppManager.getInstance().setPresence(4);
				} catch (SmackException.NotConnectedException e1) {
					e1.printStackTrace();
				} catch (XMPPException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (SmackException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}
	private void register(String uname, String upassword,String nickname,String email) throws
			Exception {
		AccountManager acc = AccountManager.getInstance(mConnection);
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", nickname);
		attributes.put("email", email);
		acc.createAccount(uname, upassword, attributes);
//		acc.createAccount(uname, upassword);
	}

	public List<Message> getOfflineMessage() {
		if (mConnection == null)
			return null;
		List<Message> offlineMsgs = new ArrayList<Message>();
		try {
			om = new OfflineMessageManager(mConnection);
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
		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
		}

		return offlineMsgs;
	}

	/**
	 * 检测与服务器的心跳
	 *
	 * @return
	 */
	public boolean checkPingPong() {
		if (mConnection == null) {
			return false;
		}
		try {
			PingManager pingManager = PingManager.getInstanceFor(mConnection);
			if (pingManager == null) {
				return false;
			}
//            pingManager.setPingInterval(45);
			pingManager.setPingInterval(-1);// 设置心跳间隔
			return pingManager.pingMyServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 注册聊天消息监听器
	 */
	public void registerOnlineMessageListener() {
		L.d(TAG, "registerOnlineMessageListener");
		StanzaFilter filter = new StanzaTypeFilter(Message.class);
		StanzaListener listener = new StanzaListener() {

			@Override
			public void processPacket(Stanza packet)
					throws SmackException.NotConnectedException {
				if (packet instanceof Message) {
					Message msg = (Message) packet;

					if (msg.getType() == Message.Type.chat) {
						// 避免接收聊天状态的信息
						if (!TextUtils.isEmpty(msg.getBody())) {
							Log.w(TAG, " Message.Type.chat：" + msg.toXML());
							// TODO: 2015/11/23 加入线程池处理
							XmppService.this.onOnlineMessage(msg);
						}
					} else if (msg.getType() == Message.Type.groupchat) {
						L.d(TAG, " Message.Type.groupchat:" + msg.toXML());
						XmppService.this.onOnlineMessage(msg);
					} else if (msg.getType() == Message.Type.normal) {
						L.d(TAG, " Message.Type.normal:" + msg.toXML());
					} else if (msg.getType() == Message.Type.headline) {
						L.d(TAG, " Message.Type.headline:" + msg.toXML());
						// TODO: 2016/3/20 应用消息
						XmppService.this.onOnlineMessage(msg);
					} else if (msg.getType() == Message.Type.error) {
						L.d(TAG, " Message.Type.headline:" + msg.toXML());
					}
				}
			}
		};
		mConnection.addSyncStanzaListener(listener, filter);
	}

	/**
	 * 实现该方法，设置xmpp 登录的用户信息
	 *
	 * @return
	 */
	protected abstract String getLocalUserJid();

	/**
	 * 实现该方法，设置xmpp 登录的用户密码
	 *
	 * @return
	 */
	protected abstract String getLocalUserPwd();

	/**
	 * 获取用户的昵称
	 * @return
     */
	protected abstract String getLocalUserNick();

	/**
	 * 获取用户的邮箱
	 * @return
     */
	protected abstract String getLocalUserEmail();

	/**
	 * 是否是手动退出  true 是的，false 不是
	 *
	 * @return
	 */
	protected abstract boolean isLogout();

    /**
     * 获得openfire服务的host
     * @return
     */
    protected abstract String getOpenfireHost();

    /**
     * 获得openfire服务的port
     * @return
     */
    protected abstract int getOpenfirePort();
    /**
     * 获得openfire服务的域名
     * @return
     */
    protected abstract String getOpenfireServiceName();

    /**
     * 用于设置登陆时登录用户的资源符号，采用deviceId ,,如xxx@admin-pc/base109
     * @return
     */
    protected abstract String getOpenfireResourceTag();


	/**
	 * 推送服务 在线消息回调
	 *
	 * @param message
	 */
	protected void onOnlineMessage(Message message) {
		L.d(TAG, "onOnlineMessage");
		executorService.submit(new ListenerNotification(message));
	}

	/**
	 * 推送服务 离线消息回调
	 *
	 * @param messages
	 */
	protected void onOfflineMessage(List<Message> messages) {
		L.d(TAG, "onOfflineMessage");
		L.d(TAG, "onOfflineMessage size:" + messages.size());
		executorService.submit(new ListenerNotification(messages));
	}


	protected final Map<PushMessageListener, ListenerWrapper> recvListeners = new ConcurrentHashMap<PushMessageListener, ListenerWrapper>();

	public void addPushMessageListener(PushMessageListener msgListener,
			PushMessageFilter msgFilter) {
		if (msgListener == null) {
			throw new NullPointerException("Packet listener is null.");
		}
		ListenerWrapper wrapper = new ListenerWrapper(msgListener, msgFilter);
		recvListeners.put(msgListener, wrapper);
	}

	protected static class ListenerWrapper {

		private PushMessageListener packetListener;
		private PushMessageFilter packetFilter;

		public ListenerWrapper(PushMessageListener packetListener,
				PushMessageFilter packetFilter) {
			this.packetListener = packetListener;
			this.packetFilter = packetFilter;
		}

		public void notifyListenerOnlineMessage(Message message) {
			if (packetFilter == null
					|| packetFilter.acceptOnlineMessage(message)) {
				packetListener.processOnlineMessage(message);
			}
		}

		public void notifyListenerOfflineMessage(List<Message> messages) {
			if (packetFilter == null
					|| packetFilter.acceptOfflineMessage(messages)) {
				packetListener.processOfflineMessage(packetFilter
						.getAcceptOfflineMessage(messages));
			}
		}
	}

	private class ListenerNotification implements Runnable {

		private Message message;
		private List<Message> messages;

		public ListenerNotification(Message message) {
			L.d(TAG, " ListenerNotification");
			this.message = message;
		}

		public ListenerNotification(List<Message> messages) {
			this.messages = messages;
		}

		@Override
		public void run() {
			L.d(TAG, " recvListeners size:" + recvListeners.size());

			try {
				om.deleteMessages();
			} catch (SmackException.NoResponseException e) {
				e.printStackTrace();
			} catch (XMPPException.XMPPErrorException e) {
				e.printStackTrace();
			} catch (SmackException.NotConnectedException e) {
				e.printStackTrace();
			}
			for (ListenerWrapper listenerWrapper : recvListeners.values()) {

				if (message != null) {
					try {
						listenerWrapper.notifyListenerOnlineMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (messages != null) {
					try {
						listenerWrapper.notifyListenerOfflineMessage(messages);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private final static AtomicInteger connectionCounter = new AtomicInteger(0);

	private final int connectionCounterValue = connectionCounter
			.getAndIncrement();

	// private final ScheduledExecutorService executorService = new
	// ScheduledThreadPoolExecutor(2,
	// new SmackExecutorThreadFactory(connectionCounterValue));
	protected final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
			1, new SmackExecutorThreadFactory(connectionCounterValue));

	private static final class SmackExecutorThreadFactory implements
			ThreadFactory {
		private final int connectionCounterValue;
		private int count = 0;

		private SmackExecutorThreadFactory(int connectionCounterValue) {
			this.connectionCounterValue = connectionCounterValue;
		}

		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, "Smack Executor Service "
					+ count++ + " (" + connectionCounterValue + ")");
			thread.setDaemon(true);
			return thread;
		}
	}

	/**
	 * 连接监听
	 */
	class MyConnectionListener implements ConnectionListener {

		@Override
		public void connected(XMPPConnection connection) {
		}

		@Override
		public void authenticated(XMPPConnection connection, boolean resumed) {
		}

		@Override
		public void connectionClosed() {
			L.d(TAG, " connectionClosed()");
			L.d(TAG, " connection status:" + mConnection.isConnected());
			L.d(TAG, " connectionClosed() isLogout:" + isLogout());
			if (isLogout()) {
				 XmppManager.getInstance().deleteAccount();
			} else {
				try {
					XmppConnectionInit(getLocalUserJid(), getLocalUserPwd(),getOpenfireHost(),
                            getOpenfireServiceName(),getOpenfirePort(),getOpenfireResourceTag());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void connectionClosedOnError(Exception e) {
			if(e.getMessage().contains("conflict")){
				XmppService.this.sendBroadcast(new Intent("com.yxst.epic.unifyplatform.conflict"));
				isconflict =true;
				return;
			}

			try {
				XmppConnectionInit(getLocalUserJid(), getLocalUserPwd(),getOpenfireHost(),
						getOpenfireServiceName(),getOpenfirePort(),getOpenfireResourceTag());
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		@Override
		public void reconnectionSuccessful() {
		}

		@Override
		public void reconnectingIn(int seconds) {
		}

		@Override
		public void reconnectionFailed(Exception e) {
		}
	}
}
