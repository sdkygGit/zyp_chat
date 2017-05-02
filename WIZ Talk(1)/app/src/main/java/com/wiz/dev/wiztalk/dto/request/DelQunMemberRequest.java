package com.wiz.dev.wiztalk.dto.request;

import com.wiz.dev.wiztalk.DB.Member;

import java.util.ArrayList;
import java.util.List;


public class DelQunMemberRequest extends Request {

	public String ChatRoomName;
	
	public List<Member> memberList;

	public void addMember(Member member) {
		if (this.memberList == null) {
			this.memberList = new ArrayList<Member>();
		}
		this.memberList.add(member);
	}
	
}
