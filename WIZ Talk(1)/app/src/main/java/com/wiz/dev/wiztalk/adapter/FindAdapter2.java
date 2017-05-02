package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.view.FindItemView;
import com.wiz.dev.wiztalk.view.FindItemView_;


public class FindAdapter2 extends BaseAdapter {

	private Context context;

	public static String[] FINDS = {"扫一扫", "应用服务号"/*, "检查更新"*/};
	public static int[] FIND_DRAWABLES = {
			R.drawable.ic_find_sys,
			R.drawable.ic_find_yyfwh/*,
			R.drawable.ic_find_jcgx*/
	};
	
	public FindAdapter2(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return FINDS.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FindItemView view = (FindItemView) convertView;;
		if (convertView == null) {
			view = FindItemView_.build(context);
		}
		view.bind(FIND_DRAWABLES[position % FIND_DRAWABLES.length], FINDS[position]);
//		view.setDividerVisible(position != 0);

		view.ivRight.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
		
		return view;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}