package com.wiz.dev.wiztalk.service.listener;

import com.epic.traverse.push.services.PushMessageFilter;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤消息并存储
 * Created by Admin on 2015/9/10.
 */
public class ChatPushMessageFilter implements PushMessageFilter {
    @Override
    public boolean acceptOnlineMessage(Message message) {
        if (message != null) {
            if (message.getBody() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean acceptOfflineMessage(List<Message> messages) {
        ArrayList<Message> list = this.getAcceptOfflineMessage(messages);
        if (list != null || list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Message> getAcceptOfflineMessage(List<Message> messages) {
        ArrayList<Message> list = new ArrayList<Message>();
        for (Message msg : messages) {
            if (this.acceptOnlineMessage(msg)) {
                list.add(msg);
            }
        }
        return list;
    }
}
