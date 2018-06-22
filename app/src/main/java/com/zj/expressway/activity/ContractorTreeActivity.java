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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.zj.expressway.R;
import com.zj.expressway.adapter.TreeNodeAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.listener.ContractorListener;
import com.zj.expressway.model.ContractorModel;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SetListHeight;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
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
 *       Created by HaiJun on 2018/6/11 16:59
 *       工序树形图
 */
public class ContractorTreeActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.btnRight)
    private Button btnRight;
    @ViewInject(R.id.searchBar)
    private MaterialSearchBar searchBar;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.lvContractorList)
    private ListView lvContractorList;
    private Activity mContext;
    private List<Node> allCache;
    private List<Node> all;
    private TreeNodeAdapter ta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contractor_tree);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        imgBtnRight.setVisibility(View.VISIBLE);
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.search_btn));
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("确认");
        txtTitle.setText(R.string.app_title);

        lvContractorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TreeNodeAdapter) parent.getAdapter()).ExpandOrCollapse(position);
            }
        });

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
                if (StrUtil.isEmpty(text)) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
                } else if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    ToastUtil.showShort(mContext, "请连接您的网络！");
                } else {
                    searchProcess(String.valueOf(text));
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {}
        });

        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                searchBar.setVisibility(View.GONE);
                if (StrUtil.isEmpty(String.valueOf(v.getTag()))) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
                } else if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    ToastUtil.showShort(mContext, "请连接您的网络！");
                } else {
                    searchProcess(String.valueOf(v.getTag()));
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                DataSupport.deleteAll(SearchRecordBean.class, "searchTitle=? and searchType=2", String.valueOf(searchBar.getLastSuggestions().get(position)));
                searchBar.getLastSuggestions().remove(position);
                searchBar.updateLastSuggestions(searchBar.getLastSuggestions());
            }
        });

        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            List<ContractorBean> listBean = DataSupport.where("parentId = ? and levelType = ?", "0", getIntent().getStringExtra("type")).find(ContractorBean.class);
            setContractorNode(listBean);
        }
    }

    /**
     * 设置搜索历史列表
     */
    private void initSearchRecord() {
        List<SearchRecordBean> searchList = DataSupport.where("searchType=2").find(SearchRecordBean.class);
        if (searchList != null) {
            List<String> stringList = new ArrayList<>();
            for (SearchRecordBean bean : searchList) {
                stringList.add(bean.getSearchTitle());
            }
            searchBar.setLastSuggestions(stringList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ta != null) {
            ta.notifyDataSetChanged();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("parentId", "0");
        String url;
        if (getIntent().getStringExtra("type").equals("1")) {
            url = ConstantsUtil.NEW_CONTRACTOR_LIST;
        } else if (getIntent().getStringExtra("type").equals("2")) {
            url = ConstantsUtil.getZxHwZlProjectLevelList;
        } else {
            url = ConstantsUtil.getZxHwAqProjectLevelList;
        }

        Request request = ChildThreadUtil.getRequest(mContext, url, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
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
                            // 将数据存储到LitePal数据库（根据LevelId添加或更新）
                            List<ContractorBean> listBeen = model.getData();
                            for (ContractorBean bean : listBeen) {
                                bean.setLevelType(getIntent().getStringExtra("type"));
                                bean.saveOrUpdate("levelId=?", bean.getLevelId());
                            }
                            // 设置节点
                            setContractorNode(model.getData());
                            LoadingUtils.hideLoading();
                        }
                    });
                } else {
                    ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                }
            } else {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
            }
        }
    };

    /**
     * 设置节点,可以通过循环或递归方式添加节点
     * @param contractorBean
     */
    private void setContractorNode(List<ContractorBean> contractorBean) {
        // 添加节点
        if (contractorBean != null && contractorBean.size() > 0) {
            int listSize = contractorBean.size();
            // 创建根节点
            Node root = new Node();
            root.setFolderFlag("1");

            for (int i = 0; i < listSize; i++) {
                getNode(contractorBean.get(i), root);
            }

            ta = new TreeNodeAdapter(this, root, listener);
                /* 设置展开和折叠时图标 */
            ta.setExpandedCollapsedIcon(R.drawable.open, R.drawable.fold);
				/* 设置默认展开级别 */
            ta.setExpandLevel(1);
            lvContractorList.setAdapter(ta);
            SetListHeight.setListViewHeight(lvContractorList);
        }
    }

    /**
     * 子节点
     * @param contractorListBean
     * @param root
     * @return
     */
    private Node getNode(ContractorBean contractorListBean, Node root) {
        String levelId = contractorListBean.getLevelId();
        String levelName = contractorListBean.getLevelName();
        // 创建子节点
        Node n = new Node();
        n.setParent(root);
        n.setLevelId(levelId);
        n.setLevelName(levelName);
        n.setParentId(contractorListBean.getParentId());
        n.setFolderFlag(contractorListBean.getCanExpand());
        n.setExpanded(false);
        n.setLoading(false);
        n.setChoice(false);
        //n.setCanClick(contractorListBean.getCanExpand().equals("1"));
        //n.setIsFinish(contractorListBean.getIsFinish());
        root.add(n);
        return n;
    }

    /**
     * 是否已加载监听
     */
    private ContractorListener listener = new ContractorListener() {
        @Override
        public void returnData(List<Node> allCaches, List<Node> allNode, int point, String levelId) {
            allCache = allCaches;
            all = allNode;
            // 没有网络并且没有加载过
            if (JudgeNetworkIsAvailable.isNetworkAvailable(ContractorTreeActivity.this)) {
                loadProcedureByNodeId(point, levelId);
            } else {
                List<ContractorBean> listBean = DataSupport.where("parentId = ? and levelType = ?", levelId, getIntent().getStringExtra("type")).find(ContractorBean.class);
                setNodeInChildren(listBean, point);
            }
        }
    };

    /**
     * 加载层级下的节点
     * @param position
     * @param parentId
     */
    private void loadProcedureByNodeId(final int position, final String parentId) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("parentId", parentId);
        obj.put("levelType", SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.NEW_CONTRACTOR_LIST, obj.toString());
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
                                // 将数据存储到LitePal数据库（根据nodeId添加或更新）
                                List<ContractorBean> listBeen = model.getData();
                                for (ContractorBean bean : listBeen) {
                                    bean.setLevelType(getIntent().getStringExtra("type"));
                                    bean.saveOrUpdate("levelId=?", bean.getLevelId());
                                }
                                SpUtil.put(mContext, ConstantsUtil.LEVEL_ID, SpUtil.get(mContext, ConstantsUtil.LEVEL_ID, "") + "" + parentId + ",");
                                // 将数据添加到Node的子节点中
                                setNodeInChildren(model.getData(), position);
                                LoadingUtils.hideLoading();
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
     * 将数据插入到Node中
     *
     * @param data
     * @param position
     */
    private void setNodeInChildren(List<ContractorBean> data, final int position) {
        List<Node> nodes = new ArrayList<>();
        for (ContractorBean contractor : data) {
            String levelId = contractor.getLevelId();
            String levelName = contractor.getLevelName();
            // 创建子节点
            Node n = new Node();
            n.setParent(all.get(position));
            n.setLevelId(levelId);
            n.setLevelName(levelName);
            n.setParentId(contractor.getParentId());
            n.setFolderFlag(contractor.getCanExpand());
            n.setExpanded(false);
            n.setLoading(false);
            n.setChoice(false);
            nodes.add(n);
        }

        // 添加子节点到指定根节点下面
        all.addAll(position + 1, nodes);
        // 需要放到此节点下
        Node node = all.get(position);
        int point = allCache.indexOf(node);
        allCache.addAll(point + 1, nodes);

        all.get(position).setChildren(nodes);
        all.get(position).setLoading(true);
        allCache.get(position).setChildren(nodes);
        allCache.get(position).setLoading(true);

        ta.notifyDataSetChanged();
    }

    /**
     * 搜索
     * @param searchTitle
     */
    private void searchProcess(String searchTitle) {
        Intent intent = new Intent(mContext, SearchProcedureActivity.class);
        intent.putExtra("searchType", getIntent().getStringExtra("type"));
        intent.putExtra("searchTitle", searchTitle);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {
                Intent intent = new Intent();
                intent.putExtra("procedureName", data.getStringExtra("procedureName"));
                intent.putExtra("levelId", data.getStringExtra("levelId"));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Event({R.id.imgBtnLeft, R.id.btnRight, R.id.imgBtnRight})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnRight:
                if (ta != null) {
                    ta.selectProcess((Integer) SpUtil.get(mContext, "selectProcess", -1));
                } else {
                    ToastUtil.showShort(mContext, "数据有误！");
                }
                break;
            case R.id.imgBtnRight:
                searchBar.setVisibility(View.VISIBLE);
                searchBar.enableSearch();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ScreenManagerUtil.popActivity(this);
        SpUtil.remove(mContext, "selectProcess");
        List<String> stringList = searchBar.getLastSuggestions();
        if (stringList != null) {
            DataSupport.deleteAll(SearchRecordBean.class, "searchType=2");
            for (String str : stringList) {
                SearchRecordBean bean = new SearchRecordBean();
                bean.setSearchTitle(str);
                bean.setSearchType("2");
                bean.save();
            }
        }
        super.onDestroy();
    }
}
