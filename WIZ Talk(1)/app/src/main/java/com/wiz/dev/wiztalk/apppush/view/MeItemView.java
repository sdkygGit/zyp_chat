package com.wiz.dev.wiztalk.apppush.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;

@EViewGroup(R.layout.list_item_me)
public class MeItemView extends RelativeLayout {

	@ViewById(R.id.viewDividerBar)
	View viewDividerBar;
	
	@ViewById(R.id.viewDivider)
	View viewDivider;
	
	@ViewById(R.id.tv)
	TextView tv;
	
	@ViewById
	View viewGroupLineUp;
	
	@ViewById
	View viewGroupLineDown;
	
	public MeItemView(Context context) {
		super(context);
	}

	public void bind(Drawable left, CharSequence text) {
		tv.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
		tv.setText(text);
	}
	
	public void hideDivider() {
		viewDividerBar.setVisibility(View.GONE);
		viewDivider.setVisibility(View.GONE);
	}
	
	public static final int DIVIDER_STYLE_LINE = 0;
	public static final int DIVIDER_STYLE_BAR = 1;
	
	public void showDivider(int dividerStyle) {
		hideDivider();
		if (DIVIDER_STYLE_LINE == dividerStyle) {
			viewDivider.setVisibility(View.VISIBLE);
		}
		if (DIVIDER_STYLE_BAR == dividerStyle) {
			viewDividerBar.setVisibility(View.VISIBLE);
		}
	}
	
	public enum GroupLineStyle {
		UP, DOWN, BOTH
	}
	
	public void setupGroupLine(GroupLineStyle style) {
		viewGroupLineUp.setVisibility(View.GONE);
		viewGroupLineDown.setVisibility(View.GONE);
		
		switch (style) {
		case UP:
			viewGroupLineUp.setVisibility(View.VISIBLE);
			break;
		case DOWN:
			viewGroupLineDown.setVisibility(View.VISIBLE);
			break;
		case BOTH:
			viewGroupLineUp.setVisibility(View.VISIBLE);
			viewGroupLineDown.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
}
