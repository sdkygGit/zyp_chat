package com.wiz.dev.wiztalk.apppush.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.wiz.dev.wiztalk.apppush.OnOperationClickListener;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EFragment;

import java.util.List;


@EFragment
public class OperationsMoreDialogFragment extends DialogFragment implements OnClickListener {

//	@FragmentArg
	List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations;
	
	@SuppressWarnings("unchecked")
	@AfterInject
	void afterInject() {
		Bundle args = getArguments();
		if (args != null) {
			this.operations = (List<AppPushEntity.ObjectContentEntity.OperationsEntity>) args.getSerializable("operations");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
//		OperationAdapter adapter = new OperationAdapter(this.operations);
//		builder.setAdapter(adapter, this);
		
		CharSequence[] items = getItems();
		builder.setItems(items, this);
		
		AlertDialog dialog = builder.create();
		return dialog;
	}
	
	private CharSequence[] getItems() {
		if (this.operations != null) {
			int N = this.operations.size();
			CharSequence[] items = new CharSequence[N];
			for (int i = 0; i < N; i++) {
				AppPushEntity.ObjectContentEntity.OperationsEntity operation = this.operations.get(i);
				items[i] = operation.content;
			}
			return items;
		}
		return null;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mOnOperationClickListener != null) {
			AppPushEntity.ObjectContentEntity.OperationsEntity operation = this.operations.get(which);
			mOnOperationClickListener.onOperationClick(operation);
		}
	}

	private OnOperationClickListener mOnOperationClickListener;
	
	public void setOnOperationClickListener(OnOperationClickListener l) {
		mOnOperationClickListener = l;
	}
}
