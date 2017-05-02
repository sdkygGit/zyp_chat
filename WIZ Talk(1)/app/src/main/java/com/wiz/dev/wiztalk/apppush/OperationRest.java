package com.wiz.dev.wiztalk.apppush;

import com.wiz.dev.wiztalk.dto.response.Response;
import com.wiz.dev.wiztalk.rest.HttpBasicAuthenticatorInterceptor;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


@Rest(converters = { MappingJackson2HttpMessageConverter.class }, interceptors = { HttpBasicAuthenticatorInterceptor.class })
public interface OperationRest extends RestClientErrorHandling,
		RestClientRootUrl, RestClientSupport {

	@Get("")
	@Accept(MediaType.APPLICATION_JSON)
//	ServiceResult<Object> get(); 
	Response get(); 
	
	@Post("")
	@Accept(MediaType.APPLICATION_JSON)
//	ServiceResult<Object> post(Object obj);
	Response post(Object obj);
}