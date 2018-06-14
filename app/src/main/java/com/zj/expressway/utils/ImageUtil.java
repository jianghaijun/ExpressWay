package com.zj.expressway.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.vinaygaba.rubberstamp.RubberStamp;
import com.vinaygaba.rubberstamp.RubberStampConfig;
import com.vinaygaba.rubberstamp.RubberStampPosition;
import com.zj.expressway.R;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.UserLevelBean;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;

import java.util.Calendar;
import java.util.List;

import cn.hutool.core.date.DateUtil;

public class ImageUtil {
    /**
     * 设置水印图片在左上角
     *
     * @param mContext
     * @param baseMap       // 需要添加水印的照片
     * @param filePath      // 工序位置
     * @param photosBean    // 照片bean
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(Context mContext, Bitmap baseMap, String filePath, PhotosBean photosBean) {
        return createWaterMaskBitmap(baseMap, mContext, filePath, photosBean);
    }

    /**
     * 给图片添加水印图和文字
     *
     * @param mContext
     * @param baseMap       // 需要添加水印的照片
     * @param filePath      // 工序位置
     * @param photosBean    // 照片bean
     * @return
     */
    private static Bitmap createWaterMaskBitmap(Bitmap baseMap, Context mContext, String filePath, PhotosBean photosBean) {
        if (baseMap == null) {
            return null;
        }
        // 原图尺寸
        int width = baseMap.getWidth();
        int height = baseMap.getHeight();
        // 原图与固定尺寸比
        float widthMultiple = Float.valueOf(width) / Float.valueOf(1280);
        float heightMultiple = Float.valueOf(height) / Float.valueOf(960);
        // 水印尺寸
        int watermarkWidth = (int) (350 * widthMultiple);
        int watermarkHeight = (int) (145 * heightMultiple);
        // 施工部位所占长度
        int len = TextUtils.isEmpty(filePath) ? 0 : filePath.length();
        String qualityUserName = "";    // 质检负责人姓名
        if (len > 3) {
            List<UserLevelBean> qualityBeanList;
            String rootLevel = filePath.substring(3, 4);
            switch (rootLevel) {
                case "一":
                    qualityBeanList = DataSupport.where("rootLevelId=1").find(UserLevelBean.class);
                    if (qualityBeanList != null && qualityBeanList.size() > 0) {
                        qualityUserName = qualityBeanList.get(0).getRealName();
                    } else {
                        qualityUserName = "王皓";
                    }
                    break;
                case "二":
                    qualityBeanList = DataSupport.where("rootLevelId=2").find(UserLevelBean.class);
                    if (qualityBeanList != null && qualityBeanList.size() > 0) {
                        qualityUserName = qualityBeanList.get(0).getRealName();
                    } else {
                        qualityUserName = "李欣";
                    }
                    break;
                case "三":
                    qualityBeanList = DataSupport.where("rootLevelId=3").find(UserLevelBean.class);
                    if (qualityBeanList != null && qualityBeanList.size() > 0) {
                        qualityUserName = qualityBeanList.get(0).getRealName();
                    } else {
                        qualityUserName = "赵泽明";
                    }
                    break;
                case "四":
                    qualityBeanList = DataSupport.where("rootLevelId=4").find(UserLevelBean.class);
                    if (qualityBeanList != null && qualityBeanList.size() > 0) {
                        qualityUserName = qualityBeanList.get(0).getRealName();
                    } else {
                        qualityUserName = "王和平";
                    }
                    break;
                case "五":
                    qualityBeanList = DataSupport.where("rootLevelId=5").find(UserLevelBean.class);
                    if (qualityBeanList != null && qualityBeanList.size() > 0) {
                        qualityUserName = qualityBeanList.get(0).getRealName();
                    } else {
                        qualityUserName = "李立山";
                    }
                    break;
                case "六":
                    qualityBeanList = DataSupport.where("rootLevelId=6").find(UserLevelBean.class);
                    if (qualityBeanList != null && qualityBeanList.size() > 0) {
                        qualityUserName = qualityBeanList.get(0).getRealName();
                    } else {
                        qualityUserName = "马平";
                    }
                    break;
            }
        }
        // 计算一个16sp中文所占像素
        Paint pFont = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        pFont.setTextSize(16 * widthMultiple);
        pFont.getTextBounds("豆", 0, 1, rect);
        Paint.FontMetrics fm = pFont.getFontMetrics();
        int oneSizeHeight = (int) (fm.descent - fm.ascent);
        // 工程部位水印文字宽度
        int waterTextWith = watermarkWidth - computeMaxStringWidth(new String[]{"施工部位："}, pFont) - DensityUtil.dip2px(10);
        TextPaint mPaint = new TextPaint();
        // 文字矩阵区域
        Rect textBounds = new Rect();
        // 水印的字体大小
        mPaint.setTextSize(16 * widthMultiple);
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 水印的区域
        mPaint.getTextBounds(filePath, 0, filePath.length(), textBounds);
        // 水印的颜色
        mPaint.setColor(Color.BLACK);

        // 计算工程部位所占行数
        Bitmap gcbwBitmap = Bitmap.createBitmap(watermarkWidth, watermarkHeight, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(gcbwBitmap);
        can.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        can.drawColor(Color.argb(255, 255, 255, 255));
        StaticLayout gcbwLayout = new StaticLayout(filePath, 0, filePath.length(), mPaint, waterTextWith, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.5F, true);
        // 文字开始的坐标
        float x = width - gcbwBitmap.getWidth() + computeMaxStringWidth(new String[]{"施工部位："}, pFont) + DensityUtil.dip2px(5);
        float y = height - 0;
        // 画文字
        can.translate(x, y);
        gcbwLayout.draw(can);

        int rows = gcbwLayout.getLineCount();
        // 设置水印高度
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect r = new Rect();
        p.setTextSize(20 * widthMultiple);
        p.getTextBounds("豆", 0, 1, r);
        Paint.FontMetrics fm1 = pFont.getFontMetrics();
        int marginSum = (int) (DensityUtil.dip2px(10 * heightMultiple) + (fm1.descent - fm1.ascent) + (rows + 3) * oneSizeHeight);
        if (marginSum > watermarkHeight) {
            watermarkHeight = marginSum;
        }
        // 创建水印画布
        Bitmap watermarkBitmap = Bitmap.createBitmap(watermarkWidth, watermarkHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(Color.argb(255, 255, 255, 255));
        // 将Logo添加到底板中
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.watermark_logo);
        RubberStampConfig config = new RubberStampConfig.RubberStampConfigBuilder()
                .base(watermarkBitmap)
                .rubberStamp(bitmap)
                .rubberStampPosition(RubberStampPosition.TOP_LEFT)
                .margin(10, 2)
                .build();
        RubberStamp rubberStamp = new RubberStamp(mContext);
        watermarkBitmap = rubberStamp.addStamp(config);
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "中交一公局", p, r, (int) (bitmap.getWidth() + DensityUtil.dip2px(10) + 5 * widthMultiple), DensityUtil.dip2px(2 * heightMultiple));
        // 距离上面的间距
        int marginTopSize = (int) (DensityUtil.dip2px(5 * heightMultiple) + (fm1.descent - fm1.ascent));
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "工程名称：" + mContext.getString(R.string.app_name), pFont, rect, DensityUtil.dip2px(5), marginTopSize);
        // 距离上面间距
        marginTopSize += oneSizeHeight;
        // 循环向图片上添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "施工部位：", pFont, rect, DensityUtil.dip2px(5), marginTopSize);
        int mar = rows * oneSizeHeight + marginTopSize;
        // 根据用户级别显示不同按钮(0:施工人员; 1:质检部长; 2:监理; 3:领导)
        String userLevel = (String) SpUtil.get(mContext, ConstantsUtil.USER_LEVEL, "");
        String userName = (String) SpUtil.get(mContext, "UserName", "");
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(photosBean.getCreate_time());
        int apm = mCalendar.get(Calendar.AM_PM);
        String am_pm;
        if (apm == 0) {
            am_pm = "AM";
        } else {
            am_pm = "PM";
        }

