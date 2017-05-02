package com.wiz.dev.wiztalk.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiz.dev.wiztalk.DB.Member;

public class GetOrgInfoResponse extends Response {

	private static final long serialVersionUID = 947822122738833535L;

	@JsonProperty(value="ognizationMemberList")
	public Member OgnizationMemberList;

	@JsonProperty(value="userOgnization")
	public String UserOgnization;

	@JsonProperty(value="starMemberCount")
	public int StarMemberCount;
	
	@JsonProperty(value="starMemberList")
	public List<Member> StarMemberList;

}
