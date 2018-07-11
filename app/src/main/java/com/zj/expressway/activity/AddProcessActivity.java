package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.zj.expressway.R;
import com.zj.expressway.adapter.LocalProcessListAdapter;
import com.zj.expressway.base.BaseNoImmersionBarActivity;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.bean.ProcessDictionaryBean;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.bean.SyncLinkageMenuBean;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;


/**
 * Created by HaiJun on 2018/6/11 16:32
 */
public class AddProcessActivity extends BaseNoImmersionBarActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnCondition1)
    private Button btnCondition1;
    @ViewInject(R.id.btnCondition2)
    private Button btnCondition2;
    @ViewInject(R.id.btnCondition3)
    private Button btnCondition3;
    @ViewInject(R.id.btnQueryAdd)
    private Button btnQueryAdd;
    @ViewInject(R.id.refreshLayout)
    private RefreshLayout refreshLayout;
    @ViewInject(R.id.rvProcessList)
    private RecyclerView rvProcessList;
    @ViewInject(R.id.llSearchData)
    private LinearLayout llSearchData;
    @ViewInject(R.id.txtMsg)
    private TextView txtMsg;
    @ViewInject(R.id.txtClear)
    private TextView txtClear;
    @ViewInject(R.id.searchBar)
    private MaterialSearchBar searchBar;
    @ViewInject(R.id.edtProcessNum)
    private EditText edtProcessNum;

    // 工序列表数据
    private List<ProcessDictionaryBean> processList = new ArrayList<>();
    private LocalProcessListAdapter processAdapter;

    private int processSum; // 工序数量
    private int pagePosition = 1, loadType = -1;
    private boolean isSearch = false;
    private String firstLevelId = "", secondLevelId = "", thirdLevelId = "", searchText = "", levelId;
    private Activity mContext;
    private ArrayList<String> oldSelect = new ArrayList<>();
    private ArrayList<String> oldSelectName = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_process);
        x.view().inject(this);
        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        levelId = getIntent().getStringExtra("levelId");
        if (StrUtil.equals(levelId, "update")) {
            txtTitle.setText("添加层级");
        } else {
            btnQueryAdd.setText("确认修改");
            txtTitle.setText("修改层级");
        }
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnRight.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_btn));
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.search_btn));

        initSearchRecord();
        initBtnTitle();
        initListData();
    }

    /**
     * 设置btn标题
     */
    private void initBtnTitle() {
        btnCondition1.setText("全部");
        btnCondition2.setText("全部");
        btnCondition3.setText("全部");
    }

    /**
     * 初始化列表数据
     */
    private void initListData() {
        // 设置主题颜色
        refreshLayout.setPrimaryColorsId(R.color.main_bg, android.R.color.white);
        refreshLayout.setFooterTriggerRate(1);
        refreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        // 通过多功能监听接口实现 在第一次加载完成之后 自动刷新
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadType = 1;
                if (processList.size() < processSum) {
                    pagePosition++;
                    getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
                } else {
                    ToastUtil.showShort(mContext, "没有更多数据了！");
                    refreshLayout.finishLoadMore(1000);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadType = 2;
                pagePosition = 1;
                processList.clear();
                getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
            }
        });

        getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
    }

    /**
     * 初始化搜索历史记录
     */
    private void initSearchRecord() {
        List<SearchRecordBean> searchList = DataSupport.where("searchType=3").find(SearchRecordBean.class);
        if (searchList != null) {
            List<String> stringList = new ArrayList<>();
            for (SearchRecordBean bean : searchList) {
                stringList.add(bean.getSearchTitle());
            }
            searchBar.setLastSuggestions(stringList);
        }

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    searchBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (StrUtil.isEmpty(text)) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
                } else if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    ToastUtil.showShort(mContext, "请连接您的网络！");
                } else {
                    searchBar.setVisibility(View.GONE);
                    pagePosition = 1;
                    processList.clear();
                    searchText = String.valueOf(text);
                    getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (StrUtil.isEmpty(String.valueOf(v.getTag()))) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
                } else if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    ToastUtil.showShort(mContext, "请连接您的网络！");
                } else {
                    searchBar.setVisibility(View.GONE);
                    pagePosition = 1;
                    processList.clear();
                    searchText = String.valueOf(v.getTag());
                    getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                DataSupport.deleteAll(SearchRecordBean.class, "searchTitle=? and searchType=3", String.valueOf(searchBar.getLastSuggestions().get(position)));
                searchBar.getLastSuggestions().remove(position);
                searchBar.updateLastSuggestions(searchBar.getLastSuggestions());
            }
        });
    }

    /**
     * 获取本地数据
     *
     * @param firstLevelId
     * @param secondLevelId
     * @param thirdLevelId
     * @param searchText
     */
    private void getLocalData(String firstLevelId, String secondLevelId, String thirdLevelId, String searchText) {
        StringBuffer sb = new StringBuffer();
        if (StrUtil.isNotEmpty(firstLevelId)) {
            sb.append("firstLevelId ='" + firstLevelId + "' and ");
        }
        if (StrUtil.isNotEmpty(secondLevelId)) {
            sb.append("secondLevelId ='" + secondLevelId + "' and ");
        }
        if (StrUtil.isNotEmpty(thirdLevelId)) {
            sb.append("thirdLevelId ='" + thirdLevelId + "' and ");
        }
        if (StrUtil.isNotEmpty(searchText)) {
            sb.append("dictName ='" + searchText + "' and ");
        }
        String start = String.valueOf((pagePosition - 1) * 10);
        String end = String.valueOf(pagePosition * 10);

        List<ProcessDictionaryBean> list = DataSupport.where(sb.toString() + "type = 1 order by createTime desc limit ?, ?", start, end).find(ProcessDictionaryBean.class);
        List<ContractorBean> contractorBeen = DataSupport.where("parentId=?", levelId).find(ContractorBean.class);

        processSum = list == null ? 0 : list.size();
        if (list != null) {
            for (ProcessDictionaryBean bean : list) {
                for (ContractorBean bean1 : contractorBeen) {
                    if (StrUtil.equals(bean.getDictName(), bean1.getLevelName())) {
                        bean.setSelect(true);
                        oldSelect.add(bean.getDictId());
                        oldSelectName.add(bean.getDictName());
                    }
                }
                bean.setOperation("拍照内容");
            }
        }

        ProcessDictionaryBean bean = new ProcessDictionaryBean();
        bean.setDictName("部位名称");
        bean.setOperation("操作");
        processList.add(bean);
        processList.addAll(list);
        stopLoad();

        // 显示无数据
        if (isSearch && pagePosition == 1 && processSum == 0) {
            rvProcessList.setVisibility(View.GONE);
            llSearchData.setVisibility(View.VISIBLE);
            txtMsg.setText("未搜索到任何数据");
            txtClear.setText("，清空搜索条件");
        } else if (pagePosition == 1 && processSum == 0) {
            rvProcessList.setVisibility(View.GONE);
            llSearchData.setVisibility(View.VISIBLE);
            txtMsg.setText("暂无数据");
            txtClear.setText("");
        } else {
            rvProcessList.setVisibility(View.VISIBLE);
            llSearchData.setVisibility(View.GONE);
        }

        if (!StrUtil.equals(levelId, "update")) {
            edtProcessNum.setText(getIntent().getStringExtra("levelName"));
        }

        // 数据处理
        processAdapter = new LocalProcessListAdapter(mContext, processList);
        rvProcessList.setAdapter(processAdapter);
        rvProcessList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 停止加载
     */
    private void stopLoad() {
        if (loadType == 1) {
            refreshLayout.finishLoadMore(1000);
        } else if (loadType == 2) {
            refreshLayout.finishRefresh(1000);
        }
    }

    /**
     * 展示选择框
     */
    private void showSelectDialog(List<SyncLinkageMenuBean> menuBeanList, final int dialogType) {
        int size = menuBeanList == null ? 0 : menuBeanList.size();
        final String[] strList = new String[size];
        final String[] idList = new String[size];
        for (int i = 0; i < size; i++) {
            switch (dialogType) {
                case 1:
                    strList[i] = menuBeanList.get(i).getFirstLevelName();
                    idList[i] = menuBeanList.get(i).getFirstLevelId();
                    break;
                case 2:
                    strList[i] = menuBeanList.get(i).getSecondLevelName();
                    idList[i] = menuBeanList.get(i).getSecondLevelId();
                    break;
                case 3:
                    strList[i] = menuBeanList.get(i).getThirdLevelName();
                    idList[i] = menuBeanList.get(i).getThirdLevelId();
                    break;
            }
        }
        OptionPicker picker = new OptionPicker(this, strList);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setShadowColor(ContextCompat.getColor(mContext, R.color.main_bg), 80);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(true);
        picker.setTextSize(18);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                switch (dialogType) {
                    case 1:
                        firstLevelId = idList[index];
                        btnCondition1.setText(strList[index]);
                        secondLevelId = "";
                        btnCondition2.setText("全部");
                        thirdLevelId = "";
                        btnCondition3.setText("全部");
                        searchText = "";
                        break;
                    case 2:
                        secondLevelId = idList[index];
                        btnCondition2.setText(strList[index]);
                        btnCondition3.setText("全部");
                        searchText = "";
                        searchText = "";
                        break;
                    case 3:
                        thirdLevelId = idList[index];
                        btnCondition3.setText(strList[index]);
                        searchText = "";
                        break;
                }
                processList.clear();
                pagePosition = 1;
                getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
            }
        });
        picker.show();
    }

    @Event({R.id.imgBtnLeft, R.id.imgBtnRight, R.id.btnCondition1, R.id.btnCondition2, R.id.btnCondition3, R.id.txtClear, R.id.btnQueryAdd})
    private void onClick(View v) {
        SyncLinkageMenuBean syncLinkageMenuBean = new SyncLinkageMenuBean();
        syncLinkageMenuBean.setFirstLevelName("全部");
        syncLinkageMenuBean.setFirstLevelId("");
        syncLinkageMenuBean.setSecondLevelName("全部");
        syncLinkageMenuBean.setSecondLevelId("");
        syncLinkageMenuBean.setThirdLevelName("全部");
        syncLinkageMenuBean.setThirdLevelId("");
        List<SyncLinkageMenuBean> beanList;
        switch (v.getId()) {
            // 关闭
            case R.id.imgBtnLeft:
                this.finish();
                break;
            // 搜索
            case R.id.imgBtnRight:
                searchBar.setVisibility(View.VISIBLE);
                searchBar.enableSearch();
                break;
            // 分类1
            case R.id.btnCondition1:
                isSearch = true;
                // 查询一级分类数据
                beanList = DataSupport.where("type=1").find(SyncLinkageMenuBean.class);
                if (beanList == null) {
                    beanList = new ArrayList<>();
                }
                beanList.add(0, syncLinkageMenuBean);
                showSelectDialog(beanList, 1);
                break;
            // 分类2
            case R.id.btnCondition2:
                if (StrUtil.isEmpty(firstLevelId)) {
                    ToastUtil.showShort(mContext, "请先选择一级分类！");
                    return;
                }
                isSearch = true;
                // 查询一级分类数据
                beanList = DataSupport.where("type=2").find(SyncLinkageMenuBean.class);
                if (beanList == null) {
                    beanList = new ArrayList<>();
                }
                beanList.add(0, syncLinkageMenuBean);
                showSelectDialog(beanList, 2);
                break;
            // 分类3
            case R.id.btnCondition3:
                if (StrUtil.isEmpty(firstLevelId)) {
                    ToastUtil.showShort(mContext, "请先选择一级分类！");
                    return;
                } else if (StrUtil.isEmpty(secondLevelId)) {
                    ToastUtil.showShort(mContext, "请先选择二级分类！");
                    return;
                }
                isSearch = true;
                // 查询一级分类数据
                beanList = DataSupport.where("type=3").find(SyncLinkageMenuBean.class);
                if (beanList == null) {
                    beanList = new ArrayList<>();
                }
                beanList.add(0, syncLinkageMenuBean);
                showSelectDialog(beanList, 3);
                break;
            // 清空搜索条件
            case R.id.txtClear:
                firstLevelId = "";
                btnCondition1.setText("全部");
                secondLevelId = "";
                btnCondition2.setText("全部");
                thirdLevelId = "";
                btnCondition3.setText("全部");
                searchText = "";
                processList.clear();
                getLocalData(firstLevelId, secondLevelId, thirdLevelId, searchText);
                break;
            // 确认添加
            case R.id.btnQueryAdd:
                ArrayList<String> strList = new ArrayList<>();
                for (ProcessDictionaryBean bean : processList) {
                    if (bean.isSelect()) {
                        strList.add(bean.getDictId());
                    }
                }
                if (StrUtil.isEmpty(edtProcessNum.getText().toString())) {
                    ToastUtil.showShort(mContext, "请输入桩号！");
                } else if (strList.size() == 0) {
                    ToastUtil.showShort(mContext, "至少选择一个层级！");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("position", getIntent().getIntExtra("position", 0)); // 点击节点位置
                    intent.putStringArrayListExtra("dictIdList", strList); // 层级Id
                    intent.putStringArrayListExtra("oldDictIdList", oldSelect); // 层级Id
                    intent.putStringArrayListExtra("oldDictNameList", oldSelectName); // 层级Id
                    intent.putExtra("pileNo", edtProcessNum.getText().toString()); // 桩号
                    intent.putExtra("levelId", levelId); // 层级Id
                    setResult(Activity.RESULT_OK, intent);
                    this.finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
        List<String> stringList = searchBar.getLastSuggestions();
        if (stringList != null) {
            DataSupport.deleteAll(SearchRecordBean.class, "searchType=3");
            for (String str : stringList) {
                SearchRecordBean bean = new SearchRecordBean();
                bean.setSearchTitle(str);
                bean.setSearchType("3");
                bean.save();
            }
        }
    }
}
