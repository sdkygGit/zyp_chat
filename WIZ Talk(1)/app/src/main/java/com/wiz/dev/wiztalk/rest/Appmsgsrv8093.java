package com.wiz.dev.wiztalk.rest;


import com.wiz.dev.wiztalk.dto.request.CancelShieldMsgRequest;
import com.wiz.dev.wiztalk.dto.request.IsUserShieldAppRequest;
import com.wiz.dev.wiztalk.dto.request.SetShieldMsgRequest;
import com.wiz.dev.wiztalk.dto.request.UpdateQunTopicRequest;
import com.wiz.dev.wiztalk.dto.response.CancelShieldMsgResponse;
import com.wiz.dev.wiztalk.dto.response.IsUserShieldAppResponse;
import com.wiz.dev.wiztalk.dto.response.SetShieldMsgResponse;
import com.wiz.dev.wiztalk.dto.response.UpdateQunTopicResponse;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

//@Rest(rootUrl = "http://115.29.107.77:8093/app/client/device", converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
// @Rest(rootUrl = "http://115.29.226.14:8093/app/client/device", converters = {
// MappingJackson2HttpMessageConverter.class }, interceptors = {
// HttpBasicAuthenticatorInterceptor.class })
@Rest(rootUrl = "http://10.180.120.63:8093/app/client/device", converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
//@Rest(rootUrl = "http://http://10.180.120.157:8093/app/client/device", converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
@Accept(MediaType.APPLICATION_JSON)
public interface Appmsgsrv8093 extends RestClientErrorHandling,
		RestClientRootUrl, RestClientSupport {

//	void setHeader(String name, String value);
//
//	String getHeader(String name);

	@Post("/updateQunTopic")
	UpdateQunTopicResponse updateQunTopic(UpdateQunTopicRequest request);


	@Post("/setShieldMsg")
	SetShieldMsgResponse setShieldMsg(SetShieldMsgRequest request);
	
	@Post("/cancelShieldMsg")
	CancelShieldMsgResponse cancelShieldMsg(CancelShieldMsgRequest request);
	
	@Post("/isUserShieldApp")
	IsUserShieldAppResponse isUserShieldApp(IsUserShieldAppRequest request);
}
