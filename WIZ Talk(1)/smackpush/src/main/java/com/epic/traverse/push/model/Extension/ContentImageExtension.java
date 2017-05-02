package com.epic.traverse.push.model.Extension;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by Dong on 2015/11/25.
 */
public class ContentImageExtension implements ExtensionElement {

    public static final String NAME = "contentImage";
    public static final String NAME_SPACE = "com:epic";

    private int mold; //消息类型
    private int width; //图片宽
    private int height; //图片高

    private String filePath; //url
    private String fileExtention;
    private String fileMimeType;

    public ContentImageExtension() {
    }

    public ContentImageExtension(int mold, int width, int height, String filePath, String fileExtention, String fileMimeType) {
        this.mold = mold;
        this.width = width;
        this.height = height;
        this.filePath = filePath;
        this.fileExtention = fileExtention;
        this.fileMimeType = fileMimeType;
    }

    public int getMold() {
        return mold;
    }

    public void setMold(int mold) {
        this.mold = mold;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

    @Override
    public String getElementName() {
        return NAME;
    }
//    int mold; //消息类型
//    int width; //图片宽
//    int height; //图片高
//    String filePath; //url
//    String fileExtention;
//    String fileMimeType;
    @Override
    public CharSequence toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(NAME).append(" xmlns=\"").append(NAME_SPACE).append("\"");;
        sb.append(" mold=\"").append(mold).append("\"");
        sb.append(" width=\"").append(width).append("\"");
        sb.append(" height=\"").append(height).append("\"");
        sb.append(" filePath=\"").append(filePath).append("\"");
        sb.append(" fileExtention=\"").append(fileExtention).append("\"");
        sb.append(" fileMimeType=\"").append(fileMimeType).append("\"");
        sb.append(" >");
        sb.append("</").append(NAME).append(">");
        return sb.toString();
    }
}
