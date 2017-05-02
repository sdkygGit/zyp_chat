package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epic.traverse.push.util.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.util.RoundImageView;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.utils.DraftConfig;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.SmileyParser;
import com.wiz.dev.wiztalk.utils.TimeUtils;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 最近聊天项
 * Created by Dong on 2015/10/13. R.layout.item_conversation
 */
@EViewGroup(R.layout.item_conversation)
public class RecentChatItemView extends RelativeLayout {

    private static final String TAG = "RecentChatItemView";

    @ViewById
    ImageView iv_recent_avatar;

    @ViewById
    TextView tv_recent_name;
    @ViewById
    TextView tv_recent_msg;
    @ViewById
    TextView tv_recent_time;
    @ViewById
    TextView tv_recent_unread;

    @ViewById
    ImageView un_send_iv;

    private Context mContext;

    private XmppMessage message;


    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int sizeBigger;

    @DimensionPixelOffsetRes(R.dimen.icon_size_small)
    int sizeSmall;

    DraftConfig draftConfig;

    public RecentChatItemView(Context context) {
        super(context);
        this.mContext = context;
        draftConfig = new DraftConfig(context);
    }

    public void bind(XmppMessage msg, int unRead) {

//        String username2 = null;
//        username2 = msg.getExtRemoteUserName().split("@")[0];
//        username2 = username2.concat(Member.SUFFIX_USER);
//        Intent intent = new Intent("msg");
//        intent.putExtra("unread",unRead);
//        intent.putExtra("name",username2);
//        intent.putExtra("getExtRemoteUserName",msg.getExtRemoteUserName());
//        intent.putExtra("getExtRemoteDisplayName",msg.getExtRemoteDisplayName());
//        mContext.sendBroadcast(intent);

        L.d(TAG, msg.toString());
        this.message = msg;
        if (msg.getType().toString().equals(MsgInFo.TYPE_GROUP_OPERATE)) {
            tv_recent_msg.setText(msg.getExtGroupOperateUserNick() + " " + msg.getBody() + " " + msg
                    .getExtLocalDisplayName());
        } else if (msg.getType().toString().equals(MsgInFo.TYPE_HEADLINE)) {
            //应用消息
            tv_recent_msg.setText(msg.getBody());


        } else {//聊天消息
//	      转换 emot表情
           /* SpannableString spannableString = FaceTextUtils
	                .toSpannableString(mContext, msg.getBody());
	        tv_recent_msg.setText(spannableString);*/

            String msgBody = SmileyParser.getInstance().addSmileySpans(msg.getBody()).toString();
            try {

                if (!TextUtils.isEmpty(draftConfig.getStringConfig(msg.getExtRemoteUserName() + "@" + MyApplication.getInstance().getOpenfireJid()))) {
                    tv_recent_msg.setText(draftConfig.getStringConfig(msg.getExtRemoteUserName() + "@" + MyApplication.getInstance().getOpenfireJid()));
                    un_send_iv.setVisibility(View.VISIBLE);
                } else {
                    un_send_iv.setVisibility(View.GONE);
                    if (msgBody.split("@")[0].equals(ConstDefine.location_pwd)) {
                        JSONObject jsonObject = new JSONObject(msgBody.split("@")[1]);
                        if (!TextUtils.isEmpty(jsonObject.getString("latitude")) && !TextUtils.isEmpty(jsonObject.getString("longitude")) && !TextUtils.isEmpty((String) jsonObject.getString("localName"))) {
                            tv_recent_msg.setText("[位置]");
                        } else {
                            tv_recent_msg.setText(SmileyParser.getInstance().addSmileySpans(msg.getBody()));
                        }
                    } else if (msgBody.split("#")[0].equals(SaveConfig.SHARE_PWD)) {
                        tv_recent_msg.setText("[分享]");
                    } else {
                        JSONObject jsonObject = new JSONObject(msgBody);
                        String name = jsonObject.getString("filename");
                        if(!TextUtils.isEmpty(jsonObject.getString("filename")) && !TextUtils.isEmpty(jsonObject.getString("filesize")) ){
                            tv_recent_msg.setText("[文件]");
                        }else {
                            tv_recent_msg.setText(SmileyParser.getInstance().addSmileySpans(msg.getBody()));
                        }
                    }
                }

            } catch (Exception e) {
                tv_recent_msg.setText(SmileyParser.getInstance().addSmileySpans(msg.getBody()));
            }


        }
        if (msg.getExtRemoteUserName().equalsIgnoreCase("leave_token")) {
            tv_recent_name.setText("请假");
        } else if (msg.getExtRemoteUserName().equalsIgnoreCase("sw_token")) {
            tv_recent_name.setText("收文");
        } else {
            tv_recent_name.setText(msg.getExtRemoteDisplayName());
        }
        if (!TextUtils.isEmpty(String.valueOf(msg.getCreateTime()))) {
            tv_recent_time.setText(TimeUtils.getDescriptionTimeFromTimestamp(msg.getCreateTime()));
        }

        if (unRead > 0) {
            tv_recent_unread.setText(unRead + "");
            tv_recent_unread.setVisibility(View.VISIBLE);
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }
        String username = null;
        if (msg.getType().equalsIgnoreCase(MsgInFo.TYPE_CHAT)) {
            username = msg.getExtRemoteUserName().split("@")[0];
            username = username.concat(Member.SUFFIX_USER);
        } else {
//            群没有头像
            //3@group.skysea.com/8a34f8c9491f3d8101493bb934bb34c2
            username = msg.getFrom_();
            if (username.contains("/")) {
                username = username.substring(username.lastIndexOf("/") + 1, username.length
                        ()).toLowerCase();
                username = username.concat(Member.SUFFIX_USER);
            }
        }
        setIcon(username);
    }

