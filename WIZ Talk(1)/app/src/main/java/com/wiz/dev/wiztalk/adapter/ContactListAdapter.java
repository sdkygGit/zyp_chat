package com.wiz.dev.wiztalk.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.listener.OnMemberCheckedChangedListener;
import com.wiz.dev.wiztalk.listener.OnMembersCheckedChangedListener;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.ContactItemSectionView;
import com.wiz.dev.wiztalk.view.ContactItemSectionView_;
import com.wiz.dev.wiztalk.view.ContactItemView;
import com.wiz.dev.wiztalk.view.ContactItemView_;

//import  com.yxst.epic.unifyplatform.adapter.SectionedBaseAdapter2;
@EBean
public class ContactListAdapter extends SectionedBaseAdapter2 {

	public static final String TAG = "ContactListAdapter";
	
	@RootContext
	Context context;

	boolean isSelectMode;

	private OnMemberCheckedChangedListener mOnMemberCheckedChangedListener;
	private OnMembersCheckedChangedListener mOnMembersCheckedChangedListener;
	
	public ContactListAdapter(Context mContext) {
	}
	
	public void onPause() {
	}
	
	public void onResume() {
	}
	
	@AfterInject
	void afterInject() {
		// ACache.get(context).getAsObject("");
	}

	public void setOnMemberCheckedChangedListener(
			OnMemberCheckedChangedListener l) {
		mOnMemberCheckedChangedListener = l;
		this.notifyDataSetChanged();
	}
	public void setOnMembersCheckedChangedListener(
			OnMembersCheckedChangedListener l) {
		mOnMembersCheckedChangedListener = l;
		this.notifyDataSetChanged();
	}

	private Map<String, List<Member>> map;

	public void changeData(Map<String, List<Member>> map) {
		this.map = map;
		this.notifyDataSetChanged();
	}

	public Map<String, List<Member>> getData() {
		return this.map;
	}

	public void setIsSelectMode(boolean isSelectMode) {
		this.isSelectMode = isSelectMode;
		this.notifyDataSetChanged();
	}

	public String getItemBySection(int section) {
		Map.Entry<String, List<Member>> entry = Utils.mapGet(map, section);
		if (entry != null) {
			return entry.getKey();
		}
		return null;
	}

	@Override
	public Member getItem(int section, int position) {
		Map.Entry<String, List<Member>> entry = Utils.mapGet(map, section);
		if (entry != null) {
			List<Member> memberList = entry.getValue();
			if (memberList != null) {
				return memberList.get(position);
			}
		}
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	@Override
	public int getSectionCount() {
		return map == null ? 0 : map.size();
	}

	@Override
	public int getCountForSection(int section) {
		Map.Entry<String, List<Member>> entry = Utils.mapGet(map, section);
		List<Member> memberList = entry.getValue();
		if (memberList != null) {
			return memberList.size();
		}
		return 0;
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

		Member member = getItem(section, position);
		view.setOnMemberCheckedChangedListener(null);
		view.bind(member, isSelectMode, isSelected(member), isLocked(member));
		view.setDividerVisible(position != 0);
		view.setOnMemberCheckedChangedListener(mOnMemberCheckedChangedListener);

		return view;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		ContactItemSectionView view;

		if (convertView == null) {
			view = ContactItemSectionView_.build(context);
		} else {
			view = (ContactItemSectionView) convertView;
		}

		String key = getItemBySection(section);
		view.bind(key);
		
		List<Member> membersInSection = null;
		List<Member> membersNotLocked = new ArrayList<Member>();
		
		Map.Entry<String, List<Member>> entry = Utils.mapGet(map, section);
		if (entry != null) {
			membersInSection = entry.getValue();
			if (membersInSection != null) {
				for (Iterator<Member> it = membersInSection.iterator(); it.hasNext();) {
					Member m = it.next();
					if (!isLocked(m)) {
						membersNotLocked.add(m);
					}
				}
			}
		}
		
		boolean isSectionCanCheck = isSelectMode && !"公共通讯录".equals(key) && !"组织结构".equals(key) && membersInSection != null && membersInSection.size() != 0;
		view.setVisible(isSectionCanCheck);
		view.setOnMembersCheckedChangedListener(null);
		view.setMembers(membersNotLocked);
		view.setOnMembersCheckedChangedListener(mOnMembersCheckedChangedListener);

		return view;
	}

	private List<Member> mSelectMembers;

	public void setSelectMembers(List<Member> selectMembers) {
		mSelectMembers = selectMembers;
		this.notifyDataSetChanged();
	}

	private boolean isSelected(Member member) {
		if (mSelectMembers != null) {
			return Utils.listContains(mSelectMembers, member.Uid);
		}
		return false;
	}

	private boolean isLocked(Member member) {
		if (lockMembers != null) {
			return Utils.listContains(lockMembers, member.Uid);
		}
		return false;
	}

	private List<Member> lockMembers;

	public void setLockMembers(List<Member> lockMembers) {
		this.lockMembers = lockMembers;
		this.notifyDataSetChanged();
	}

}
