package com.zj.expressway.utils;

import android.content.Context;

/**
 * Create dell By 2018/5/10 11:40
 */

public class ProviderUtil {
    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".provider";
    }
}
