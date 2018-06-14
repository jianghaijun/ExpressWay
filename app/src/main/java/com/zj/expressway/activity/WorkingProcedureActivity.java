package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.model.WorkingListModel;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 *       Created by HaiJun on 2018/6/11 17:00
 *       工序列表主界面
 */
public class WorkingProcedureActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.searchBar)
    private MaterialSearchBar searchBar;
    @ViewInject(R.id.btnTakePicture)
    private Button btnTakePicture;
    @ViewInject(R.id.vTakePicture)
    private View vTakePicture;
    @ViewInject(R.id.btnToBeAudited)
    private Button btnToBeAudited;
    @ViewInject(R.id.vToBeAudited)
    private View vToBeAudited;
    @ViewInject(R.id.btnFinish)
    private Button btnFinish;
    @ViewInject(R.id.vFinish)
    private View vFinish;
    @ViewInject(R.id.vpWorkingProcedure)
    private ViewPager vpWorkingProcedure;
    // --------------------viewPage--------------------
    private View layTakePicture, layToBeAudited, layFinish;
    private WorkingProcedureListActivity takePictureActivity;
    private WorkingProcedureListActivity toBeAuditedActivity;
    private WorkingProcedureListActivity finishActivity;
    private ArrayList<View> views;
    // --------------------viewPage--------------------
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_working_procedure);
        x.view().inject(this);
        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        txtTitle.setText(R.string.app_name);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        imgBtnRight.setVisibility(View.VISIBLE);
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.search_btn));

        initViewPageData();
        initSearchRecord();

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    searchBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                ToastUtil.showShort(mContext, text + "---text---");
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                ToastUtil.showShort(mContext, buttonCode + "---buttonCode---");
            }
        });

        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                searchBar.setVisibility(View.GONE);
                ToastUtil.showShort(mContext, position + "---click---" + v.getTag());
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                DataSupport.deleteAll(SearchRecordBean.class, "searchTitle=?", String.valueOf(searchBar.getLastSuggestions().get(position)));
                searchBar.getLastSuggestions().remove(position);
                searchBar.updateLastSuggestions(searchBar.getLastSuggestions());
            }
        });
        getTodoNum();
        initRecyclerViewData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConstantsUtil.isLoading) {
            getTodoNum();
            initRecyclerViewData();
            ConstantsUtil.isLoading = false;
        }
    }

    /**
     * 设置搜索历史列表
     */
    private void initSearchRecord() {
        List<SearchRecordBean> searchList = DataSupport.findAll(SearchRecordBean.class);
        if (searchList != null) {
            List<String> stringList = new ArrayList<>();
            for (SearchRecordBean bean : searchList) {
                stringList.add(bean.getSearchTitle());
            }
            searchBar.setLastSuggestions(stringList);
        }
    }

    /**
     * 添加选项卡数据
     */
    private void initTabData(String str, String toDoNum, String hasToDoNum) {
        btnTakePicture.setText("待拍照（" + str + "）");
        btnToBeAudited.setText("待办（" + toDoNum + "）");
        btnFinish.setText("已办（" + hasToDoNum + "）");
    }

    /**
     * 初始化viewPage数据
     */
    private void initViewPageData() {
        // 将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layTakePicture = viewLI.inflate(R.layout.layout_msg, null);
        layToBeAudited = viewLI.inflate(R.layout.layout_msg, null);
        layFinish = viewLI.inflate(R.layout.layout_msg, null);
        // 待拍照
        takePictureActivity = new WorkingProcedureListActivity(mContext, layTakePicture);
        // 待审核
        toBeAuditedActivity = new WorkingProcedureListActivity(mContext, layToBeAudited);
        // 已完成
        finishActivity = new WorkingProcedureListActivity(mContext, layFinish);

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layTakePicture);
        views.add(layToBeAudited);
        views.add(layFinish);

        vpWorkingProcedure.setOnPageChangeListener(new MyOnPageChangeListener());
        vpWorkingProcedure.setAdapter(mPagerAdapter);
        vpWorkingProcedure.setCurrentItem(0);
    }

    /**
     * 初始化列表数据
     */
    private void initRecyclerViewData() {
        //takePictureActivity.setDate(1);
        toBeAuditedActivity.setDate(2);
        finishActivity.setDate(3);
    }

    /**
     * 获取数量
     */
    private void getTodoNum() {
        //LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("flowId", ConstantsUtil.flowId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.FLOW_COUNT)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //LoadingUtils.hideLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, getString(R.string.server_exception));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        if (resultFlag) {
                            JSONObject jsonObject = new JSONObject(obj.getString("data"));
                            final String toDoNum = jsonObject.getString("todoCount");
                            final String hasTodoNum = jsonObject.getString("hasTodoCount");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initTabData("0", toDoNum, hasTodoNum);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(mContext, getString(R.string.json_error));
                            }
                        });
                        e.printStackTrace();
                    }
                    //LoadingUtils.hideLoading();
                } else {
                    //LoadingUtils.hideLoading();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(mContext, getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }


    /**
     * 填充ViewPager的数据适配器
     */
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    };

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            setStates(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 设置背景
     *
     * @param option
     */
    private void setStates(int option) {
        // 待拍照
        btnTakePicture.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vTakePicture.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        // 待审核
        btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        // 已完成
        btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));

        switch (option) {
            case 0:
                btnTakePicture.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vTakePicture.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
            case 1:
                btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
            case 2:
                btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10001) {
                ToastUtil.showShort(mContext, data.getStringExtra("procedureName"));
            } else if (requestCode == 10002) {
                ToastUtil.showShort(mContext, data.getStringExtra("procedureName"));
            }
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Event({R.id.imgBtnLeft, R.id.imgBtnRight, R.id.btnTakePicture, R.id.btnToBeAudited, R.id.btnFinish, R.id.btnCommonlyUsed, R.id.btnAll})
    private void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.imgBtnRight:
                searchBar.setVisibility(View.VISIBLE);
                searchBar.enableSearch();
                break;
            case R.id.btnTakePicture:
                vpWorkingProcedure.setCurrentItem(0);
                break;
            case R.id.btnToBeAudited:
                vpWorkingProcedure.setCurrentItem(1);
                break;
            case R.id.btnFinish:
                vpWorkingProcedure.setCurrentItem(2);
                break;
            case R.id.btnCommonlyUsed:
                // 常用工序
                intent = new Intent(this, CommonWorkingProcedureActivity.class);
                startActivityForResult(intent, 10001);
                break;
            case R.id.btnAll:
                // 全部工序
                intent = new Intent(this, ContractorTreeActivity.class);
                startActivityForResult(intent, 10002);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
        List<String> stringList = searchBar.getLastSuggestions();
        if (stringList != null) {
            DataSupport.deleteAll(SearchRecordBean.class);
            for (String str : stringList) {
                SearchRecordBean bean = new SearchRecordBean();
                bean.setSearchTitle(str);
                bean.save();
            }
        }
    }
}