        if (userLevel.equals("0")) {
            watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "现场技术员：" + userName + "    质检负责人：" + qualityUserName, pFont, rect, DensityUtil.dip2px(5), mar);
            mar += oneSizeHeight;
            watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "拍照时间：" + DateUtil.formatDateTime(DateUtil.date(DateUtil.date(photosBean.getCreate_time()))) + "  " + am_pm, pFont, rect, DensityUtil.dip2px(5), mar);
        }

        if (userLevel.equals("2")) {
            watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "现场监理：" + userName + "    质检负责人：" + qualityUserName, pFont, rect, DensityUtil.dip2px(5), mar);
            mar += oneSizeHeight;
            watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "拍照时间：" + DateUtil.formatDateTime(DateUtil.date(DateUtil.date(photosBean.getCreate_time()))) + "  " + am_pm, pFont, rect, DensityUtil.dip2px(5), mar);
        }

        // 创建一个新的和SRC长度宽度一样的位图
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 将该图片作为画布
        canvas = new Canvas(newBitmap);
        // 在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(baseMap, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermarkBitmap, width - watermarkBitmap.getWidth(), height - watermarkBitmap.getHeight(), null);

        StaticLayout layout = new StaticLayout(filePath, 0, filePath.length(), mPaint, waterTextWith, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.5F, true);
        // 文字开始的坐标
        float textX = width - watermarkBitmap.getWidth() + computeMaxStringWidth(new String[]{"施工部位："}, pFont) + DensityUtil.dip2px(5);
        float textY = height - watermarkBitmap.getHeight() + marginTopSize - 5 * heightMultiple;
        // 画文字
        canvas.translate(textX, textY);
        layout.draw(canvas);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();

        return newBitmap;
    }

    /**
     * 给图片添加文字到左上角
     *
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Bitmap bitmap, String text, Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        return drawTextToBitmap(bitmap, text, paint, paddingLeft, paddingTop + bounds.height());
    }

    // 图片上绘制文字
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    /**
     * 获取字符串所占像素（px）
     *
     * @param strings
     * @param p
     * @return
     */
    private static int computeMaxStringWidth(String[] strings, Paint p) {
        float maxWidthF = 0.0f;
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int) (maxWidthF + 0.5);
        return maxWidth;
    }
}
