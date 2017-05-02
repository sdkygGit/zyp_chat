package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetOrgUserListRequest extends Request {

	@JsonProperty(value = "orgid")
	public String orgid;

}
