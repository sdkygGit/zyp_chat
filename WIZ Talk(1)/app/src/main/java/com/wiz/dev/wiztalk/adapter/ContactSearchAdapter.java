package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lidroid.xutils.BitmapUtils;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.XXBitmapUtils;
import com.wiz.dev.wiztalk.view.ContactItemView;
import com.wiz.dev.wiztalk.view.ContactItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class ContactSearchAdapter extends BaseAdapter {

	@RootContext
	Context context;

	private List<Member> members = new ArrayList<Member>();
	
	private BitmapUtils bitmapUtils;
	
	public ContactSearchAdapter(Context context) {
		bitmapUtils = new XXBitmapUtils(context);
		
		bitmapUtils.configDefaultLoadingImage(R.drawable.ic_default_avata_mini);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_default_avata_mini);
	}
	
	@Override
	public int getCount() {
		return members.size();
	}

	@Override
	public Object getItem(int position) {
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactItemView view;

		if (convertView == null) {
			view = ContactItemView_.build(context);
		} else {
			view = (ContactItemView) convertView;
		}

		Member member = (Member) getItem(position);
		view.bind(member);
		view.setDividerVisible(position != 0);
		
		return view;
	}

	public void addMembers(List<Member> members) {
		this.members.addAll(members);
		this.notifyDataSetChanged();
	}

	public void clear() {
		this.members.clear();
		this.notifyDataSetChanged();
	}

}
