package com.zj.expressway.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.bean.ProcessDictionaryBean;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;

import java.util.List;


public class ProcessPopupWindow extends PopupWindow {
    private Activity mActivity;
    private View mView;
    private int width;

    public ProcessPopupWindow(Activity mActivity, String dictId) {
        super();
        this.mActivity = mActivity;
        this.initPopupWindow();
        List<ProcessDictionaryBean> beenList = DataSupport.where("parentId = ?", dictId).find(ProcessDictionaryBean.class);
        setDate(beenList);
    }

    /**
     * 初始化
     */
    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = inflater.inflate(R.layout.popwindow_working, null);
        this.setContentView(mView);
        width = DensityUtil.getScreenWidth();
        this.setWidth((int) (width * 0.8));
        this.setHeight((int) (DensityUtil.getScreenWidth() * 0.5));
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        ColorDrawable background = new ColorDrawable(0x4f000000);
        this.setBackgroundDrawable(background);
        this.draw();
    }

    /**
     * 添加数据
     *
     * @param workList
     */
    private void setDate(List<ProcessDictionaryBean> workList) {
        // 添加数据
        TableLayout tabLay = (TableLayout) mView.findViewById(R.id.tabLay);
        // 动态添加行
        for (int i = 0; i < workList.size(); i++) {
            ProcessDictionaryBean bean = workList.get(i);
            TableRow tableRow = new TableRow(mActivity);
            // 编号
            TextView txtNo = new TextView(mActivity);
            txtNo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtNo.setText((i + 1) + "");
            txtNo.setTextSize(14);
            txtNo.setGravity(Gravity.CENTER);
            txtNo.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(DensityUtil.dip2px(39), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(txtNo, lp);

            // 工序名称
            TextView txtWorkingName = new TextView(mActivity);
            txtWorkingName.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtWorkingName.setText(bean.getDictName());
            txtWorkingName.setTextSize(14);
            txtWorkingName.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            int widthSize = (int) ((width * 0.8) - DensityUtil.dip2px(10 + 40 + 80 * 2 + 3));
            lp = new TableRow.LayoutParams(widthSize, DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtWorkingName.setPadding(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
            txtWorkingName.setSingleLine(false);
            txtWorkingName.setHorizontallyScrolling(true);
            txtWorkingName.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtWorkingName.setGravity(Gravity.CENTER_VERTICAL);
            tableRow.addView(txtWorkingName, lp);

            // 拍照要求
            TextView txtDistance = new TextView(mActivity);
            txtDistance.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtDistance.setText(bean.getPhotoDistance());
            txtDistance.setTextSize(14);
            txtDistance.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(79), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtDistance.setGravity(Gravity.CENTER);
            tableRow.addView(txtDistance, lp);

            // 照片数量
            TextView txtPhotoNum = new TextView(mActivity);
            txtPhotoNum.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtPhotoNum.setText(bean.getPhotoNumber() + "");
            txtPhotoNum.setTextSize(14);
            txtPhotoNum.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(79), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtPhotoNum.setGravity(Gravity.CENTER);
            tableRow.addView(txtPhotoNum, lp);

            tabLay.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 绘制
     */
    private void draw() {
        this.mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    /**
     * 显示在控件下右方
     *
     * @param parent
     */
    public void showAtDropDownRight(View parent) {
        if (parent.getVisibility() == View.GONE) {
            this.showAtLocation(parent, 0, 0, 0);
        } else {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, 0, location[0] + parent.getWidth() - this.getWidth(), location[1] + parent.getHeight());
        }
    }
}
