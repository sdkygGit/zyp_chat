package com.wiz.dev.wiztalk.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.PixelUtils;

/**
 * 自定义ActionBar
 * Created by Dong on 2015/9/28.
 */
public class HeaderLayout extends LinearLayout {

    private static final String TAG = "HeaderLayout";

    private LayoutInflater mInflater;
    private View mHeader;
    private LinearLayout mLayoutLeftContainer;
    private LinearLayout mLayoutRightContainer;
    private TextView mHtvSubTitle;
    private LinearLayout mLayoutRightImageButtonLayout;
    private Button mRightImageButton;
    private onRightImageButtonClickListener mRightImageButtonClickListener;

    private LinearLayout mLayoutLeftImageButtonLayout;
    private ImageButton mLeftImageButton;
    private ImageView mRightImage;
    private onLeftImageButtonClickListener mLeftImageButtonClickListener;

    public enum HeaderStyle {// 头部整体样式
        DEFAULT_TITLE, TITLE_LIFT_IMAGEBUTTON, TITLE_RIGHT_IMAGEBUTTON, TITLE_DOUBLE_IMAGEBUTTON, TITLE_BOTH_IMAGE
    }

    public HeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.common_header, null);
        addView(mHeader);
        initViews();
    }

    public void initViews() {
        mHtvSubTitle = (TextView) findViewByHeaderId(R.id.header_htv_subtitle);
        mLayoutLeftContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_leftview_container);
        // mLayoutMiddleContainer = (LinearLayout)
        // findViewByHeaderId(R.id.header_layout_middleview_container);中间部分添加搜索或者其他按钮时可打开
        mLayoutRightContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_rightview_container);
    }

    public View findViewByHeaderId(int id) {
        return mHeader.findViewById(id);
    }

    public void init(HeaderStyle hStyle) {
        switch (hStyle) {
            case DEFAULT_TITLE:
                defaultTitle();
                break;

            case TITLE_LIFT_IMAGEBUTTON:
                defaultTitle();
                titleLeftImageButton();
                break;

            case TITLE_RIGHT_IMAGEBUTTON:
                defaultTitle();
                titleRightImageButton();
                break;

            case TITLE_DOUBLE_IMAGEBUTTON:
                defaultTitle();
                titleLeftImageButton();
                titleRightImageButton();
                break;
            case TITLE_BOTH_IMAGE:
                defaultTitle();
                titleLeftImageButton();
                titleRightRealImageButton();
                break;
        }
    }

    // 默认文字标题
    private void defaultTitle() {
        mLayoutLeftContainer.removeAllViews();
        mLayoutRightContainer.removeAllViews();
    }

    // 左侧自定义按钮
    private void titleLeftImageButton() {
        View mleftImageButtonView = mInflater.inflate(
                R.layout.common_header_button, null);
        mLayoutLeftContainer.addView(mleftImageButtonView);
        mLayoutLeftImageButtonLayout = (LinearLayout) mleftImageButtonView
                .findViewById(R.id.header_layout_imagebuttonlayout);
        mLeftImageButton = (ImageButton) mleftImageButtonView
                .findViewById(R.id.header_ib_imagebutton);
        mLayoutLeftImageButtonLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mLeftImageButtonClickListener != null) {
                    mLeftImageButtonClickListener.onClick();
                }
            }
        });
    }

    // 右侧自定义button按钮
    private void titleRightImageButton() {
        View mRightButtonView = mInflater.inflate(
                R.layout.common_header_rightbutton, null);
        mLayoutRightContainer.addView(mRightButtonView);
        mLayoutRightImageButtonLayout = (LinearLayout) mRightButtonView
                .findViewById(R.id.header_layout_imagebuttonlayout);
        mRightImageButton = (Button) mRightButtonView
                .findViewById(R.id.header_ib_imagebutton);
        mLayoutRightImageButtonLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mRightImageButtonClickListener != null) {
                    mRightImageButtonClickListener.onClick();
                }
            }
        });
    }

    // 右侧自定义imageView
    private void titleRightRealImageButton() {
        View mRightImageButtonView = mInflater.inflate(
                R.layout.common_header_right_image, null);
        mLayoutRightContainer.addView(mRightImageButtonView);
        mLayoutRightImageButtonLayout = (LinearLayout) mRightImageButtonView
                .findViewById(R.id.header_layout_imagebuttonlayout);
        mRightImage = (ImageView) mRightImageButtonView
                .findViewById(R.id.header_ib_imagebutton);
        mLayoutRightImageButtonLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mRightImageButtonClickListener != null) {
                    mRightImageButtonClickListener.onClick();
                }
            }
        });
    }

    /**
     * 获取右边按钮
     *
     * @param @return
     * @return Button
     * @throws
     * @Title: getRightImageButton
     * @Description: TODO
     */
    public Button getRightImageButton() {
        if (mRightImageButton != null) {
            return mRightImageButton;
        }
        return null;
    }

    /**
     * 获取右边imageview
     *
     * @param @return
     * @return Button
     * @throws
     * @Title: getRightImageView
     * @Description: TODO
     */
    public ImageView getRightImageView() {
        if (mRightImage != null) {
            return mRightImage;
        }
        return null;
    }

    public void setDefaultTitle(CharSequence title) {
        if (title != null) {
            mHtvSubTitle.setText(title);
        } else {
            mHtvSubTitle.setVisibility(View.GONE);
        }
    }

    /**
     * @param title
     * @param backid
     * @param text
     * @param onRightImageButtonClickListener
     */
    public void setTitleAndRightButton(CharSequence title, int backid, String text,
                                       onRightImageButtonClickListener onRightImageButtonClickListener) {
        setDefaultTitle(title);
        mLayoutRightContainer.setVisibility(View.VISIBLE);
        if (mRightImageButton != null && backid > 0) {
            mRightImageButton.setWidth(PixelUtils.dp2px(45));
            mRightImageButton.setHeight(PixelUtils.dp2px(40));
            mRightImageButton.setBackgroundResource(backid);
            mRightImageButton.setText(text);
            setOnRightImageButtonClickListener(onRightImageButtonClickListener);
        }
    }

    /**
     * 右边是图片
     *
     * @param title
     * @param resouceId
     * @param onRightImageButtonClickListener
     */
    public void setTitleAndRightImageButton(CharSequence title, int resouceId,
                                            onRightImageButtonClickListener onRightImageButtonClickListener) {
        setDefaultTitle(title);

        if (mRightImage != null && resouceId > 0) {
            mLayoutRightContainer.setVisibility(View.VISIBLE);
            mRightImage.setImageResource(resouceId);
            setOnRightImageButtonClickListener(onRightImageButtonClickListener);
        }
    }

    public void setTitleAndLeftImageButton(CharSequence title, int id,
                                           onLeftImageButtonClickListener listener) {
        setDefaultTitle(title);
        if (mLeftImageButton != null && id > 0) {
            mLeftImageButton.setImageResource(id);
            setOnLeftImageButtonClickListener(listener);
        }
        mLayoutRightContainer.setVisibility(View.INVISIBLE);
    }

    public void setOnRightImageButtonClickListener(
            onRightImageButtonClickListener listener) {
        mRightImageButtonClickListener = listener;
    }

    public interface onRightImageButtonClickListener {
        void onClick();
    }

    public void setOnLeftImageButtonClickListener(
            onLeftImageButtonClickListener listener) {
        mLeftImageButtonClickListener = listener;
    }

    public interface onLeftImageButtonClickListener {
        void onClick();
    }
}
