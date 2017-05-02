package com.wiz.dev.wiztalk.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Response implements Serializable {

	private static final long serialVersionUID = -8658023577124660186L;

	@JsonProperty(value="baseResponse")
	public BaseResponse BaseResponse;

	@Override
	public String toString() {
		return writeValueAsString(this);
//		return "Response [BaseResponse=" + BaseResponse + "]";
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
