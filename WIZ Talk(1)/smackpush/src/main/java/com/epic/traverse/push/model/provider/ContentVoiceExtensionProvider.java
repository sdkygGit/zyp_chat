package com.epic.traverse.push.model.provider;

import android.text.TextUtils;

import com.epic.traverse.push.model.Extension.ContentVoiceExtension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by Dong on 2015/11/30.
 */ 
public class ContentVoiceExtensionProvider extends EmbeddedExtensionProvider<ContentVoiceExtension> {
    @Override
    protected ContentVoiceExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        ContentVoiceExtension cve = new ContentVoiceExtension();
        String mold = attributeMap.get("mold");
        if (!TextUtils.isEmpty(mold)){
            try {
                int m = Integer.parseInt(mold);
                cve.setMold(m);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cve.setMold(1);
            }
        }else{
            cve.setMold(1);
        }

        String voiceLength = attributeMap.get("voiceLength");
        if (!TextUtils.isEmpty(voiceLength)){
            try {
                int length = Integer.parseInt(voiceLength);
                cve.setVoiceLength(length);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cve.setVoiceLength(1);
            }
        }

        String filePath = attributeMap.get("filePath");
        String fileExtention = attributeMap.get("fileExtention");
        String fileMimeType = attributeMap.get("fileMimeType");

        cve.setFilePath(filePath);
        cve.setFileExtention(fileExtention);
        cve.setFileMimeType(fileMimeType);
        return cve;

    }
    private String filePath;
    private String fileExtention;
    private String fileMimeType;
}
