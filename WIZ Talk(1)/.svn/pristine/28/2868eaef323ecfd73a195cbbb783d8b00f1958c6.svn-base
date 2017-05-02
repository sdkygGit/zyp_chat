package com.skysea.group;

import com.skysea.group.packet.NotifyPacketExtension;
import com.skysea.group.packet.notify.*;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 事件分派器。
 * Created by zhangzhi on 2014/9/22.
 */
final class EventDispatcher implements PacketListener {
    private final ConcurrentLinkedQueue<GroupEventListener> listeners =
            new ConcurrentLinkedQueue<GroupEventListener>();

    public EventDispatcher(XMPPConnection connection, String domain) {
        assert connection != null;
        assert domain != null;

        /* 监听所有从圈子服务发送过来的Message，并自动触发事件 */
        connection.addPacketListener(this,
                new AndFilter(new PacketTypeFilter(Message.class),
                        new DomainFilter(domain)));
    }

    /**
     * 添加事件监听器。
     * @param listener
     */
    public void addEventListener(GroupEventListener listener) {
        assert listener != null;
        listeners.add(listener);
    }

    /**
     * 删除事件监听器。
     * @param listener
     */
    public void removeEventListener(GroupEventListener listener) {
        assert listener != null;
        listeners.remove(listener);
    }

    /**
     * 获得事件监听器列表。
     * @return
     */
    public Collection<GroupEventListener> getEventListeners() {
        return Collections.unmodifiableCollection(listeners);
    }

    /**
     * Xmpp消息监听回调。
     * @param packet
     * @throws NotConnectedException
     */
    public void processPacket(Stanza packet) throws NotConnectedException {
        Notify notify = getNotifyFromExtensions(packet);

        if (notify != null) {
            dispatch(packet.getFrom(), notify);
        }
    }

    /**
     * 从packet中取得Notify扩展信息。
     * @param packet
     * @return
     */
    private Notify getNotifyFromExtensions(Packet packet) {
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
                dispatchJoined(jid, (MemberJoinedNotify)notify);
                break;
            case MEMBER_EXITED:
                dispatchExited(jid, (MemberExitedNotify)notify);
                break;
            case MEMBER_KICKED:
                dispatchKicked(jid, (MemberKickedNotify)notify);
                break;
            case MEMBER_PROFILE_CHANGED:
                dispatchProfile(jid, (MemberProfileChangedNotify)notify);
                break;
            case MEMBER_APPLY_TO_JOIN:
                dispatchApply(jid, (MemberApplyToJoinNotify)notify);
                break;
            case MEMBER_APPLY_TO_JOIN_RESULT:
                dispatchApplyResult(jid, (MemberApplyResultNotify)notify);
                break;
            case GROUP_DESTROY:
                dispatchDestroy(jid, (GroupDestroyedNotify) notify);
                break;
        }
    }

    void dispatchCreate(String jid, DataForm createFrom) {
        for (GroupEventListener listener:listeners) {
            listener.created(jid, createFrom);
        }
    }

    private void dispatchProfile(String jid, MemberProfileChangedNotify notify) {
        for (GroupEventListener listener:listeners) {
            listener.memberNicknameChanged(jid, notify.getMemberInfo(), notify.getNewNickname());
        }
    }

    private void dispatchKicked(String jid, MemberKickedNotify notify) {
        for (GroupEventListener listener:listeners) {
            listener.memberKicked(jid, notify.getMemberInfo(), notify.getFrom(), notify.getReason());
        }
    }

    private void dispatchExited(String jid, MemberExitedNotify notify) {
        for (GroupEventListener listener:listeners) {
            listener.memberExited(jid, notify.getMemberInfo(), notify.getReason());
        }
    }

    private void dispatchJoined(String jid, MemberJoinedNotify notify) {
        for (GroupEventListener listener:listeners) {
            listener.memberJoined(jid, notify.getMemberInfo());
        }
    }

    private void dispatchApply(String jid, MemberApplyToJoinNotify notify) {
        for (GroupEventListener listener : listeners) {
            listener.applyArrived(jid, notify.getId(), notify.getMemberInfo(), notify.getReason());
        }
    }

    private void dispatchDestroy(String jid, GroupDestroyedNotify notify) {
        for (GroupEventListener listener:listeners) {
            listener.destroyed(jid, notify.getFrom(), notify.getReason());
        }
    }

    private void dispatchApplyResult(String jid, MemberApplyResultNotify notify) {
        for (GroupEventListener listener:listeners) {
            listener.applyProcessed(jid, notify.getResult(), notify.getFrom(), notify.getReason());
        }
    }


    /**
     * Packet的域名过滤器。
     */
    static class DomainFilter implements PacketFilter {
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

            //return domain.equalsIgnoreCase(StringUtils.parseServer(packet.getFrom()));
        }

        private boolean domainEquals(String from) {
            // 注意大小写问题，Resource问题。
            return from.endsWith(domain) &&
                    (from.length() > domain.length() &&
                            from.charAt(from.length() - domain.length() - 1) == '@');
        }

    }

}
