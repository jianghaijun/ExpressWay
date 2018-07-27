package com.zj.expressway.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;

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
     * yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String setDataToStr(long lData) {
        Date date = DateUtil.date(lData == 0 ? System.currentTimeMillis() : lData);
        String strDate = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
        return strDate;
    }

    /**
     * 获取系统当前日期
     * yyyy-MM-dd
     * @param lData
     * @return
     */
    public static String setDataToStr2(long lData) {
        Date date = DateUtil.date(lData == 0 ? System.currentTimeMillis() : lData);
        String strDate = DateUtil.format(date, "yyyy-MM-dd");
        return strDate;
    }

    /**
     * 日期选择
     */
    public static void onYearMonthDayPicker(Activity mActivity, final Button btnDate) {
        final DatePicker picker = new DatePicker(mActivity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(mActivity, 10));
        picker.setRangeEnd(2100, 1, 31);
        picker.setRangeStart(2000, 1, 31);
        String date = btnDate.getText().toString();
        Date time = StrUtil.isEmpty(date) ? new Date() : DateUtil.parse(date);
        picker.setSelectedItem(DateUtil.year(time), DateUtil.month(time) + 1, DateUtil.dayOfMonth(time));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                btnDate.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    /**
     * 日期选择
     */
    public static void onYearMonthDayPicker(Activity mActivity, final TextView btnDate) {
        final DatePicker picker = new DatePicker(mActivity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(mActivity, 10));
        picker.setRangeEnd(2100, 1, 31);
        picker.setRangeStart(2000, 1, 31);
        String date = btnDate.getText().toString();
        Date time = StrUtil.isEmpty(date) ? new Date() : DateUtil.parse(date);
        picker.setSelectedItem(DateUtil.year(time), DateUtil.month(time) + 1, DateUtil.dayOfMonth(time));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                btnDate.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
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

    /**
     * 获取工序部位所占行数
     *
     * @param dropText
     * @param mPaint
     * @param dropWidth
     * @return
     */
    public static int getProcessPathNum(String dropText, TextPaint mPaint, int dropWidth) {
        StaticLayout layout = new StaticLayout(dropText, 0, dropText.length(), mPaint, dropWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.5F, true);
        return layout.getLineCount();
    }

    /**
     * 画笔
     *
     * @param textSize
     * @param dropText
     * @param color
     * @return
     */
    public static TextPaint getTextPaint(int textSize, String dropText, int color) {
        TextPaint mPaint = new TextPaint();
        // 文字矩阵区域
        Rect textBounds = new Rect();
        // 水印的字体大小
        mPaint.setTextSize(textSize);
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 水印的区域
        mPaint.getTextBounds(dropText, 0, dropText.length(), textBounds);
        // 水印的颜色
        mPaint.setColor(color);
        return mPaint;
    }

    /**
     * 获取字符串所占像素（px）
     *
     * @param strings
     * @param p
     * @return
     */
    public static int computeMaxStringWidth(String[] strings, Paint p) {
        float maxWidthF = 0.0f;
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int) (maxWidthF + 0.5);
        return maxWidth;
    }

    /**
     * 获取画笔
     *
     * @param textSize
     * @return
     */
    public static Paint getPaint(int textSize) {
        // 计算一个16sp中文所占像素
        Paint pFont = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        pFont.setTextSize(textSize);
        pFont.getTextBounds("豆", 0, 1, rect);
        return pFont;
    }

    /**
     * 获取画笔
     *
     * @param textSize
     * @return
     */
    public static Rect getRect(int textSize) {
        // 计算一个16sp中文所占像素
        Paint pFont = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        pFont.setTextSize(textSize);
        pFont.getTextBounds("豆", 0, 1, rect);
        return rect;
    }

    /**
     * 获取一行高度
     *
     * @param pFont
     * @return
     */
    public static int getOneRowsHeight(Paint pFont) {
        Paint.FontMetrics fm = pFont.getFontMetrics();
        int oneSizeHeight = (int) (fm.descent - fm.ascent);
        return oneSizeHeight;
    }

}
