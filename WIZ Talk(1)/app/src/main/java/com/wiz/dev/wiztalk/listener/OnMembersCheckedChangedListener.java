package com.wiz.dev.wiztalk.listener;

import com.wiz.dev.wiztalk.DB.Member;

import java.util.List;

public interface OnMembersCheckedChangedListener {

	void onMembersCheckedChanged(List<Member> members, boolean isChecked);
}
