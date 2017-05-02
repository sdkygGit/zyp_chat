package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AddOrRemoveContactRequest extends Request {

	@JsonProperty(value="uid")
	public String Uid;
	
	@JsonProperty(value="starFriend")
	public Integer StarFriend;
	
}
