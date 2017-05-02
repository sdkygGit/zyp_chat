package com.epic.traverse.push.util;


import android.util.Log;

/**
 * Log统一管理类
 * Created by Dong on 2015/9/26.
 */
public class L {
    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isPrintDebug =true; // 是否需要打印bug，
    public static boolean isPrintError =true;// 是否需要打印error，
    //可以在application的onCreate函数里面初始化,或定义全局的常量集

    private static final String TAG = "Dong";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isPrintDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isPrintDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isPrintError)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isPrintDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isPrintDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isPrintDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isPrintError)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isPrintDebug)
            Log.v(tag, msg);
    }
}
