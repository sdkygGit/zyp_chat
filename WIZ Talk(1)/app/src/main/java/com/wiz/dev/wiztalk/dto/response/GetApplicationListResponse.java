package com.wiz.dev.wiztalk.dto.response;

import com.wiz.dev.wiztalk.DB.Member;

import java.util.List;



//@JsonIgnoreProperties(ignoreUnknown=true)
public class GetApplicationListResponse extends Response {

	private static final long serialVersionUID = 5412666525061779465L;

	public int memberCount;
	
	public List<Member> memberList;
}
