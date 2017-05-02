package com.wiz.dev.wiztalk.service.listener;

import android.content.Context;
import android.content.Intent;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.skysea.group.Group;
import com.skysea.group.GroupEventListener;
import com.skysea.group.GroupService;
import com.skysea.group.MemberInfo;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

/**
 * 
 * @author Admin
 */
public class CustomGroupEventListener implements GroupEventListener {

	private static final String TAG = CustomGroupEventListener.class
			.getSimpleName();

	public final static String GROUP_OPERATE_CREATED = "group_operate_created";
	public final static String GROUP_OPERATE_DESTROYED = "group_operate_destroyed";
	public final static String GROUP_OPERATE_MEMBER_JOINED = "group_operate_member_joined";
	public final static String GROUP_OPERATE_MEMBER_EXITED = "group_operate_member_exited";
	public final static String GROUP_OPERATE_MEMBER_KICKED = "group_operate_member_kicked";
	public final static String GROUP_OPERATE_MEMBER_NICKNAME_CHANGED = "group_operate_member_nickname_changed";
	public final static String GROUP_OPERATE_MEMBER_APPLY_ARRIVED = "group_operate_member_apply_arrived";
	public final static String GROUP_OPERATE_MEMBER_APPLY_PROCESSED = "group_operate_member_apply_processed";

	private Context mContext;

	public CustomGroupEventListener(Context context) {
		this.mContext = context;
	}

	@Override
	public void created(String groupJid, DataForm createForm) {
		// TODO Auto-generated method stub
		L.d(TAG, "created:" + groupJid);

	}

	@Override
	public void destroyed(String groupJid, String from, String reason) {
		// TODO Auto-generated method stub
		L.d(TAG, "destroyed:" + groupJid);
		L.d(TAG, "reason:" + reason);
		L.d(TAG, "from :" + from); // from :xujianxue@admin-pc

		// 自己的操作
		if (from.equals(MyApplication.getInstance().getOpenfireJid())) {
			return;
		}

		Intent intent = new Intent();
		intent.setAction(GROUP_OPERATE_DESTROYED);
		intent.putExtra("gourpJid", groupJid);
		intent.putExtra("from", from);
		intent.putExtra("reason", reason);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 直接插库 提示消息
	 */
	@Override
	public void memberJoined(String groupJid, MemberInfo member) {
		// TODO Auto-generated method stub
		L.d(TAG, "memberJoined:" + member.getUserName());
		L.d(TAG, "memberJoined  groupJid:" + groupJid);
		// exclude current user

		if (MyApplication.getInstance().getOpenfireJid()
				.contains(member.getUserName())) {
			return;
		}

		XmppMessage xmsg = createTipsMessage(groupJid, member.getNickname()
				+ " 加入本群！");
		long xmsgId = XmppDbManager.getInstance(mContext).insertMessage(xmsg);

		L.d(TAG, "msg xmsgId:" + xmsgId);
		if (xmsgId != 0) {
			mContext.getContentResolver().notifyChange(
					XmppMessageContentProvider.CONTENT_URI, null);
		}
	}

	/**
	 * 直接插库
	 */
	@Override
	public void memberExited(String groupJid, MemberInfo member, String reason) {
		// TODO Auto-generated method stub
		L.d(TAG, "memberExited:" + groupJid);

		if (MyApplication.getInstance().getOpenfireJid()
				.contains(member.getUserName())) {
			return;
		}

		XmppMessage xmsg = createTipsMessage(groupJid, member.getNickname()
				+ " 退出本群！");
		long xmsgId = XmppDbManager.getInstance(mContext).insertMessage(xmsg);
		if (xmsgId > 0) {
			mContext.getContentResolver().notifyChange(
					XmppMessageContentProvider.CONTENT_URI, null);
		}
	}

	/**
	 * 直接插库
	 */
	@Override
	public void memberKicked(String groupJid, MemberInfo member, String from,
			String reason) {
		// TODO Auto-generated method stub
		L.d(TAG, "memberKicked:" + groupJid);
		if (MyApplication.getInstance().getOpenfireJid()
				.contains(member.getUserName())) {
			Intent intent = new Intent();
			intent.setAction(GROUP_OPERATE_MEMBER_KICKED);
			intent.putExtra("gourpJid", groupJid);
			intent.putExtra("username", member.getUserName());
			intent.putExtra("from", from);
			intent.putExtra("reason", reason);
			mContext.sendBroadcast(intent);
		} else {
			XmppMessage xmsg = createTipsMessage(groupJid, member.getNickname()
					+ " 被管理员踢出本群！");
			long xmsgId = XmppDbManager.getInstance(mContext).insertMessage(
					xmsg);
			if (xmsgId > 0) {
				mContext.getContentResolver().notifyChange(
						XmppMessageContentProvider.CONTENT_URI, null);
			}
		}

	}

	/**
	 * 直接插库
	 */
	@Override
	public void memberNicknameChanged(String groupJid, MemberInfo member,
			String newNickname) {
		L.d(TAG, "memberNicknameChanged:" + groupJid);
	}

	@Override
	public void applyArrived(String groupJid, String id, MemberInfo member,
			String reason) {
		L.d(TAG, "applyArrived:" + groupJid);
		Intent intent = new Intent();
		intent.setAction(GROUP_OPERATE_MEMBER_APPLY_ARRIVED);
		intent.putExtra("gourpJid", groupJid);
		intent.putExtra("id", id);
//		intent.putExtra("member", member);
		intent.putExtra("reason", reason);
		mContext.sendBroadcast(intent);
	}

	@Override
	public void applyProcessed(String groupJid, boolean agree, String from,
			String reason) {
		L.d(TAG, "applyProcessed:" + groupJid);
		Intent intent = new Intent();
		intent.setAction(GROUP_OPERATE_MEMBER_APPLY_PROCESSED);
		intent.putExtra("gourpJid", groupJid);
		intent.putExtra("agree", agree);
		intent.putExtra("from", from);
		intent.putExtra("reason", reason);
		mContext.sendBroadcast(intent);
	}

	private XmppMessage createTipsMessage(String groupJid, String tips) {
		XmppMessage msg = new XmppMessage();
		// 写 反了 看效果
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
		msg.setExtLocalUserName(MyApplication.getInstance().getOpenfireJid()
				.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME));

		msg.setExtRemoteUserName(groupJid);

		try {
			GroupService groupService = XmppManager.getInstance()
					.getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
			Group group = groupService.getGroup(groupJid);

			DataForm dataForm = group.getInfo();

			Form form = new Form(dataForm);

			// TODO 必须要得到群昵称
			msg.setExtRemoteDisplayName(form.getField("name").getValues()
					.get(0));
			// msg.setExtRemoteDisplayName(groupJid);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		}

		return msg;
	}
}
