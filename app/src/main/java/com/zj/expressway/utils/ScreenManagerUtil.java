package com.zj.expressway.utils;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;

/**
 * Activity堆栈管理
 *
 */
public class ScreenManagerUtil {
    /*Activity堆栈*/
    private static Stack<Activity> activityStack = new Stack<>();

    /*当前类对象*/
    private static ScreenManagerUtil instance;

    /*方法重载*/
    private ScreenManagerUtil() {
        Log.v("Activity", " ***** create ScreenManager ******");
    }

    /**
     * 数据初始化
     *
     * @return
     */
    public static ScreenManagerUtil getScreenManager() {
        if (instance == null) {
            instance = new ScreenManagerUtil();
            if (activityStack == null) {
                activityStack = new Stack<>();
            }
        }
        return instance;
    }

    /**
     * 结束最后一个Activity
     */
    public void popActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * 退出当前Activity
     *
     * @param activity
     */
    public static void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 获取Activity的个数
     *
     * @return
     */
    public static int getActivitySize() {
        return activityStack.size();
    }

    /**
     * 获取当前活动的Activity
     *
     * @return
     */
    public static Activity currentActivity() {
        Activity activity;
        try {
            activity = activityStack.lastElement();
        } catch (Exception e) {
            return null;
        }
        return activity;
    }

    /**
     * 向堆中添加Activity
     *
     * @param activity
     */
    public static void pushActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 结束到指定Activity的所有Activity
     *
     * @param cls
     */
    @SuppressWarnings("rawtypes")
    public static void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            } else {
                popActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void popAllActivityExceptOne() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }
}
