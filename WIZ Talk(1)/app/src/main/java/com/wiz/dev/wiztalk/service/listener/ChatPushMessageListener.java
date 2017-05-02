package com.wiz.dev.wiztalk.service.listener;

import android.content.Context;

import com.epic.traverse.push.services.PushMessageListener;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;

import org.jivesoftware.smack.packet.Message;

import java.util.List;

/**
 * Created by Admin on 2015/9/10.
 */
public class ChatPushMessageListener implements PushMessageListener {

    private Context mContext;

    //如果需要过滤处理其他消息，在此处添加其他队Listener和filter
    ChatPushVoiceMessageFilter voiceMessageFilter;
    ChatPushVoiceMessageListener voiceMessageListener;

    public ChatPushMessageListener(Context context) {
        this.mContext = context;
        voiceMessageFilter = new ChatPushVoiceMessageFilter();
        voiceMessageListener = new ChatPushVoiceMessageListener(context);
    }

    @Override
    public void processOnlineMessage(Message message) {
        //存入数据库
        XmppMessage xmppMessage = XmppDbManager.getInstance(mContext).onOnlineMessage(message);
        //使用其他Listener对message进行判断处理。
        if (xmppMessage != null && xmppMessage.getMold() == MsgInFo.MOLD_VOICE) {
            if (voiceMessageFilter.acceptOnlineMessage(message)) {
                voiceMessageListener.processOnlineMessage(message);
            }
        }
    }

    @Override
    public void processOfflineMessage(List<Message> messages) {
        //先存入数据库再处理
        XmppDbManager.getInstance(mContext).onOfflineMessage(messages);
        boolean acceptVoiceMsg = voiceMessageFilter.acceptOfflineMessage(messages);
        if (acceptVoiceMsg) {
            voiceMessageListener.processOfflineMessage(messages);
        }
    }
}
