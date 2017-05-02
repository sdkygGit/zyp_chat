package com.epic.traverse.push.model.Extension;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by Dong on 2015/11/25.
 */
public class ResponseUploadExtension implements ExtensionElement {

    public static final String NAME = "responseUpload";
    public static final String NAME_SPACE = "com:epic";

    private String fid;
    private String fileName;
    private String fileUrl;
    private long size;
    private String error;
    
    public ResponseUploadExtension(){}

    public ResponseUploadExtension(String fid, String fileName, String fileUrl, long size) {
        this.fid = fid;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.size = size;
    }


    public ResponseUploadExtension(String error) {
        this.error = error;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }
   
    @Override
    public CharSequence toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(NAME).append(" xmlns=\"").append(NAME_SPACE).append("\"");;
        sb.append(" fid=\"").append(fid).append("'");
        sb.append(" fileName=\"").append(fileName).append("\"");
        sb.append(" fileUrl=\"").append(fileUrl).append("\"");
        sb.append(" size=\"").append(size).append("\"");
        sb.append(" error=\"").append(error).append("\"");
        sb.append(" >");
        sb.append("</").append(NAME).append(">");
        return sb.toString();
    }
}
