package com.wiz.dev.wiztalk.view;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wiz.dev.wiztalk.adapter.SectionedBaseAdapter2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.AbsListView.OnScrollListener;

public class PinnedHeaderPullToRefreshListView extends PullToRefreshListView implements OnScrollListener {

    private OnScrollListener mOnScrollListener;

    public static interface PinnedSectionedHeaderAdapter {
        public boolean isSectionHeader(int position);

        public int getSectionForPosition(int position);

        public View getSectionHeaderView(int section, View convertView, ViewGroup parent);

        public int getSectionHeaderViewType(int section);

        public int getCount();

    }

    private PinnedSectionedHeaderAdapter mAdapter;
    private View mCurrentHeader;
    private int mCurrentHeaderViewType = 0;
    private float mHeaderOffset;
    private boolean mShouldPin = true;
    private int mCurrentSection = 0;
    private int mWidthMode;
    private int mHeightMode;

    public PinnedHeaderPullToRefreshListView(Context context) {
        super(context);
        super.setOnScrollListener(onScrollListener);
    }

    public PinnedHeaderPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(onScrollListener);
    }

    public PinnedHeaderPullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        super.setOnScrollListener(onScrollListener);
    }

    public void setPinHeaders(boolean shouldPin) {
        mShouldPin = shouldPin;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mCurrentHeader = null;
        mAdapter = (PinnedSectionedHeaderAdapter) adapter;
        super.setAdapter(adapter);
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }

            if (mAdapter == null || mAdapter.getCount() == 0 || !mShouldPin || (firstVisibleItem < getHeaderViewsCount())) {
                mCurrentHeader = null;
                mHeaderOffset = 0.0f;
                for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                    View header = getChildAt(i);
                    if (header != null) {
                        header.setVisibility(VISIBLE);
                    }
                }
                return;
            }

            firstVisibleItem -= getHeaderViewsCount();

            int section = mAdapter.getSectionForPosition(firstVisibleItem);
            int viewType = mAdapter.getSectionHeaderViewType(section);
            mCurrentHeader = getSectionHeaderView(section, mCurrentHeaderViewType != viewType ? null : mCurrentHeader);
            ensurePinnedHeaderLayout(mCurrentHeader);
            mCurrentHeaderViewType = viewType;

            mHeaderOffset = 0.0f;

            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                if (mAdapter.isSectionHeader(i)) {
                    View header = getChildAt(i - firstVisibleItem);
                    float headerTop = header.getTop();
                    float pinnedHeaderHeight = mCurrentHeader.getMeasuredHeight();
                    header.setVisibility(VISIBLE);
                    if (pinnedHeaderHeight >= headerTop && headerTop > 0) {
                        mHeaderOffset = headerTop - header.getHeight();
                    } else if (headerTop <= 0) {
                        header.setVisibility(INVISIBLE);
                    }
                }
            }

            invalidate();
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }
    };

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = section != mCurrentSection || oldView == null;

        View view = mAdapter.getSectionHeaderView(section, oldView, this);
        if (shouldLayout) {
            // a new section, thus a new header. We should lay it out again
            ensurePinnedHeaderLayout(view);
            mCurrentSection = section;
        }
        return view;
    }

    protected int getHeaderViewsCount() {
        return getRefreshableView().getHeaderViewsCount();
    }

    private void ensurePinnedHeaderLayout(View header) {
        if (header.isLayoutRequested()) {
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), mWidthMode);

            int heightSpec;
            ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            header.measure(widthSpec, heightSpec);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAdapter == null || !mShouldPin || mCurrentHeader == null)
            return;
        int saveCount = canvas.save();
        canvas.translate(0, mHeaderOffset);
        canvas.clipRect(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight()); // needed
        // for
        // <
        // HONEYCOMB
        mCurrentHeader.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    //    @Override
//    public void setOnScrollListener(OnScrollListener l) {
//        mOnScrollListener = l;
//    }
    public void setOnScrollListenerExt(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public static abstract class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int rawPosition, long id) {
            int count = 0;
            int headerViewCount = 0;
            int footerViewCount = 0;

            SectionedBaseAdapter2 adapter;
            if (adapterView.getAdapter().getClass().equals(HeaderViewListAdapter.class)) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) adapterView.getAdapter();
                adapter = (SectionedBaseAdapter2) wrapperAdapter.getWrappedAdapter();
                count = adapter.getCount();

                //liuxue
                headerViewCount = wrapperAdapter.getHeadersCount();
                footerViewCount = wrapperAdapter.getFootersCount();
            } else {
                adapter = (SectionedBaseAdapter2) adapterView.getAdapter();
                count = adapter.getCount();
            }

//            String TAG = "PinnedHeader";
//            Log.d(TAG, "onItemClick() rawPosition:" + rawPosition);
//            Log.d(TAG, "onItemClick() headerViewCount:" + headerViewCount);
//            Log.d(TAG, "onItemClick() footerViewCount:" + footerViewCount);
//            Log.d(TAG, "onItemClick() count:" + count);

            //liuxue
            if (headerViewCount > 0 && rawPosition < headerViewCount) {
                onHeaderViewClick(adapterView, view, rawPosition);
                return;
            }

            //liuxue
            if (footerViewCount > 0 && rawPosition >= count + headerViewCount) {
                onFooterViewClick(adapterView, view, rawPosition - count - headerViewCount);
                return;
            }

            //liuxue
//            int section = adapter.getSectionForPosition(rawPosition);
//            int position = adapter.getPositionInSectionForPosition(rawPosition);
            int section = adapter.getSectionForPosition(rawPosition - headerViewCount);
            int position = adapter.getPositionInSectionForPosition(rawPosition - headerViewCount);

            if (position == -1) {
                onSectionClick(adapterView, view, section, id);
            } else {
                onItemClick(adapterView, view, section, position, id);
            }
        }

        public abstract void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id);

        public abstract void onSectionClick(AdapterView<?> adapterView, View view, int section, long id);

        public abstract void onHeaderViewClick(AdapterView<?> adapterView, View view, int position);
        public abstract void onFooterViewClick(AdapterView<?> adapterView, View view, int position);
    }

    public void addHeaderView(View v) {
        getRefreshableView().addHeaderView(v);
    }

    public void addFooterView(View v) {
        getRefreshableView().addFooterView(v);
    }
}
