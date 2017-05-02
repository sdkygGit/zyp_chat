package com.wiz.dev.wiztalk.view.dialog;

import android.content.Context;

/**
 * Created by Admin on 2015/8/12.
 */
public class DialogTips extends DialogBase {
    boolean hasNegative;
    boolean hasTitle;
    /**
     * 
     * @param context 上下文
     * @param title 标题
     * @param message 内容
     * @param buttonText the text for positive button 
     * @param hasNegative the text for negative button
     * @param hasTitle 
     */
    public DialogTips(Context context, String title,String message,String buttonText,boolean hasNegative,boolean hasTitle) {
        super(context);
        super.setMessage(message);
        super.setNamePositiveButton(buttonText);
        this.hasNegative = hasNegative;
        this.hasTitle = hasTitle;
        super.setTitle(title);
    }

    /**通知的对话框样式
     * @param context
     * @param message
     * @param buttonText
     */
    public DialogTips(Context context,String message,String buttonText) {
        super(context);
        super.setMessage(message);
        super.setNamePositiveButton(buttonText);
        this.hasNegative = false;
        this.hasTitle = true;
        super.setTitle("提示");
        super.setCancel(false);
    }
    /**
     * 
     * @param context
     * @param message 
     * @param buttonText
     * @param negetiveText
     * @param title 标题
     * @param isCancel
     */
    public DialogTips(Context context, String message,String buttonText,String negetiveText,String title,boolean isCancel) {
        super(context);
        super.setMessage(message);
        super.setNamePositiveButton(buttonText);
        this.hasNegative=false;
        super.setNameNegativeButton(negetiveText);
        this.hasTitle = true;
        super.setTitle(title);
        super.setCancel(isCancel);
    }

    /**
     * 创建对话框
     */
    @Override
    protected void onBuilding() {
        super.setWidth(dip2px(mainContext, 300));
        if(hasNegative){
            super.setNameNegativeButton("取消");
        }
        if(!hasTitle){
            super.setHasTitle(false);
        }
    }

    public int dip2px(Context context,float dipValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int) (scale*dipValue+0.5f);
    }

    @Override
    protected void onDismiss() { }

    @Override
    protected void OnClickNegativeButton() {
        if(onCancelListener != null){
            onCancelListener.onClick(this, 0);
        }
    }

    /**
     * 确认按钮，触发onSuccessListener的onClick
     */
    @Override
    protected boolean OnClickPositiveButton() {
        if(onSuccessListener != null){
            onSuccessListener.onClick(this, 1);
        }
        return true;
    }
}
