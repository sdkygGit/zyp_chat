package com.epic.traverse.push.services;

import org.jivesoftware.smack.packet.Message;

import java.util.List;

public interface PushMessageListener {

	public void processOnlineMessage(Message message);

	public void processOfflineMessage(List<Message> messages);
}
