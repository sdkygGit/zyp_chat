package com.wiz.dev.wiztalk.listener;


import com.wiz.dev.wiztalk.DB.Member;

public interface OnMemberCheckedChangedListener {

	public void onMemberCheckedChanged(Member member, boolean isChecked);
}
