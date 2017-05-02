package com.epic.traverse.push.model.Extension;

import org.jivesoftware.smack.packet.ExtensionElement;
//EmbeddedExtensionProvider

/**
 * 消息的扩展类
 * Created by Dong on 2015/11/24.
 */
public class MessageExtension implements ExtensionElement {

    public static final String NAME = "msgExtension";
    public static final String NAME_SPACE = "com:epic";

    private Long createTime; //消息创建的时间
    private int mold; //chat 的类型，0：文本；1：语音；2：图片；3,4,5...应用
    
    //单聊时，发送者的nickname
    //群聊时，群的昵称
    private String displayName;//from用户的显示名称。
    
    private String receiverNickName;//单聊时，接收者Nick：
    private String senderNickname;//群聊时，发送人的Nick

    private Long showTime;

    public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getReceiverNickName() {
        return receiverNickName;
    }


    public Long getShowTime(){return showTime;}

    public void setShowTime(Long showTime){this.showTime = showTime;}

    public void setReceiverNickName(String receiverNickName) {
        this.receiverNickName = receiverNickName;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public int getMold() {
        return mold;
    }

    public void setMold(int mold) {
        this.mold = mold;
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

    @Override
    public String getElementName() {
        return NAME;
    }
    /*int direct; //0：接收的；1：发送的
    Long createTime; //消息创建的时间
    Long localTime;//达到本地的时间 ,因为存在离线消息
    int mold; //chat 的类型，0：文本；1：语音；2：图片；3,4,5...应用消息
    int status; //消息的各种状态，，准备发送，正在发送，发送成功，发送失败
    Object objectContent;//文件内容*/
    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(NAME).append(" xmlns=\"").append(NAME_SPACE).append("\"");;
        sb.append(" createTime=\"").append(createTime).append("\"");
        sb.append(" showTime=\"").append(showTime).append("\"");
        sb.append(" mold=\"").append(mold).append("\"");
        sb.append(" displayName=\"").append(displayName).append("\"");
        sb.append(" senderNickname=\"").append(senderNickname).append("\"");
        sb.append(" receiverNickName=\"").append(showTime).append("\"");
        sb.append(" >");
        sb.append("</").append(NAME).append(">");
        return sb.toString();
    }
    public MessageExtension(){}
    public MessageExtension( Long createTime, int mold) {
        this.createTime = createTime;
        this.mold = mold;
    }
}
