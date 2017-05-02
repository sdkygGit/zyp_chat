package com.wiz.dev.wiztalk.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiz.dev.wiztalk.DB.Member;

public class CreateQunResponse extends Response {

	public static final long serialVersionUID = -1905946076282832331L;

	@JsonProperty(value = "topic")
	public String Topic;

	@JsonProperty(value = "memberCount")
	public int MemberCount;

	@JsonProperty(value = "memberList")
	public List<Member> MemberList;

}
