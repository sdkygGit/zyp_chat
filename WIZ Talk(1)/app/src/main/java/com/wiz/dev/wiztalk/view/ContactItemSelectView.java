package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.listener.OnMemberDeleteListener;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

@EViewGroup(R.layout.list_item_contact_select)
public class ContactItemSelectView extends RelativeLayout {

	@ViewById(R.id.iv_icon)
	ImageView mIconView;

	@ViewById(R.id.tv_name)
	TextView mNameView;

	@ViewById
	ImageView ivDel;
	
	Member member;
	
	@DimensionPixelOffsetRes(R.dimen.icon_size_small)
	int size;
	
	private BitmapUtils mBitmapUtils;
	
	public ContactItemSelectView(Context context) {
		super(context);
	}

	public ContactItemSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactItemSelectView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void bind(Member member) {
		this.member = member;
		
		if (member.isTypeUser()) {
			mIconView.setImageResource(R.drawable.ic_default_avata_mini);
			mNameView.setText(member.NickName);
			
//			mBitmapUtils.display(mIconView, Utils.getAvataUrl(member.UserName, sizeBigger));
			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_default_avata_mini)
				.showImageForEmptyUri(R.drawable.ic_default_avata_mini)
				.showImageOnFail(R.drawable.ic_default_avata_mini)
				.cacheInMemory(false)
				.cacheOnDisk(false)
//				.considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(999))
				.build();
			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, size), mIconView, options, null);
//			ImageLoader.getInstance().displayImage(Utils.getImgUrl(member.Avatar, size), mIconView, options, null);
		} else if (member.isTypeOrg()) {
			mIconView.setImageResource(R.drawable.ic_default_org_mini);
			mNameView.setText(member.NickName);
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_default_org_mini)
				.showImageForEmptyUri(R.drawable.ic_default_org_mini)
				.showImageOnFail(R.drawable.ic_default_org_mini)
				.cacheInMemory(false)
				.cacheOnDisk(false)
				.considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(999))
				.build();
//			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, sizeBigger), mIconView, options, null);
			ImageLoader.getInstance().displayImage(Utils.getImgUrl(member.Avatar, size), mIconView, options, null);
		}
	}

	public void setOnMemberDeleteListener(OnMemberDeleteListener l) {
		ivDel.setVisibility(View.GONE);
		ivDel.setVisibility(View.VISIBLE);
		mOnMemberDeleteListener = l;
	}
	
	private OnMemberDeleteListener mOnMemberDeleteListener;
	
	@Click(R.id.ivDel)
	void onClickDel() {
		if (mOnMemberDeleteListener != null) {
			mOnMemberDeleteListener.onMemberDelete(member);
		}
	}
	
	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.mBitmapUtils = bitmapUtils;
	}
}
