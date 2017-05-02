package com.fanning.library;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by ynyxmac on 16/1/4.
 */

/////////

/**
 * Created by LHL on 2014/9/12.
 功能：弹出一个窗体，被弹出的窗体是自己定制的，用于combox或设置框

 用法如下：
 第一步，创建一个layout
 <?xml version="1.0" encoding="utf-8"?>
 <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="100dip" android:layout_height="wrap_content" android:background="#00FF00" android:orientation="vertical">
 <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="选择状态：" android:textColor="@android:color/white" android:textSize="20px"/>
 <RadioGroup android:id="@+id/radioGroup" android:layout_width="wrap_content" android:layout_height="wrap_content" android:orientation="vertical">
 <RadioButton android:layout_width="fill_parent" android:layout_height="wrap_content" android:text="在线"/>
 <RadioButton android:layout_width="fill_parent" android:layout_height="wrap_content" android:text="离线"/>
 <RadioButton android:layout_width="fill_parent" android:layout_height="wrap_content" android:text="隐身"/>
 </RadioGroup>
 <Button android:layout_width="fill_parent" android:layout_height="wrap_content" android:text="close" android:id="@+id/closebutton"

 android:onClick="OnPopuClose"  //注意这里可以直接指定事件
 />
 </LinearLayout>


 第二步：

 //弹出窗体中的事件
 public void OnPopuClose(View v)
 {
 ((Button)v).setText("点到了");
 }

 //窗体关闭事件
 public CPopupWindow.TOnPopwindowsClose OnCloseItem=new CPopupWindow.TOnPopwindowsClose()
 {
 @Override
 public void onClose(CPopupWindow v) {
 super.onClose(v);
 }
 };
 //调用
 public void onClick2(View v)
 {
 CPopWindow pwin=new CPopWindow(this,R.layout.popuform,100,300,true);
 pwin.OnPopwindowClose=OnCloseItem; //自动关闭事件
 Button but=(Button)pwin.MainWindow.findViewById(R.id.closebutton);
 pwin.ShowWindow(v);//这里有好多重载方法，根据需要调用
 }

 */
public class FNPopUpWindow {
    private Context mOwner;
    private int mMainWindowRes;
    private PopupWindow mPopupWindow;
    public View MainWindow;

    public static class TOnPopwindowsClose{
        public void onClose(FNPopUpWindow v) {
        }
    }
    //关闭窗体事件
    public TOnPopwindowsClose OnPopwindowClose=null;
    public FNPopUpWindow(
            Context AOwner,//显示主窗体
            int WinRes,//被显示出来的窗体
            int width,//弹出窗体的宽度
            int height,//弹出窗体的高度
            boolean AutoClose,//是否自动关闭
            boolean setbg
    )
    {
        init(AOwner, WinRes, width,height,AutoClose,setbg);
    }

    public FNPopUpWindow(
            Context AOwner,//显示主窗体
            int WinRes,//被显示出来的窗体
            int width,//弹出窗体的宽度
            int height,//弹出窗体的高度
            boolean AutoClose//是否自动关闭
    )
    {
        init(AOwner, WinRes, width,height,AutoClose, true);
    }

    private void init (Context AOwner,//显示主窗体
                       int WinRes,//被显示出来的窗体
                       int width,//弹出窗体的宽度
                       int height,//弹出窗体的高度
                       boolean AutoClose,//是否自动关闭
                       boolean setbg)
    {
        mOwner=AOwner;
        mMainWindowRes=WinRes;
        LayoutInflater layoutInflater = LayoutInflater.from(AOwner);
        MainWindow = layoutInflater.inflate(WinRes, null);
        mPopupWindow = new PopupWindow(MainWindow,width,height);
        if (AutoClose)
        {
            // 使其聚焦
            mPopupWindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupWindow.setOutsideTouchable(true);
            //刷新状态
            mPopupWindow.update();
            //点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
            if (setbg) //不设置bg可以保证相应key_back
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

            mPopupWindow.setOnDismissListener(OnPopClose);
        }

    }



    private android.widget.PopupWindow.OnDismissListener OnPopClose=
            new android.widget.PopupWindow.OnDismissListener()
            {
                @Override
                public void onDismiss() {
                    mPopupWindow.dismiss();
                    if (OnPopwindowClose!=null){
                        OnPopwindowClose.onClose(FNPopUpWindow.this);
                    }
                }
            };

    public void close(){
        mPopupWindow.dismiss();
    }
    public void setWindowWidth(int AWidth)
    {
        mPopupWindow.setWidth(AWidth);
    }
    public void setWindowHeight(int AHeight)
    {
        mPopupWindow.setHeight(AHeight);
    }
    //在 V 的正下方显示
    public void ShowWindow(View v)
    {
        mPopupWindow.showAsDropDown(v);
    }
    //在 V的正下方，再加上偏移量显示
    public void ShowWindow(View v,int x ,int y)
    {
        mPopupWindow.showAsDropDown(v,x,y);
    }
    //以主窗体为锚点，显示
    //Gravity 为： Gravity.CENTER ,  Gravity.BOTTOM
    public void ShowWindow(View v,int Gravity,int x ,int y)
    {
        mPopupWindow.showAsDropDown(v,x,y);
    }
}
