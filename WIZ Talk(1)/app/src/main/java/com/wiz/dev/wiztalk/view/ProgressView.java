package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.wiz.dev.wiztalk.R;

public class ProgressView extends View {

    float textSize;
    int textColor;
    int progressColor;
    int width = 200;
    int height = 50;


    Paint paint;

    float progress;

    float max_progress;
    String text = "下载中";


    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ProgressButtonAttrs);
        textSize = mTypedArray.getDimension(
                R.styleable.ProgressButtonAttrs_text_size, 16);
        textColor = mTypedArray.getColor(
                R.styleable.ProgressButtonAttrs_text_color, Color.WHITE);
        progressColor = mTypedArray.getColor(
                R.styleable.ProgressButtonAttrs_progress_color, Color.WHITE);


        progress = mTypedArray.getInt(
                R.styleable.ProgressButtonAttrs_curr_progress, 0);

        max_progress = mTypedArray.getInt(
                R.styleable.ProgressButtonAttrs_max_progress, 0);
        mTypedArray.recycle();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        paint.setColor(progressColor);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRoundRect(new RectF(5, 5, width - 10, height - 10), (height - 10) / 2, (height - 10) / 2, paint);// 框框


        if (progress > 10) {

            if (progress > max_progress)
                progress = max_progress;
            paint.setStyle(Style.FILL);

            canvas.drawRoundRect(new RectF(5, 5, (width - 10) * (progress / max_progress), (height - 10)), (height - 10) / 2, (height - 10) / 2, paint);//进度
        }


        if (text != "") {
            paint.setStyle(Style.FILL);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float y = height/2 - (fm.ascent + fm.descent)/2;
            canvas.drawText(text, width  / 2, y, paint);
        }

    }

    void drawProgress(Canvas canvas, Paint paint) {

//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setColor(progressColor);
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(5);

        if (progress > 0) {
//			canvas.drawRoundRect(new RectF(5, 5, width - 10, height - 10), (height - 10) / 2, (height - 10) / 2, paint);// 框框
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            if (progress > max_progress)
                progress = max_progress;
            paint.setStyle(Style.FILL);
            canvas.drawRoundRect(new RectF(5, 5, (width - 10) * (progress / max_progress), height - 10), (height - 10) / 2, (height - 10) / 2, paint);//进度
        }

        paint = null;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if(progress>10){
            text = progress + "%";
            invalidate();
        }
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {// 父类测量的值,即该View的测量模式不是wrap_content
            width = mWidth;
        } else {
            width = getNeedWidth() + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {// 父类有固定的大小，如父类是match_parent,100dp
                width = Math.min(width, mWidth);
            }
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            height = mHeight;
        } else {
            height = getNeedHeight() + getPaddingTop() + getPaddingBottom();
            if (widthMode == MeasureSpec.AT_MOST) {
                height = Math.min(mHeight, height);
            }
        }

        setMeasuredDimension(width, height);
    }

    // 定义需要的宽度，要转化为dp值
    private int getNeedHeight() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                220, getResources().getDisplayMetrics());
    }

    // 定义需要的高度，要转化为dp值
    private int getNeedWidth() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getResources().getDisplayMetrics());
    }

}
