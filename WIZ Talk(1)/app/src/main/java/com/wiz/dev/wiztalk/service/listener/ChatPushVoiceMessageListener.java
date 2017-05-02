package com.wiz.dev.wiztalk.service.listener;

import android.content.Context;
import android.text.TextUtils;

import com.epic.traverse.push.services.PushMessageListener;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.utils.MessageUtils;

import org.jivesoftware.smack.packet.Message;

import java.util.List;

/**
 * Created by Dong on 2015/10/14.
 */
public class ChatPushVoiceMessageListener implements PushMessageListener {

    private Context mContext;
    public ChatPushVoiceMessageListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void processOnlineMessage(Message message) {
        // TODO: 2015/11/24 
        XmppMessage xmppMessage = XmppDbManager.getInstance(mContext).getXmppMessageBySid(message.getStanzaId());
        if(xmppMessage ==null) return;
        if (xmppMessage.getMold() == MsgInFo.MOLD_VOICE) {
        	if (!TextUtils.isEmpty(xmppMessage.getFilePath())) {
        		  MessageUtils.receiveMediaMsg(mContext, xmppMessage);
			}
          
        }
    }
    @Override
    public void processOfflineMessage(List<Message> messages) {
        for (Message msg : messages) {
            this.processOnlineMessage(msg);
        }
    }
}
