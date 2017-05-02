package com.epic.traverse.push.model.provider;

import android.text.TextUtils;

import com.epic.traverse.push.model.Extension.ResponseUploadExtension;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by Dong on 2015/11/30.
 */
public class ResponseUploadExtensionProvider extends EmbeddedExtensionProvider<ResponseUploadExtension> {
    @Override
    protected ResponseUploadExtension createReturnExtension(String currentElement, String 
            currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {

        ResponseUploadExtension rue = new ResponseUploadExtension();
        String size = attributeMap.get("size");
        if (!TextUtils.isEmpty(size)) {
            try {
                int f = Integer.parseInt(size);
                rue.setSize(f);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                rue.setSize(0);
            }
        }else{
            rue.setSize(0);
        }

        String fid = attributeMap.get("fid");
        String fileName = attributeMap.get("fileName");
        String fileUrl = attributeMap.get("fileUrl");
        String error = attributeMap.get("error");

        if (!TextUtils.isEmpty(fid)) {
            rue.setFid(fid);
            rue.setFileName(fileName);
            rue.setFileUrl(fileUrl);
        }else if (TextUtils.isEmpty(error)) {
            rue.setError(error);
        }
        
        return rue;
       
    }
}
