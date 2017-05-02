package com.epic.traverse.push.services;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import com.skysea.group.packet.NotifyPacketExtension;
import com.skysea.group.packet.notify.GroupDestroyedNotify;
import com.skysea.group.packet.notify.MemberApplyResultNotify;
import com.skysea.group.packet.notify.MemberApplyToJoinNotify;
import com.skysea.group.packet.notify.MemberExitedNotify;
import com.skysea.group.packet.notify.MemberInviteNotify;
import com.skysea.group.packet.notify.MemberJoinedNotify;
import com.skysea.group.packet.notify.MemberKickedNotify;
import com.skysea.group.packet.notify.MemberProfileChangedNotify;
import com.skysea.group.packet.notify.Notify;

/**
 * 事件分派器。 Created by dong on 2014/9/22.
 */
public interface  GroupOperateEventListener extends StanzaListener {

	/*public GroupOperateEventListener(XMPPConnection connection, String domain) {
		assert connection != null;
		assert domain != null;

		 监听所有从圈子服务发送过来的Message，并自动触发事件 
		connection.addAsyncStanzaListener(this, new AndFilter(
				new StanzaTypeFilter(Message.class), new DomainFilter(domain)));
	}

	*//**
	 * Xmpp消息监听回调。
	 * 
	 * @param packet
	 * @throws SmackException.NotConnectedException
	 *//*
	@Override
	public void processPacket(Stanza packet)
			throws SmackException.NotConnectedException {
		Notify notify = getNotifyFromExtensions(packet);

		if (notify != null) {
			dispatch(packet.getFrom(), notify);
		}
	}*/

	/**
	 * 从packet中取得Notify扩展信息。
	 * 
	 * @param packet
	 * @return
	 */
	/*public Notify getNotifyFromExtensions(Stanza packet) {

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
//			dispatchProfile(jid, (MemberProfileChangedNotify) notify);
			break;
		case MEMBER_APPLY_TO_JOIN:
			dispatchApply(jid, (MemberApplyToJoinNotify) notify);
			break;
		case MEMBER_APPLY_TO_JOIN_RESULT:
			dispatchApplyResult(jid, (MemberApplyResultNotify) notify);
			break;
		case GROUP_DESTROY:
			dispatchDestroy(jid, (GroupDestroyedNotify) notify);
			break;
		default:
			break;
		}
	}*/

	public abstract void dispatchCreate(String jid, DataForm createFrom);

	public abstract void dispatchInvite(String jid, MemberInviteNotify notify);
	
	public abstract void dispatchKicked(String jid, MemberKickedNotify notify);
	
	public abstract void dispatchExited(String jid, MemberExitedNotify notify) ;

	public abstract void dispatchJoined(String jid, MemberJoinedNotify notify);

	public abstract void  dispatchApply(String jid, MemberApplyToJoinNotify notify) ;

	public abstract void dispatchDestroy(String jid, GroupDestroyedNotify notify);

	public abstract void dispatchApplyResult(String jid, MemberApplyResultNotify notify);

	/**
	 * Packet的域名过滤器。
	 */
	static class DomainFilter implements StanzaFilter {
		private final String domain;

		public DomainFilter(String domain) {
			assert domain != null;
			this.domain = domain;
		}

		@Override
		public boolean accept(Stanza packet) {
			String from = packet.getFrom();
			assert from != null;
			return domain.equals(from) || domainEquals(from);
		}

		private boolean domainEquals(String from) {
			// 注意大小写问题，Resource问题。
			return from.endsWith(domain)
					&& (from.length() > domain.length() && from.charAt(from
							.length() - domain.length() - 1) == '@');
		}
	}
}
