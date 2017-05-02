package com.wiz.dev.wiztalk.dto.response;

import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;

import java.util.List;


public class GetAppOperationListResponse extends Response {

	private static final long serialVersionUID = 3281405056415312352L;

	public int operationCount;
	
	public List<AppPushEntity.ObjectContentEntity.OperationsEntity> operationList;
}
