package com.epic.traverse.push.model.provider;

import android.text.TextUtils;

import com.epic.traverse.push.model.Extension.MessageExtension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by Dong on 2015/11/26.
 */
public class MessageExtensionProvider extends EmbeddedExtensionProvider<MessageExtension> {

    @Override
    protected MessageExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        MessageExtension messageExtension = new MessageExtension();
        String createTime = attributeMap.get("createTime");
        if (!TextUtils.isEmpty(createTime)) {
            try {
                long creatT = Long.parseLong(createTime);
                messageExtension.setCreateTime(creatT);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                messageExtension.setCreateTime(System.currentTimeMillis());
            }
        }else{
            messageExtension.setCreateTime(System.currentTimeMillis());
        }
        
        String mold = attributeMap.get("mold");
        if (!TextUtils.isEmpty(mold)) {
            try {
                int m = Integer.parseInt(mold);
                messageExtension.setMold(m);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                messageExtension.setMold(0);
            }
        }else {
            messageExtension.setMold(0);
        }
        String displayName = attributeMap.get("displayName");
        if (!TextUtils.isEmpty(displayName)) {
            messageExtension.setDisplayName(displayName);
        }
        String senderNickname = attributeMap.get("senderNickname");
        if (!TextUtils.isEmpty(displayName)) {
            messageExtension.setSenderNickname(senderNickname);
        }
        String receiverNickName = attributeMap.get("receiverNickName");
        if (!TextUtils.isEmpty(displayName)) {
            messageExtension.setSenderNickname(receiverNickName);
        }
        
        return messageExtension;
    }
   
}
