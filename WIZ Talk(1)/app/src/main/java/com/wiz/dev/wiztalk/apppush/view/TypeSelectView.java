package com.wiz.dev.wiztalk.apppush.view;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.viewpagerindicator.CirclePageIndicator;
import com.wiz.dev.wiztalk.R;

@EViewGroup(R.layout.simple_circles)
public class TypeSelectView extends RelativeLayout {

	private static final String TAG = "TypeSelectView";
	
	public TypeSelectView(Context context) {
		super(context);
	}

	public TypeSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TypeSelectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	TestFragmentAdapter mAdapter;
	
	@ViewById(R.id.pager)
	ViewPager mPager;
	
	@ViewById(R.id.indicator)
	CirclePageIndicator mIndicator;
	
	@AfterViews
	void afterViews() {
		mAdapter = new TestFragmentAdapter(getContext());
		mAdapter.setOnItemClickListener(mInnerOnItemClickListener);
		mPager.setAdapter(mAdapter);

		mIndicator.setViewPager(mPager);
		mIndicator.setSnap(true);
	}
	
	private static class TestFragmentAdapter extends PagerAdapter {

		private Context context;
		private OnItemClickListener listener;

		public TestFragmentAdapter(Context context) {
			this.context = context;
		}

		public void setOnItemClickListener(OnItemClickListener listener) {
			this.listener = listener;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			GridView view = new GridView(context);
			view.setNumColumns(4);
			
			view.setCacheColorHint(Color.TRANSPARENT);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));

			view.setAdapter(new MyGridAdapter());
			Log.d(TAG, "instantiateItem() listener:" + listener);
			view.setOnItemClickListener(listener);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			container.addView(view, params);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			object = null;
		}
	}
	
	private static class MyGridAdapter extends BaseAdapter {

		private String[] items = {"照片", "拍摄"};
		
		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TypeSelectItemView view;
			
			if (convertView == null) {
				view = TypeSelectItemView_.build(parent.getContext());
			} else {
				view = (TypeSelectItemView) convertView;
			}
			
			view.bind((String) getItem(position));
			
			return view;
		}
		
	}
	
	private OnItemClickListener mInnerOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, "onItemClick() mOnItemClickListener:"
					+ mOnItemClickListener);
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(parent, view, position, id);
			}
		}
	};

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}
}
