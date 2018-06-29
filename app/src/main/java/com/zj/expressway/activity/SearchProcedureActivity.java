package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.SearchAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.model.ContractorModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.ToastUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 16:34
 * 搜索工序
 */
public class SearchProcedureActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.btnRight)
    private Button btnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.txtNoSearch)
    private TextView txtNoSearch;
    @ViewInject(R.id.lvSearch)
    private ListView lvSearch;
    private SearchAdapter mAdapter;
    private List<ContractorBean> workingBeanList;
    private Activity mContext;
    private int selectPoint = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        mContext = this;
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("确定");
        txtTitle.setText(getString(R.string.app_name));

        getData();

        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (ContractorBean bean : workingBeanList) {
                    bean.setSelect(false);
                }
                selectPoint = position;
                workingBeanList.get(position).setSelect(true);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("parentNameAll", getIntent().getStringExtra("searchTitle"));
        String url;
        if (getIntent().getStringExtra("searchType").equals("1")) {
            url = ConstantsUtil.getZxHwGxProjectLevelList;
        } else if (getIntent().getStringExtra("searchType").equals("2")) {
            url = ConstantsUtil.getZxHwZlProjectLevelList;
        } else {
            url = ConstantsUtil.getZxHwAqProjectLevelList;
        }
        Request request = ChildThreadUtil.getRequest(mContext, url, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final ContractorModel model = gson.fromJson(jsonData, ContractorModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingUtils.hideLoading();
                                workingBeanList = model.getData();
                                initData();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (workingBeanList == null || workingBeanList.size() == 0) {
            txtNoSearch.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new SearchAdapter(mContext, workingBeanList);
            lvSearch.setAdapter(mAdapter);
        }
    }

    @Event({R.id.imgBtnLeft, R.id.btnRight})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnRight:
                if (selectPoint == -1) {
                    ToastUtil.showShort(mContext, "请选择！");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("procedureName", workingBeanList.get(selectPoint).getParentNameAll());
                    intent.putExtra("levelId", workingBeanList.get(selectPoint).getLevelId());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
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
