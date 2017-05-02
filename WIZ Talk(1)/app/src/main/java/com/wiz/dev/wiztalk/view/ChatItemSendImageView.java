package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.activity.XchatImageActivity_;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.MessageUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;


/**
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_sent_image)
public class ChatItemSendImageView extends ChatItemSendView {
	
	@ViewById
	ImageView iv_picture;

	XchatActivity context;


	public ChatItemSendImageView(XchatActivity context) {
		super(context);
		this.context = context;
	}

	@Override
	public void bindOther(XmppMessage message) {

		if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
			if (message.getMold() == MsgInFo.MOLD_IMG) {

				iv_picture.setMaxWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,160,context.getResources().getDisplayMetrics()));
				iv_picture.setMaxHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, context.getResources().getDisplayMetrics()));

				displayImagebyUrl(iv_picture, message.getExtInternalFilePath());
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
		L.d(TAG, "iv_picture()");
		String filepath = message.getFilePath();
		if (!TextUtils.isEmpty(message.getExtInternalFilePath())) {
			filepath = message.getExtInternalFilePath();
		}
		XchatImageActivity_.intent(getContext()).pathUrl(filepath)
				.start();
		
	}
	
	@LongClick(R.id.iv_picture)
	void ivPictureLongClick() {
		Log.d(TAG, "ivPictureLongClick()");
		initPopupWindow();
		showPopuWindow(iv_picture);
	}

	protected void initPopupWindow() {
		if (popWin == null) {
			popView = LayoutInflater.from(mContext).inflate(
					R.layout.popup_list_item_long_click, null);
			MyPopuwinOnClickLisentener lisenter = new MyPopuwinOnClickLisentener();

			TextView tvCopy = (TextView) popView.findViewById(R.id.tvCopy);
			tvCopy.setText("重发");
			tvCopy.setOnClickListener(lisenter);
			TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
			tvDel.setOnClickListener(lisenter);

			popWin = new PopupWindow(popView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			popWin.setOutsideTouchable(true);
			popView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popWin.dismiss();
				}
			});
			// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
			// 我觉得这里是API的一个bug
			popWin.setBackgroundDrawable(new BitmapDrawable());
		}
	}

	private class MyPopuwinOnClickLisentener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tvCopy:
					if (message != null)
						context.sendImageMsg(message.getExtInternalFilePath().substring(6));

					break;
				case R.id.tvDel:
					messageDao.delete(message);
					mContext.getContentResolver().notifyChange(
							XmppMessageContentProvider.CONTENT_URI, null);
					break;

			}
			popWin.dismiss();
		}

	}

	@Click
	void iv_fail_resend(View view){
		iv_fail_resend.setVisibility(View.GONE);
		MessageUtils.sendMediaMsg(context, message);
//		message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
//		sendMsg();
	}

}
