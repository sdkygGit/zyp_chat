package com.epic.traverse.push.model.provider;

import android.text.TextUtils;

import com.epic.traverse.push.model.Extension.ContentImageExtension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by Dong on 2015/11/30.
 */
public class ContentImageExtensionProvider extends EmbeddedExtensionProvider<ContentImageExtension> {
    @Override
    protected ContentImageExtension createReturnExtension(String currentElement, 
    		String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        ContentImageExtension cie = new ContentImageExtension();
        String mold = attributeMap.get("mold");
        if (!TextUtils.isEmpty(mold)){
            try {
                int m = Integer.parseInt(mold);
                cie.setMold(m);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cie.setMold(1);
            }
        }else{
            cie.setMold(1);
        }

        String width = attributeMap.get("width");
        if (!TextUtils.isEmpty(width)){
            try {
                int w = Integer.parseInt(width);
                cie.setWidth(w);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cie.setWidth(1);
            }
        }

        String height = attributeMap.get("height");
        if (!TextUtils.isEmpty(height)){
            try {
                int h = Integer.parseInt(height);
                cie.setHeight(h);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                cie.setHeight(1);
            }
        }
        String filePath = attributeMap.get("filePath");
        String fileExtention = attributeMap.get("fileExtention");
        String fileMimeType = attributeMap.get("fileMimeType");
        cie.setFilePath(filePath);
        cie.setFileExtention(fileExtention);
        cie.setFileMimeType(fileMimeType);
        return cie;
    }


}