    /**
     * 设置头像
     */
    private void setIcon(String remoteUserName) {

        final String avataBiggerUrl = Utils.getAvataUrl(remoteUserName, sizeBigger);
        final String avataSmallUrl = Utils.getAvataUrl(remoteUserName, sizeSmall);
        DisplayImageOptions options = null;

        if (message.getType().equalsIgnoreCase(MsgInFo.TYPE_CHAT)) {

            if (Member.isTypeApp(message.getExtRemoteUserName())) {
                Log.d("RecentChatItemView", "aPP");
                options = ImagerLoaderOptHelper.getAppAvatarOpt();
                ImageLoader.getInstance().displayImage(Utils.getAvataUrl(message.getExtRemoteUserName(), sizeBigger), iv_recent_avatar,
                        options);
            } else {
                Log.d("RecentChatItemView", "no aPP");
                options = ImagerLoaderOptHelper.getUserLeftAvatarOpt();
                ImageLoader.getInstance().displayImage(avataBiggerUrl, iv_recent_avatar,
                        options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                ImageSize smallSize = new ImageSize(sizeSmall, sizeSmall);
                                // TODO: 2016/4/2 添加缓存 添加聊天界面头像的缓存
                                ImagerLoaderOptHelper.removeFromCache(avataSmallUrl, smallSize);
                                Bitmap bitmap = RoundImageView.toRoundBitmap(loadedImage);
                                ImagerLoaderOptHelper.putCache(avataSmallUrl, smallSize, bitmap);
                            }
                        });
            }
        } else if (message.getType().equalsIgnoreCase(MsgInFo.TYPE_GROUPCHAT)) {
            Log.d("RecentChatItemView", "P");
            options = ImagerLoaderOptHelper.getGroupAvatarOpt();
            ImageLoader.getInstance().displayImage(null, iv_recent_avatar,
                    options);
        } else if (message.getType().equalsIgnoreCase(MsgInFo.TYPE_GROUP_OPERATE)) {
            options = ImagerLoaderOptHelper.getGroupAvatarOpt();
            ImageLoader.getInstance().displayImage(null, iv_recent_avatar,
                    options);
        }
    }
}
