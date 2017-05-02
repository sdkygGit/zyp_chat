package com.wiz.dev.wiztalk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public abstract class Request {

	@JsonProperty(value="baseRequest")
	public BaseRequest BaseRequest;

	@Override
	public String toString() {
		return writeValueAsString(this);
//		return "Request [BaseRequest=" + BaseRequest + "]";
	}

	public static String writeValueAsString(Object obj) {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
