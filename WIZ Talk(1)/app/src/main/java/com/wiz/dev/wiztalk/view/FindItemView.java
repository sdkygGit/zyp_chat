package com.wiz.dev.wiztalk.view;

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;

@EViewGroup(R.layout.list_item_find)
public class FindItemView extends RelativeLayout {


	@ViewById(R.id.iv_icon)
	public ImageView mIconIV;

	@ViewById(R.id.tv_name)
	public TextView mNameTV;

	@ViewById(R.id.cb)
	public CheckBox mCheckBox;

	@ViewById(R.id.ivRight)
	public View ivRight;
	

	public FindItemView(Context context) {
		super(context);
	}

	public FindItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FindItemView(Context context, AttributeSet attrs, int defStyle) {
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

		if (member.isTypeUser()) {
			mIconIV.setImageResource(R.drawable.default_avatar);
			mNameTV.setText(member.NickName);
		} else {
			mIconIV.setImageResource(R.drawable.addfriend_icon_qucikgroup);
			mNameTV.setText(member.NickName);
		}
		
		mCheckBox
				.setVisibility(isSelectMode && member.isTypeUser() ? View.VISIBLE
						: View.GONE);
		
		mCheckBox.setChecked(isSelected || isLocked);
		
		mCheckBox.setEnabled(!isLocked);
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
	
	public enum GroupLineStyle {
		UP, DOWN, BOTH
	}
	
}
