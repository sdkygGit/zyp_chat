package com.wiz.dev.wiztalk.apppush.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.apppush.OnOperationClickListener;
import com.wiz.dev.wiztalk.apppush.entity.AppPushEntity;
import com.wiz.dev.wiztalk.view.horizontallistview.HorizontalListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

//import com.meetme.android.horizontallistview.HorizontalListView;


@EViewGroup(R.layout.footer_list)
public class FooterListView extends LinearLayout implements OnItemClickListener {

	@ViewById(R.id.btn_mmfooter_listtotext)
	public ImageButton ibListToText;

	@ViewById
	HorizontalListView listOperations;

	MenuPopupHelper mMenuPopupHelper;

	public FooterListView(Context context) {
		super(context);
	}

	public FooterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterViews
	void afterViews() {
		listOperations.setOnItemClickListener(this);
	}

	public void bind(String remoteUserName, List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations) {
		if (mMenuPopupHelper != null && mMenuPopupHelper.isShowing()) {
			mMenuPopupHelper.dismiss();
		}

		OperationsAdapter adapter = new OperationsAdapter(operations);
		listOperations.setAdapter(adapter);
	}

	private class OperationsAdapter extends BaseAdapter {

		private List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations;

		public OperationsAdapter(List<AppPushEntity.ObjectContentEntity.OperationsEntity> operations) {
			this.operations = operations;
		}

		@Override
		public int getCount() {
			return this.operations == null ? 0 : this.operations.size();
		}

		@Override
		public Object getItem(int position) {
			return this.operations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			App102Operation view;

			if (convertView == null) {
				view = App102Operation_.build(parent.getContext());
				setupLayoutParams(view, parent);
			} else {
				view = (App102Operation) convertView;
			}

			AppPushEntity.ObjectContentEntity.OperationsEntity item = (AppPushEntity.ObjectContentEntity.OperationsEntity) getItem
					(position);

//			view.bind(position != 0, item);
			view.bind(item);

			return view;
		}

		private void setupLayoutParams(View convertView, ViewGroup parent) {
			// Log.d(TAG, "getView() parent.getWidth():" + parent.getWidth());
			// Log.d(TAG,
			// "getView() parent.getMeasuredWidth():"
			// + parent.getMeasuredWidth());

			int N = getCount();

			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					(parent.getWidth() - (N - 1) * 1) / getCount(),
					ViewGroup.LayoutParams.MATCH_PARENT);
			convertView.setLayoutParams(params);
		}
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		AppPushEntity.ObjectContentEntity.OperationsEntity operation = (AppPushEntity.ObjectContentEntity.OperationsEntity) parent
				.getItemAtPosition(position);

		if (operation.operationList == null || operation.operationList.size() == 0) {
			if (mOnOperationClickListener != null) {
				mOnOperationClickListener.onOperationClick(operation);
			}
			return;
		}

		mMenuPopupHelper = new MenuPopupHelper(parent.getContext(),
				operation.operationList, view);
		
		mMenuPopupHelper.setCallback(mOnOperationClickListener);
		mMenuPopupHelper.setForceShowIcon(false);
		mMenuPopupHelper.show();
	}

	private OnOperationClickListener mOnOperationClickListener;

	public void setOnOperationClickListener(OnOperationClickListener l) {
		mOnOperationClickListener = l;
	}
}
