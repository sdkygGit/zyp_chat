package com.wiz.dev.wiztalk.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiz.dev.wiztalk.DB.Member;

import java.util.List;

public class SearchResponse extends Response {

	private static final long serialVersionUID = -2369027069327315694L;

	@JsonProperty(value = "count")
	public int Count;

	@JsonProperty(value = "memberListSize")
	public int MemberListSize;

	@JsonProperty(value = "memberList")
	public List<Member> MemberList;
}
 