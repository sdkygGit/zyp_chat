package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epic.traverse.push.util.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.activity.XchatImageActivity_;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 显示接收图片的view
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_received_image)
public class ChatItemReceiveImageView extends ChatItemView {

    @ViewById
    ImageView iv_picture;

    @ViewById
    TextView tv_name;

    XchatActivity context;

    String jsonObject;

    public ChatItemReceiveImageView(XchatActivity context) {
        super(context);
        this.context = context;
    }

    @Override
    public void bind(XmppMessage message) {
        super.bind(message);
    }

    @Override
    public void bindOther(XmppMessage message) {
        if (XmppDbMessage.isGroupChatMessage(message)) {
            tv_name.setText(message.getExtLocalDisplayName());
        } else {
            tv_name.setVisibility(View.GONE);
//            XmppDbManager.getInstance(mContext).queryToReadStatusAndSendNotify(message);
        }
        if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
            if (message.getMold() == MsgInFo.MOLD_IMG) {

                iv_picture.setMaxWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, context.getResources().getDisplayMetrics()));
                iv_picture.setMaxHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, context.getResources().getDisplayMetrics()));

                try {
                    jsonObject = message.getFilePath();

                    String url;
                    if(context.saveConfig.getStringConfig("httpConfig").equals("true")){
                        url = jsonObject.split("&")[0];
                    }else {
                        url = jsonObject.split("&")[1];
                    }

                    displayImagebyUrl(iv_picture, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * 显示 图片信息
     *
     * @param iv_picture
     * @param strUrl
     */
    protected void displayImagebyUrl(final ImageView iv_picture,
                                     final String strUrl) {

        // TODO: 2016/4/8
        if (iv_picture != null && iv_picture.getTag() != null && !TextUtils.isEmpty(iv_picture.getTag()
                .toString()) && iv_picture.getTag()
                .equals(strUrl)) {

            return;
        }

        Bitmap bitmapFromCache = ImagerLoaderOptHelper.getBitmapFromCache(strUrl, new
                ImageSize(pictureSizeWidth, pictureSizeheight));
        if (bitmapFromCache != null) {
            iv_picture.setImageBitmap(bitmapFromCache);
            iv_picture.setTag(strUrl);
            L.d(TAG, "display bitmap from cache");
            return;
        }


        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chat_nopicture)
                .showImageForEmptyUri(R.drawable.chat_nopicture)
                .showImageOnFail(R.drawable.card_photofail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new SimpleBitmapDisplayer())
                .resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
                .build();


        ImageLoader.getInstance().displayImage(strUrl, iv_picture,
                options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        ImagerLoaderOptHelper.putCache(strUrl, new
                                ImageSize(pictureSizeWidth, pictureSizeheight), loadedImage);
                        iv_picture.setTag(strUrl);
                    }
                });

    }

    @Click
    void iv_picture() {
        try {
            if(jsonObject ==  null)
                return;
            String url;
            if(context.saveConfig.getStringConfig("httpConfig").equals("true")){
                url = jsonObject.split("&")[0];
            }else {
                url = jsonObject.split("&")[1];
            }
            XchatImageActivity_.intent(getContext()).pathUrl(url).start();
        }catch (Exception e){}
    }

    @LongClick(R.id.iv_picture)
    public void ivPictureLongClick() {
        Log.d(TAG, "ivPictureLongClick()");
        initPopupWindow();
        showPopuWindow(iv_picture);
    }

    protected View popView;
    protected void initPopupWindow() {
        if (popWin == null) {
            popView = LayoutInflater.from(mContext).inflate(
                    R.layout.popup_list_item_long_click, null);


            View copyLay = (View) popView.findViewById(R.id.tvCopy);
            copyLay.setVisibility(View.GONE);
            View line = (View) popView.findViewById(R.id.line);
            line.setVisibility(View.GONE);


            TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
            tvDel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageDao.delete(message);
                    mContext.getContentResolver().notifyChange(
                            XmppMessageContentProvider.CONTENT_URI, null);
                    popWin.dismiss();
                }
            });

            popWin = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popWin.setOutsideTouchable(true);
            popView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWin.dismiss();
                    // todo
                }
            });
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popWin.setBackgroundDrawable(new BitmapDrawable());
        }
    }

    protected void showPopuWindow(View iv) {
        popView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int popupWidth = popView.getMeasuredWidth();
        int popupHeight = popView.getMeasuredHeight();
        int[] location = new int[2];
        iv.getLocationOnScreen(location);
        popWin.showAtLocation(iv, Gravity.NO_GRAVITY,
                (location[0] + iv.getWidth() / 2) - popupWidth / 2, location[1]
                        - popupHeight);
    }
}
