package com.wiz.dev.wiztalk.rest;

import org.androidannotations.annotations.EBean;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

@EBean
public class MyErrorHandler implements RestErrorHandler {
   
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        
    }
}
