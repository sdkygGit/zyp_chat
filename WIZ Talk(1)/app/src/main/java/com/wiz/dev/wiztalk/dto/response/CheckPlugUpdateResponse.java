package com.wiz.dev.wiztalk.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckPlugUpdateResponse extends Response {

	private static final long serialVersionUID = 5033737217934061340L;

	public boolean isUpdate;

	@JsonProperty(value="msg")
	public Msg msg;
	
}
