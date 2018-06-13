package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.SameDayBean;
import com.zj.expressway.model.SameDayModel;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.DateUtils;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *                     _ooOoo_
 *                    o8888888o
 *                    88" . "88
 *                    (| -_- |)
 *                    O\  =  /O
 *                 ____/`---'\____
 *               .'  \\|     |//  `.
 *              /  \\|||  :  |||//  \
 *             /  _||||| -:- |||||-  \
 *             |   | \\\  -  /// |   |
 *             | \_|  ''\---/''  |   |
 *             \  .-\__  `-`  ___/-. /
 *           ___`. .'  /--.--\  `. . __
 *        ."" '<  `.___\_<|>_/___.'  >'"".
 *       | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *       \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                     `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 			   佛祖保佑       永无BUG
 *       Created by HaiJun on 2018/6/11 16:49
 *       工序报表
 */
public class ProcessReportActivity extends BaseActivity {
    @ViewInject(R.id.barChartMonth)
    private BarChart barChartMonth;
    @ViewInject(R.id.barChart)
    private BarChart barChart;
    @ViewInject(R.id.txtReportTitle)
    private TextView txtReportTitle;
    @ViewInject(R.id.txtBeganDate)
    private TextView txtBeganDate;
    @ViewInject(R.id.txtEndDate)
    private TextView txtEndDate;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.tbSameDay)
    private TableLayout tbSameDay;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_process_report);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        mContext = this;

        txtTitle.setText("工序报表");
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));

        txtBeganDate.setText(DateUtil.format(new Date(), "yyyy-MM-dd"));
        txtEndDate.setText(DateUtil.format(DateUtil.tomorrow(), "yyyy-MM-dd"));

        if (ConstantsUtil.sameDayBean != null) {
            initNewBarData(ConstantsUtil.sameDayBean);
            if (ConstantsUtil.sameDayBean.getSearchTotalList() != null) {
                initSameDay(ConstantsUtil.sameDayBean.getSearchTotalList());
            } else {
                initSameDay(new ArrayList<SameDayBean>());
            }
            if (ConstantsUtil.sameDayBean.getHistoryTotalList() != null) {
                initHistoryData(ConstantsUtil.sameDayBean.getHistoryTotalList());
            } else {
                initHistoryData(new ArrayList<SameDayBean>());
            }
        } else {
            initNewBarData(new SameDayBean());
            initSameDay(new ArrayList<SameDayBean>());
            initHistoryData(new ArrayList<SameDayBean>());
        }
    }

    /**
     * 设置历史数据
     *
     * @param bean
     */
    private void initNewBarData(SameDayBean bean) {
        txtReportTitle.setText("本月工序完成统计表（" + bean.getMonth() + "）");

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, Float.parseFloat(bean.getTotalNum1())));
        entries.add(new BarEntry(1f, Float.parseFloat(bean.getTotalNum2())));
        entries.add(new BarEntry(2f, Float.parseFloat(bean.getTotalNum3())));
        entries.add(new BarEntry(3f, Float.parseFloat(bean.getTotalNum4())));
        entries.add(new BarEntry(4f, Float.parseFloat(bean.getTotalNum5())));
        entries.add(new BarEntry(5f, Float.parseFloat(bean.getTotalNum6())));

        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(new int[]{Color.argb(255, 247, 142, 98)});
        set.setHighlightEnabled(false);
        set.setValueTextColor(ContextCompat.getColor(mContext, R.color.white));
        set.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String s = String.valueOf(value).substring(0, String.valueOf(value).indexOf("."));
                return s + "道";
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        barChartMonth.setData(data);
        barChartMonth.setFitBars(true); // make the x-axis fit exactly all bars
        barChartMonth.invalidate();
        barChartMonth.getAxisRight().setEnabled(false); //右侧不显示Y轴
        barChartMonth.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //设置X轴的位置
        barChartMonth.getLegend().setEnabled(false);
        barChartMonth.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
        barChartMonth.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String s = "";
                switch (String.valueOf(value)) {
                    case "0.0":
                        s = "一分部";
                        break;
                    case "1.0":
                        s = "二分部";
                        break;
                    case "2.0":
                        s = "三分部";
                        break;
                    case "3.0":
                        s = "四分部";
                        break;
                    case "4.0":
                        s = "五分部";
                        break;
                    case "5.0":
                        s = "六分部";
                        break;
                }
                return s;
            }
        });

        barChartMonth.setPinchZoom(true);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
        barChartMonth.getAxisLeft().setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙
        Description desc = new Description();
        desc.setText("");
        barChartMonth.setDescription(desc);//设置描述
        barChartMonth.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.white));
        barChartMonth.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.white)); // X轴文字颜色
        barChartMonth.setBackgroundColor(ContextCompat.getColor(mContext, R.color.report_bg));
        //设置动画
        barChartMonth.animateXY(0, 2000);
    }

    /**
     * 设置当日工序完成数据
     *
     * @param beanList
     */
    private void initSameDay(List<SameDayBean> beanList) {
        tbSameDay.removeAllViews();

        List<SameDayBean> sList = new ArrayList<>();
        SameDayBean bean = new SameDayBean();
        bean.setRootLevelName("");
        bean.setRoadbedNum("路基");
        bean.setBridgeNum("桥梁");
        bean.setCulvertNum("涵洞");
        bean.setWaterNum("排水");
        bean.setProtectNum("防护");
        bean.setSubtotal("合计");
        bean.setOperation("操作");
        sList.add(bean);
        if (beanList != null && beanList.size() > 1) {
            for (int i = 0; i < beanList.size(); i++) {
                beanList.get(i).setOperation("详情");
                sList.add(beanList.get(i));
            }
        }
        // 动态添加行
        int size = sList.size();
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        int width = (screenWidth - DensityUtil.dip2px(32) - DensityUtil.dip2px(50) - DensityUtil.dip2px(40)) / 6 - DensityUtil.dip2px(1);
        int height = DensityUtil.dip2px(40);
        for (int i = 0; i < size; i++) {
            final SameDayBean sameDayBean = sList.get(i);
            TableRow tableRow = new TableRow(this);
            // 分部名称
            TextView divisionName = new TextView(this);
            divisionName.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            divisionName.setText(TextUtils.isEmpty(sameDayBean.getRootLevelName()) ? "" : sameDayBean.getRootLevelName().substring(3));
            divisionName.setTextSize(12);
            divisionName.setGravity(Gravity.CENTER);
            divisionName.setTextColor(ContextCompat.getColor(this, R.color.white));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(DensityUtil.dip2px(49), height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(divisionName, lp);

            // 路基完成数
            TextView ljFinishNum = new TextView(this);
            ljFinishNum.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            ljFinishNum.setText(sameDayBean.getRoadbedNum());
            ljFinishNum.setTextSize(12);
            ljFinishNum.setGravity(Gravity.CENTER);
            ljFinishNum.setTextColor(ContextCompat.getColor(this, R.color.white));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(ljFinishNum, lp);

            // 桥梁完成数
            TextView qlFinishNum = new TextView(this);
            qlFinishNum.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            qlFinishNum.setText(sameDayBean.getBridgeNum());
            qlFinishNum.setTextSize(12);
            qlFinishNum.setGravity(Gravity.CENTER);
            qlFinishNum.setTextColor(ContextCompat.getColor(this, R.color.white));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(qlFinishNum, lp);

            // 涵洞完成数
            TextView hdFinishNum = new TextView(this);
            hdFinishNum.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            hdFinishNum.setText(sameDayBean.getCulvertNum());
            hdFinishNum.setTextSize(12);
            hdFinishNum.setGravity(Gravity.CENTER);
            hdFinishNum.setTextColor(ContextCompat.getColor(this, R.color.white));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(hdFinishNum, lp);

            // 排水完成数
            TextView psFinishNum = new TextView(this);
            psFinishNum.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            psFinishNum.setText(sameDayBean.getWaterNum());
            psFinishNum.setTextSize(12);
            psFinishNum.setGravity(Gravity.CENTER);
            psFinishNum.setTextColor(ContextCompat.getColor(this, R.color.white));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(psFinishNum, lp);

            // 防护完成数
            TextView pfhFinishNum = new TextView(this);
            pfhFinishNum.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            pfhFinishNum.setText(sameDayBean.getProtectNum());
            pfhFinishNum.setTextSize(12);
            pfhFinishNum.setGravity(Gravity.CENTER);
            pfhFinishNum.setTextColor(ContextCompat.getColor(this, R.color.white));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(pfhFinishNum, lp);

            // 合计
            TextView subTotal = new TextView(this);
            subTotal.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            subTotal.setText(sameDayBean.getSubtotal());
            subTotal.setTextSize(12);
            subTotal.setGravity(Gravity.CENTER);
            subTotal.setTextColor(ContextCompat.getColor(this, R.color.white));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(subTotal, lp);

            // 操作
            TextView operation = new TextView(this);
            operation.setBackgroundColor(ContextCompat.getColor(this, R.color.report_bg));
            operation.setText(sameDayBean.getOperation());
            operation.setTextSize(12);
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(40), height);
            if (i == 0) {
                operation.setTextColor(ContextCompat.getColor(this, R.color.white));
                lp.setMargins(0, 0, 0, 0);
            } else {
                operation.setTextColor(ContextCompat.getColor(this, R.color.main_check_bg));
                operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (JudgeNetworkIsAvailable.isNetworkAvailable((Activity) mContext)) {
                            Intent intent = new Intent(mContext, SameDayProjectDetailsActivity.class);
                            intent.putExtra("rootLevelId", sameDayBean.getRootLevelId());
                            intent.putExtra("rootLevelName", sameDayBean.getRootLevelName());
                            intent.putExtra("beganDate", DateUtil.parse(txtBeganDate.getText().toString()).getTime());
                            intent.putExtra("endDate", DateUtil.parse(txtEndDate.getText().toString()).getTime());
                            intent.putExtra("rootLevelName", sameDayBean.getRootLevelName());
                            startActivity(intent);
                        } else {
                            ToastUtil.showShort(mContext, getString(R.string.not_network));
                        }
                    }
                });
                lp.setMargins(0, DensityUtil.dip2px(1), 0, 0);
            }
            operation.setGravity(Gravity.CENTER);
            tableRow.addView(operation, lp);

            tbSameDay.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 设置工序总数
     *
     * @param beanList
     */
    private void initHistoryData(List<SameDayBean> beanList) {
        List<Float> list1 = new ArrayList<>();
        List<Float> list3 = new ArrayList<>();
        int size = beanList.size();
        for (int i = 0; i < size; i++) {
            list1.add(Float.valueOf(beanList.get(i).getFinishedNum()));
            list3.add(Float.valueOf(beanList.get(i).getTotalNum()));
        }

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup3 = new ArrayList<>();

        for (int i = 0; i < list1.size(); i++) {
            entriesGroup1.add(new BarEntry(i, list1.get(i)));
            entriesGroup3.add(new BarEntry(i, list3.get(i)));
        }

        BarDataSet set1 = new BarDataSet(entriesGroup1, "已完成工序数");
        set1.setColors(new int[]{Color.argb(255, 0, 176, 80)}); // 设置每条柱子的颜色
        set1.setValueTextColor(ContextCompat.getColor(mContext, R.color.white));
        set1.setHighlightEnabled(false);
        set1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String s = String.valueOf(value).substring(0, String.valueOf(value).indexOf("."));
                return s;
            }
        });

        BarDataSet set3 = new BarDataSet(entriesGroup3, "总工序数");
        set3.setColors(new int[]{Color.argb(255, 247, 142, 98)}); // 设置每条柱子的颜色
        set3.setHighlightEnabled(false);
        set3.setValueTextColor(ContextCompat.getColor(mContext, R.color.white));
        set3.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String s = String.valueOf(value).substring(0, String.valueOf(value).indexOf("."));
                return s;
            }
        });

        float groupSpace = 0.25f;
        float barSpace = 0.02f;
        float barWidth = 0.35f;

        BarData data = new BarData(set3, set1);
        data.setBarWidth(barWidth); // set the width of each bar
        barChart.setData(data);
        barChart.groupBars(-0.5f, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.invalidate(); // refresh
        //barChart.setTouchEnabled(false); // 设置是否可以触摸
        //barChart.setDragEnabled(true);// 是否可以拖拽
        //barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.getAxisRight().setEnabled(false); //右侧不显示Y轴
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //设置X轴的位置
        barChart.getLegend().setForm(Legend.LegendForm.SQUARE); //这是左边显示小图标的形状
        barChart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.white));
        barChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        barChart.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String s = "";
                switch (String.valueOf(value)) {
                    case "0.0":
                        s = "一分部";
                        break;
                    case "1.0":
                        s = "二分部";
                        break;
                    case "2.0":
                        s = "三分部";
                        break;
                    case "3.0":
                        s = "四分部";
                        break;
                    case "4.0":
                        s = "五分部";
                        break;
                    case "5.0":
                        s = "六分部";
                        break;
                }
                return s;
            }
        });

        barChart.setPinchZoom(true);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
        barChart.getAxisLeft().setEnabled(true);//是否显示最右侧竖线
        barChart.getAxisLeft().setDrawAxisLine(true);
        barChart.getXAxis().setDrawAxisLine(true);
        barChart.getAxisLeft().setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙
        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);//设置描述
        barChart.setBackgroundColor(ContextCompat.getColor(mContext, R.color.report_bg));
        barChart.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.white));
        barChart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.white)); // X轴文字颜色
        //设置动画
        barChart.animateXY(0, 2000);
    }

    /**
     * 日期选择
     *
     * @param point
     */
    public void onYearMonthDayPicker(final int point) {
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(2100, 1, 31);
        picker.setRangeStart(2000, 1, 31);
        String date;
        if (point == 0) {
            date = txtBeganDate.getText().toString();
        } else {
            date = txtEndDate.getText().toString();
        }
        Date time = DateUtil.parse(date);
        picker.setSelectedItem(DateUtil.year(time), DateUtil.month(time) + 1, DateUtil.dayOfMonth(time));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                if (point == 0) {
                    txtBeganDate.setText(year + "-" + month + "-" + day);
                } else {
                    txtEndDate.setText(year + "-" + month + "-" + day);
                }
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
     * 获取数据
     */
    private void getSameDayData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("startDate", DateUtil.parse(txtBeganDate.getText().toString()).getTime());
            obj.put("endDate", DateUtil.parse(txtEndDate.getText().toString()).getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PROCESS_REPORT_TODAY)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread(mContext.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            Gson gson = new Gson();
                            final SameDayModel model = gson.fromJson(jsonData, SameDayModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSameDay(model.getData().getSearchTotalList());
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            LoadingUtils.hideLoading();
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(mContext.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    LoadingUtils.hideLoading();
                    runChildrenThread(mContext.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 子线程运行
     */
    private void runChildrenThread(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(mContext, msg);
            }
        });
    }

    /**
     * Token过期
     *
     * @param code
     * @param msg
     */
    private void tokenErr(final String code, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtils.hideLoading();
                switch (code) {
                    case "3003":
                    case "3004":
                        // Token异常重新登录
                        ToastUtil.showLong(mContext, "Token过期请重新登录！");
                        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                        ScreenManagerUtil.popAllActivityExceptOne();
                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        break;
                    default:
                        ToastUtil.showLong(mContext, msg);
                        break;
                }
            }
        });
    }

    @Event({R.id.imgBtnLeft, R.id.txtBeganDate, R.id.txtEndDate, R.id.btnSearch})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.txtBeganDate:
                onYearMonthDayPicker(0);
                break;
            case R.id.txtEndDate:
                onYearMonthDayPicker(1);
                break;
            case R.id.btnSearch:
                if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                    String begin = txtBeganDate.getText().toString();
                    String end = txtEndDate.getText().toString();
                    if (begin.equals(end)) {
                        ToastUtil.showShort(mContext, "开始时间和结束时间不能相同！");
                    } else if (DateUtils.compare_date(begin, end) == 1) {
                        ToastUtil.showShort(mContext, "开始时间不能大于结束时间！");
                    } else {
                        getSameDayData();
                    }
                } else {
                    ToastUtil.showShort(mContext, getString(R.string.not_network));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
