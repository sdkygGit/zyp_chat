package com.epic.traverse.push.model.Extension;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by Dong on 2015/11/25.
 */
public class ContentVoiceExtension implements ExtensionElement {
    public static final String NAME = "contentVoice";
    public static final String NAME_SPACE = "com:epic";

    private int mold; //消息类型
    private String filePath;
    private String fileExtention;
    private String fileMimeType;
    private long voiceLength;//语音的时长
    
    public ContentVoiceExtension(){
    	
    }
    
    public ContentVoiceExtension(int mold, String filePath, String fileExtention, String fileMimeType, long voiceLength) {
        this.mold = mold;
        this.filePath = filePath;
        this.fileExtention = fileExtention;
        this.fileMimeType = fileMimeType;
        this.voiceLength = voiceLength;
    }

    public int getMold() {
        return mold;
    }

    public void setMold(int mold) {
        this.mold = mold;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileExtention() {
        return fileExtention;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }

    public String getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public long getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(long voiceLength) {
        this.voiceLength = voiceLength;
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

    @Override
    public String getElementName() {
        return NAME;
    }
   
    @Override
    public CharSequence toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(NAME).append(" xmlns=\"").append(NAME_SPACE).append("\"");;
        sb.append(" mold=\"").append(mold).append("\"");
        sb.append(" filePath=\"").append(filePath).append("\"");
        sb.append(" fileExtention=\"").append(fileExtention).append("\"");
        sb.append(" fileMimeType=\"").append(fileMimeType).append("\"");
        sb.append(" voiceLength=\"").append(voiceLength).append("\"");
        sb.append(" >");
        sb.append("</").append(NAME).append(">");
        return sb.toString();
    }
}
