package com.zj.expressway.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hutool.core.date.DateUtil;

/**
 * 日期
 *
 * @author JiangHaiJun
 * @date 2016-7-14
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {
    /**
     * 获取系统当前日期
     *
     * @return
     */
    public static String setDataToStr(long lData) {
        Date date = DateUtil.date(lData == 0 ? System.currentTimeMillis() : lData);
        String strDate = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
        return strDate;
    }

    /**
     * 日期比较大小
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

}
