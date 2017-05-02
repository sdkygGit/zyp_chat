package com.wiz.dev.wiztalk.rest;

import android.content.Context;

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.Response;
import com.wiz.dev.wiztalk.public_store.ConstDefine;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Appmsgsrv8093Proxy implements InvocationHandler {

	private static final String TAG = "IMInterfaceProxy";

	public static Appmsgsrv8093 create(int timeout) {
//		ClientHttpRequestFactory requestFactory;
//		SimpleClientHttpRequestFactory schrf
//		CommonsClientHttpRequestFactory cchrf;
//		HttpComponentsClientHttpRequestFactory hcchrf
		
//		SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
//		schrf.setConnectTimeout(timeout);
//		schrf.setReadTimeout(timeout);
		
		HttpComponentsClientHttpRequestFactory hcchrf = new HttpComponentsClientHttpRequestFactory();
		hcchrf.setConnectTimeout(timeout);
		hcchrf.setReadTimeout(timeout);
		
		Appmsgsrv8093 myRestClient = new Appmsgsrv8093_(MyApplication.getInstance());
		myRestClient.getRestTemplate().setRequestFactory(hcchrf);
		
		Context context = MyApplication.getInstance();
		if (context != null) {
			// TODO: 2016/4/8  
//			myRestClient.setRootUrl(Utils.getURLAppmsgsrv8093(context, "/app/client/device"));
			myRestClient.setRootUrl(ConstDefine.HttpAdress+"/app/client/device");
		}
		
		return create(myRestClient);
	}
	
	public static Appmsgsrv8093 create() {
		return create(8* 1000);
	}
	
	private static Appmsgsrv8093 create(Appmsgsrv8093 object) {
		return (Appmsgsrv8093) Proxy.newProxyInstance(Appmsgsrv8093.class
				.getClassLoader(), new Class[] { Appmsgsrv8093.class },
				new Appmsgsrv8093Proxy(object));
	}

	private Appmsgsrv8093 mObj;

	public Appmsgsrv8093Proxy(Appmsgsrv8093 object) {
		mObj = object;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = method.invoke(mObj, args);

		handleTokenExpire(result);

		return result;
	}

	private void handleTokenExpire(Object result) {
		L.d(TAG, "handleTokenExpire() isTokenExpire(result):" + isTokenExpire(result));
		if (isTokenExpire(result)) {
			MyApplication.getInstance().onReLogin();
		}
	}

	private boolean isTokenExpire(Object result) {
		if (result instanceof Response) {
			Response response = (Response) result;
			BaseResponse baseResponse = response.BaseResponse;
//			response.BaseResponse.Ret = BaseResponse.RET_ERROR_TOKEN;
			return baseResponse.Ret == BaseResponse.RET_ERROR_TOKEN;
		}

		return false;
	}

}
