package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/6/23.
 */
public class MyTextView extends EditText {
    public MyTextView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        // TODO Auto-generated constructor stub
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean getDefaultEditable() {//等同于在布局文件中设置 android:editable="false"
        return false;
    }
}
