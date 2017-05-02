package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiz.dev.wiztalk.dto.response.Response;

public class GetMemberResponse extends Response {

	private static final long serialVersionUID = 6840544300239008166L;

	@JsonProperty(value="member")
	public com.wiz.dev.wiztalk.DB.Member Member;
	
}
