package com.wiz.dev.wiztalk.apppush;

import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;

import java.util.List;


public interface OnOperationClickListener {

	void onOperationClick(AppPushEntity.ObjectContentEntity.OperationsEntity operation);

	void onOperationsClick(List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations);
}
