package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epic.traverse.push.util.L;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

@EViewGroup(R.layout.list_item_contact)
public class ContactItemView extends RelativeLayout {

	private static final String TAG = "ContactItemView";

	@ViewById
	public View viewDivider;

	@ViewById(R.id.iv_icon)
	public ImageView mIconIV;

	@ViewById(R.id.tv_name)
	public TextView mNameTV;

	@ViewById
	public TextView tvOrgName;
	
	@ViewById(R.id.cb)
	public CheckBox mCheckBox;
	
	@ViewById
	View ivRight;
	
	@DimensionPixelOffsetRes(R.dimen.icon_size_normal)
	int size;

	public ContactItemView(Context context) {
		super(context);
	}

	public ContactItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void bind(int imageResId, int textResId) {
		mIconIV.setImageResource(imageResId);
		mNameTV.setText(textResId);
	}
	
	public void bind(int imageResId, String text) {
		mIconIV.setImageResource(imageResId);
		mNameTV.setText(text);
	}

	public void bind(Member member) {
		bind(member, false, false, false);
	}
	
	public void bind(Member member, boolean isSelectMode, boolean isSelected, boolean isLocked) {
		this.member = member;

		//Log.d(TAG, "bind() member:" + member);
		tvOrgName.setText(member.OrgName);
		
		if (member.isTypeUser()) {
			mIconIV.setImageResource(R.drawable.ic_default_avata_mini);
			mNameTV.setText(member.NickName);
			ivRight.setVisibility(View.GONE);
			
//			mBitmapUtils.display(mIconIV, Utils.getAvataUrl(member.UserName, sizeBigger));
			
//			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, sizeBigger), mIconIV, options);
			//TODO 设置头像
//			final String originalUrl = Utils.getImgUrl(member.Avatar, sizeBigger);
			final String originalUrl = Utils.getAvataUrl(member.UserName, size);
			L.d(TAG, "originalUrl:"+originalUrl);
			ImageLoader.getInstance().displayImage(originalUrl, mIconIV, ImagerLoaderOptHelper.getUserLeftAvatarOpt());
			
		} else if (member.isTypeApp()) {
			mIconIV.setImageResource(R.drawable.ic_default_app_mini);
			mNameTV.setText(member.NickName);
			ivRight.setVisibility(View.GONE);
			
//			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, sizeBigger), mIconIV, options);
			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, size), mIconIV,
					ImagerLoaderOptHelper.getAppAvatarOpt());
		} else if (member.isTypeOrg()){
			mIconIV.setImageResource(R.drawable.ic_default_org_mini);
			mNameTV.setText(member.NickName);
			ivRight.setVisibility(View.VISIBLE);
			
//			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, sizeBigger), mIconIV, options);
			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, size), mIconIV,
					ImagerLoaderOptHelper.getOrgAvatarOpt());
		} else if (member.isTypeTenant()){
			mIconIV.setImageResource(R.drawable.ic_default_tenant_mini);
			mNameTV.setText(member.NickName);
			ivRight.setVisibility(View.VISIBLE);
            
//			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, sizeBigger), mIconIV, options);
			ImageLoader.getInstance().displayImage(Utils.getAvataUrl(member.UserName, size), mIconIV,
					ImagerLoaderOptHelper.getTenantAvatarOpt());
		}
		
		mCheckBox
				.setVisibility(isSelectMode && member.isTypeUser() ? View.VISIBLE
						: View.GONE);
		
		mCheckBox.setChecked(isSelected || isLocked);
		
		mCheckBox.setEnabled(!isLocked);
	}

	public void setDividerVisible(boolean b) {
		viewDivider.setVisibility(b ? View.VISIBLE : View.GONE);
	}

	@CheckedChange(R.id.cb)
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (mOnMemberCheckedChangedListener != null) {
			mOnMemberCheckedChangedListener.onMemberCheckedChanged(member,
					isChecked);
		}
	}

	private Member member;
	private OnMemberCheckedChangedListener mOnMemberCheckedChangedListener;

	public void setOnMemberCheckedChangedListener(
			OnMemberCheckedChangedListener l) {
		mOnMemberCheckedChangedListener = l;
	}


	
}
