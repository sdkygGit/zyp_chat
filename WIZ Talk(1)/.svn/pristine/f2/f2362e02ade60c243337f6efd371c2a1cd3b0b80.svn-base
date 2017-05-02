package com.epic.traverse.push.services;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

public interface PushMessageFilter {

	public boolean acceptOnlineMessage(Message message);

	public boolean acceptOfflineMessage(List<Message> messages);
	public ArrayList<Message> getAcceptOfflineMessage(List<Message> messages);
}
