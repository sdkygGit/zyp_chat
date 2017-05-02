package com.wiz.dev.wiztalk.adapter;

import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.view.ContactItemView;
import com.wiz.dev.wiztalk.view.ContactItemView_;


@EBean
public class AppListAdapter extends SectionedBaseAdapter {

	@RootContext
	Context context;
	
	private List<Member> members;
	
//	BitmapUtils bitmapUtils;
	
	public AppListAdapter(Context context) {
//		bitmapUtils = new XXBitmapUtils(context);
//		bitmapUtils.configDefaultLoadingImage(R.drawable.ic_default_app_mini);
//		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_default_app_mini);
	}
	
	public void onPause() {
//		bitmapUtils.clearMemoryCache();
//		bitmapUtils.flushCache();
//		bitmapUtils.pause();
	}
	
	public void onResume() {
//		bitmapUtils.resume();
	}
	
	public void changeData(List<Member> members) {
		this.members = members;
		this.notifyDataSetChanged();
	}
	
	public List<Member> getData() {
		return this.members;
	}
	
	@Override
	public Object getItem(int section, int position) {
		return this.members.get(position);
	}

	@Override
	public long getItemId(int section, int position) {
		return position;
	}

	@Override
	public int getSectionCount() {
		return 1;
	}

	@Override
	public int getCountForSection(int section) {
		return this.members == null ? 0 : this.members.size();
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		ContactItemView view;

		if (convertView == null) {
			view = ContactItemView_.build(context);
		} else {
			view = (ContactItemView) convertView;
		}
		
		Member member = (Member) getItem(section, position);
		view.setDividerVisible(position != 0);
//		view.setBitmapUtils(bitmapUtils);
		view.bind(member, false, false, false);
		
		view.tvOrgName.setText("1".equals(member.Follow) ? "已关注" : "");
		
		return view;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		return new View(context);
	}

}
