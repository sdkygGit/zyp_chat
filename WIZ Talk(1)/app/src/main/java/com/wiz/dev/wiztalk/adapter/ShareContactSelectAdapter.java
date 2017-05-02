package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.listener.OnMemberDeleteListener;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.ContactItemSelectView;
import com.wiz.dev.wiztalk.view.ContactItemSelectView_;

import java.util.ArrayList;
import java.util.List;

public class ShareContactSelectAdapter extends BaseAdapter {

	Context context;

//	private BitmapUtils bitmapUtils;

	public ShareContactSelectAdapter(Context context) {
//		bitmapUtils = new XXBitmapUtils(context);
		this.context = context;
//		bitmapUtils.configDefaultLoadingImage(R.drawable.ic_default_avata_mini);
//		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_default_avata_mini);
	}

	@Override
	public int getCount() {
		return mMemberList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMemberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactItemSelectView view;

		if (convertView == null) {
			view = ContactItemSelectView_.build(context);
		} else {
			view = (ContactItemSelectView) convertView;
		}
//		view.setBitmapUtils(bitmapUtils);
		view.bind((Member) getItem(position));
		view.setOnMemberDeleteListener(mOnMemberDeleteListener);

		return view;
	}

	private OnMemberDeleteListener mOnMemberDeleteListener;
	
	public void setOnMemberDeleteListener(OnMemberDeleteListener l) {
		mOnMemberDeleteListener = l;
		this.notifyDataSetChanged();
	}
	
	private List<Member> mMemberList = new ArrayList<Member>();

	public void addMember(Member member) {
		int index = Utils.listIndexOf(mMemberList, member.Uid);
		if (index == -1) {
			mMemberList.add(member);
		} else {
			mMemberList.set(index, member);
		}
		this.notifyDataSetChanged();
	}
	
	public void addMembers(List<Member> members) {
		if (members != null) {
//			mMemberList.addAll(members);
//			this.notifyDataSetChanged();
			for (Member member : members) {
				int index = Utils.listIndexOf(mMemberList, member.Uid);
				if (index == -1) {
					mMemberList.add(member);
				} else {
					mMemberList.set(index, member);
				}
			}
			this.notifyDataSetChanged();
		}
	}

	public void clear(){
		mMemberList.clear();
		this.notifyDataSetChanged();
	}

	public void removeMember(Member member) {
		if (member != null) {
			mMemberList.remove(Utils.listGet(mMemberList, member.Uid));
			this.notifyDataSetChanged();
		}
	}

	public void removeMembers(List<Member> members) {
		if (members != null) {
			for (Member member : members) {
				mMemberList.remove(Utils.listGet(mMemberList, member.Uid));
			}
			this.notifyDataSetChanged();
		}
	}
	
	public List<Member> getMembers() {
		return mMemberList;
	}
}
