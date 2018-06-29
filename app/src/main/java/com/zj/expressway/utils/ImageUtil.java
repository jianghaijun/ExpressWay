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

import com.vinaygaba.rubberstamp.RubberStamp;
import com.vinaygaba.rubberstamp.RubberStampConfig;
import com.vinaygaba.rubberstamp.RubberStampPosition;
import com.zj.expressway.R;
import com.zj.expressway.bean.PhotosBean;

import org.xutils.common.util.DensityUtil;

import cn.hutool.core.date.DateUtil;

public class ImageUtil {
    /**
     * 设置水印图片在左上角
     *
     * @param mContext
     * @param baseMap     // 需要添加水印的照片
     * @param processPath // 工序位置
     * @param photosBean  // 照片bean
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(Context mContext, Bitmap baseMap, String processPath, PhotosBean photosBean) {
        return createWaterMaskBitmap(baseMap, mContext, processPath, photosBean);
    }

    /**
     * 给图片添加水印图和文字
     *
     * @param mContext
     * @param baseMap     // 需要添加水印的照片
     * @param processPath // 工序位置
     * @param photosBean  // 照片bean
     * @return
     */
    private static Bitmap createWaterMaskBitmap(Bitmap baseMap, Context mContext, String processPath, PhotosBean photosBean) {
        if (baseMap == null) {
            return null;
        }

        // 原图尺寸
        int width = baseMap.getWidth();
        int height = baseMap.getHeight();
        // 水印尺寸
        float widthMultiple = Float.valueOf(width / 1280);
        float heightMultiple = Float.valueOf(height / 960);
        int watermarkWidth = (int) (350 * widthMultiple);
        int watermarkHeight = (int) (150 * heightMultiple);
        // 原图与固定尺寸比
        // 水印文字大小
        int textSize = (int) (18 * widthMultiple);
        // 施工部位所占宽度
        int constructionSiteWidth = DateUtils.computeMaxStringWidth(new String[]{"施工部位："}, DateUtils.getPaint(textSize));
        // 工序位置水印宽度== 水印宽度-左右边距-文字"施工部位："所占宽度
        int processPathWatermarkWidth = watermarkWidth - DensityUtil.dip2px(10) - constructionSiteWidth;
        // 计算工序位置水印总行数
        int processPathRows = DateUtils.getProcessPathNum(processPath, DateUtils.getTextPaint(textSize, processPath, Color.BLACK), processPathWatermarkWidth);
        // 一行高度
        int oneSizeHeight = DateUtils.getOneRowsHeight(DateUtils.getPaint(textSize));
        // 计算水印总高度==总行数*每行高度 + 上下边距
        int rowSumHeight = (processPathRows + 4) * oneSizeHeight + DensityUtil.dip2px(10);
        // 判断是否大于水印默认高度
        if (rowSumHeight > watermarkHeight) {
            watermarkHeight = rowSumHeight;
        } else if (watermarkHeight - rowSumHeight > 20) { // 解决竖屏拍照水印高度过高
            watermarkHeight = rowSumHeight + 20;
        }

        Paint paint = DateUtils.getPaint(textSize);
        Rect rect = DateUtils.getRect(textSize);
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
                .margin(10, 5)
                .build();
        RubberStamp rubberStamp = new RubberStamp(mContext);
        watermarkBitmap = rubberStamp.addStamp(config);
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, ConstantsUtil.projectName, DateUtils.getPaint((int) (20 * widthMultiple)), DateUtils.getRect((int) (20 * widthMultiple)), DensityUtil.dip2px(30), DensityUtil.dip2px(5));
        // 距离上面的间距
        int marginTopSize = DensityUtil.dip2px(10) + oneSizeHeight;
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, ConstantsUtil.engineeringName + mContext.getString(R.string.app_name), paint, rect, DensityUtil.dip2px(5), marginTopSize);
        // 距离上面间距
        marginTopSize += oneSizeHeight;
        int topSize = marginTopSize;
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, ConstantsUtil.constructionSite, paint, rect, DensityUtil.dip2px(5), marginTopSize);
        // 距离上面间距
        marginTopSize += processPathRows * oneSizeHeight;
        // 添加文字
        // 现场技术员
        String userName = (String) SpUtil.get(mContext, "UserName", "");
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, ConstantsUtil.technician + userName + ConstantsUtil.inspection + "管理员", paint, rect, DensityUtil.dip2px(5), marginTopSize);
        marginTopSize += oneSizeHeight;
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, ConstantsUtil.takeTime + DateUtil.formatDateTime(DateUtil.date(DateUtil.date(photosBean.getCreateTime()))), paint, rect, DensityUtil.dip2px(5), marginTopSize);

        // 创建一个新的和SRC长度宽度一样的位图
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 将该图片作为画布
        canvas = new Canvas(newBitmap);
        // 在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(baseMap, 0, 0, null);
        // 在画布上绘制水印图片
        canvas.drawBitmap(watermarkBitmap, width - watermarkBitmap.getWidth(), height - watermarkBitmap.getHeight(), null);
        StaticLayout layout = new StaticLayout(processPath, 0, processPath.length(), DateUtils.getTextPaint(textSize, processPath, Color.BLACK), processPathWatermarkWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.5F, true);
        // 文字开始的坐标
        float textX = width - watermarkBitmap.getWidth() + constructionSiteWidth + DensityUtil.dip2px(5);
        float textY = height - watermarkBitmap.getHeight() + topSize - DensityUtil.dip2px(5);
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

}
