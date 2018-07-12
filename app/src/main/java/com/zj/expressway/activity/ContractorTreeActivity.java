package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.zj.expressway.R;
import com.zj.expressway.adapter.TreeNodeAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.bean.ProcessDictionaryBean;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.SlippingHintDialog;
import com.zj.expressway.listener.ContractorListener;
import com.zj.expressway.model.ContractorModel;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 16:59
 * 工序树形图
 */
public class ContractorTreeActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.searchBar)
    private MaterialSearchBar searchBar;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnNoData)
    private Button btnNoData;
    @ViewInject(R.id.lvContractorList)
    private RecyclerView lvContractorList;
    private Activity mContext;
    private List<Node> allCache = new ArrayList<>();
    private List<Node> all = new ArrayList<>();
    private TreeNodeAdapter ta;
    private String processType;

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
        txtTitle.setText(R.string.app_title);

        processType = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "1");

        boolean isPrompt = (boolean) SpUtil.get(this, ConstantsUtil.Long_press, false);
        if (processType.equals("1")) {
            if (!isPrompt) {
                new SlippingHintDialog(mContext, R.drawable.cloud, ConstantsUtil.Long_press, "长按层级进行添加！\n左滑添加、删除层级！").show();
            }
        } else {
            if (!isPrompt) {
                new SlippingHintDialog(mContext, R.drawable.cloud, ConstantsUtil.Long_press, "左滑添加、删除层级！").show();
            }
        }

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
                } else {
                    searchBar.setVisibility(View.GONE);
                    searchProcess(String.valueOf(text));
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
                } else {
                    searchBar.setVisibility(View.GONE);
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
            List<ContractorBean> listBean = DataSupport.where("parentId = ? and levelType = ?", "0", processType).find(ContractorBean.class);
            setContractorNode(listBean);
        }
    }

    /**
     * 添加节点
     *
     * @param node
     */
    private void addNode(Node node) {
        if (node.getParent() != null) {
            all.add(node);
            allCache.add(node);
        }
        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(node.getChildren().get(i));
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
        if (processType.equals("1")) {
            url = ConstantsUtil.getZxHwGxProjectLevelList;
        } else if (processType.equals("2")) {
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
                                bean.setLevelType(processType);
                                bean.setIsLocalAdd(2);
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
     *
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

            addNode(root);
            ta = new TreeNodeAdapter(this, all, allCache, listener);
            /* 设置展开和折叠时图标 */
            ta.setExpandedCollapsedIcon(R.drawable.open, R.drawable.fold);
            /* 设置默认展开级别 */
            ta.setExpandLevel(1);
            lvContractorList.setAdapter(ta);
            lvContractorList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

            /*allCache = ta.allCache;
            all = ta.all;*/
        } else {
            btnNoData.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 子节点
     *
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
        n.setParentIdAll(contractorListBean.getParentIdAll());
        n.setParentNameAll(contractorListBean.getParentNameAll());
        n.setFolderFlag(contractorListBean.getCanExpand());
        n.setExpanded(false);
        n.setLoading(false);
        n.setChoice(false);
        root.add(n);
        return n;
    }

    /**
     * 是否已加载监听
     */
    private ContractorListener listener = new ContractorListener() {
        @Override
        public void returnData(List<Node> allCaches, List<Node> allNode, int point, String levelId) {
            //allCache = allCaches;
            //all = allNode;
            // 没有网络并且没有加载过
            if (JudgeNetworkIsAvailable.isNetworkAvailable(ContractorTreeActivity.this)) {
                loadProcedureByNodeId(point, levelId);
            } else {
                List<ContractorBean> listBean = DataSupport.where("parentId = ? and levelType = ?", levelId, processType).find(ContractorBean.class);
                setNodeInChildren(listBean, point, true);
            }
        }
    };

    /**
     * 加载层级下的节点
     *
     * @param position
     * @param parentId
     */
    private void loadProcedureByNodeId(final int position, final String parentId) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("parentId", parentId);
        obj.put("levelType", processType);
        String url;
        if (processType.equals("1")) {
            url = ConstantsUtil.getZxHwGxProjectLevelList;
        } else if (processType.equals("2")) {
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
                                // 将数据存储到LitePal数据库（根据nodeId添加或更新）
                                List<ContractorBean> listBeen = model.getData();
                                for (ContractorBean bean : listBeen) {
                                    bean.setLevelType(processType);
                                    bean.setIsLocalAdd(2);
                                    bean.saveOrUpdate("levelId=?", bean.getLevelId());
                                }
                                // 查询本地保存的数据
                                List<ContractorBean> beanList = DataSupport.where("parentId=? and levelType=? and isLocalAdd=?", parentId, processType, "1").find(ContractorBean.class);
                                if (beanList != null) {
                                    model.getData().addAll(beanList);
                                }
                                // 将数据添加到Node的子节点中
                                setNodeInChildren(model.getData(), position, true);
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
    private void setNodeInChildren(List<ContractorBean> data, int position, boolean isLoading) {
        List<Node> nodes = new ArrayList<>();
        for (ContractorBean contractor : data) {
            String levelId = contractor.getLevelId();
            String levelName = contractor.getLevelName();
            // 创建子节点
            Node n = new Node();
            n.setParent(all.get(position));
            n.setLevelId(levelId);
            n.setLevelName(levelName);
            n.setLevelLevel(contractor.getLevelLevel());
            n.setParentId(contractor.getParentId());
            n.setParentIdAll(contractor.getParentIdAll());
            n.setParentNameAll(contractor.getParentNameAll());
            n.setFolderFlag(contractor.getCanExpand());
            n.setExpanded(false);
            n.setLoading(false);
            n.setChoice(false);
            n.setLocalAdd(StrUtil.equals(contractor.getIsLocalAdd()+"", "1"));
            nodes.add(n);
        }

        // 添加子节点到指定根节点下面
        all.addAll(position + 1, nodes);
        // 需要放到此节点下
        Node node = all.get(position);
        int point = allCache.indexOf(node);
        allCache.addAll(point + 1, nodes);

        all.get(position).setChildren(nodes);
        all.get(position).setLoading(isLoading);
        allCache.get(position).setChildren(nodes);
        allCache.get(position).setLoading(isLoading);

        if (data == null || data.size() == 0) {
            all.get(position).setFolderFlag("1");
            allCache.get(position).setFolderFlag("1");
        }

        ta.notifyDataSetChanged();
    }

    /**
     * 搜索
     *
     * @param searchTitle
     */
    private void searchProcess(String searchTitle) {
        Intent intent = new Intent(mContext, SearchProcedureActivity.class);
        intent.putExtra("searchType", processType);
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
            } else if (requestCode == 1005) {
                // 子级新增
                String pileNo = data.getStringExtra("pileNo");
                int position = data.getIntExtra("position", 0);
                ArrayList<String> dictId = data.getStringArrayListExtra("dictIdList");

                // 判断子级是否有重复层级名称
                List<Node> nodeList = all.get(position).getChildren();
                if (nodeList != null && nodeList.size() != 0) {
                    for (Node node : nodeList) {
                        if (StrUtil.equals(node.getLevelName(), pileNo)) {
                            ToastUtil.showShort(mContext, "不能添加相同层级名称的数据！");
                            return;
                        }
                    }
                }

                List<ContractorBean> beanList = new ArrayList<>();
                String parentLevelId = RandomUtil.randomUUID().replaceAll("-", "");
                // 1、向节点下添加桩号
                String parentIdAll = all.get(position).getParentIdAll() + "," + parentLevelId;
                String parentNameAll = all.get(position).getParentNameAll() + "," + pileNo;

                all.get(position).setFolderFlag("0");
                all.get(position).setLoading(true);
                allCache.get(position).setFolderFlag("0");
                allCache.get(position).setLoading(true);

                List<ContractorBean> beanList1 = DataSupport.where("levelId=?", all.get(position).getLevelId()).find(ContractorBean.class);
                for (ContractorBean bean : beanList1) {
                    bean.setCanExpand("0");
                    bean.saveOrUpdate("levelId=?", bean.getLevelId());
                }

                ContractorBean newPileNo = addLocalLevel(parentLevelId, pileNo, all.get(position).getLevelId(), "1", "0", "", parentIdAll, parentNameAll, "1");
                beanList.add(newPileNo);
                // 向桩号下添加层级
                for (String str : dictId) {
                    // 查询层级
                    List<ProcessDictionaryBean> proList = DataSupport.where("dictId=?", str).find(ProcessDictionaryBean.class);
                    if (proList != null) {
                        for (ProcessDictionaryBean proBean : proList) {
                            String levelId = RandomUtil.randomUUID().replaceAll("-", "");
                            parentIdAll += "," + levelId;
                            parentNameAll += "," + proBean.getDictName();
                            addLocalLevel(levelId, proBean.getDictName(), parentLevelId, "0", "1", proBean.getDictCode(), parentIdAll, parentNameAll, "2");
                            List<ProcessDictionaryBean> list = DataSupport.where("parentId=?", str).find(ProcessDictionaryBean.class);
                            for (ProcessDictionaryBean bean : list) {
                                addLocalProcess(bean, position, pileNo + "," + proBean.getDictName(), levelId, parentIdAll);
                            }
                        }
                    }
                }
                all.get(position).setExpanded(true);
                allCache.get(position).setExpanded(true);
                setNodeInChildren(beanList, position, false);
            } else if (requestCode == 1006) {
                // 子级新增
                String pileNo = data.getStringExtra("pileNo");
                String levelId = data.getStringExtra("levelId");
                int position = data.getIntExtra("position", 0);
                ArrayList<String> dictId = data.getStringArrayListExtra("dictIdList");
                ArrayList<String> oldDictId = data.getStringArrayListExtra("oldDictIdList");
                ArrayList<String> oldDictName = data.getStringArrayListExtra("oldDictNameList");

                // 判断父级中的子级是否有重复层级名称
                String oldName = all.get(position).getLevelName();
                List<Node> nodeList = all.get(position).getParent().getChildren();
                if (nodeList != null && nodeList.size() != 0) {
                    for (Node node : nodeList) {
                        if (StrUtil.equals(node.getLevelName(), pileNo) && !StrUtil.equals(node.getLevelName(), oldName)) {
                            ToastUtil.showShort(mContext, "不能添加相同层级名称的数据！");
                            return;
                        }
                    }
                }

                // 修改桩号
                List<ContractorBean> pileNoList = DataSupport.where("levelId=?", levelId).find(ContractorBean.class);
                if (pileNoList != null) {
                    for (ContractorBean bean : pileNoList) {
                        bean.setLevelName(pileNo);
                        bean.setParentNameAll(bean.getParentNameAll().substring(0, bean.getParentNameAll().lastIndexOf(",")) + "," + pileNo);
                        bean.saveOrUpdate("levelId=?", bean.getLevelId());
                    }
                }

                // 判断以前选择的层级是否还在---有去除新选择的--没有删除原来的
                for (int i = 0; i < oldDictId.size(); i++) {
                    if (dictId.contains(oldDictId.get(i))) {
                        dictId.remove(oldDictId.get(i));
                    } else {
                        List<ContractorBean> oldLevelList = DataSupport.where("parentId=? and levelName=?", levelId, oldDictName.get(i)).find(ContractorBean.class);
                        if (oldLevelList != null) {
                            for (ContractorBean bean : oldLevelList) {
                                DataSupport.deleteAll(ContractorBean.class, "levelId=?", bean.getLevelId());
                                DataSupport.deleteAll(WorkingBean.class, "levelId=?", bean.getLevelId());
                            }
                        }
                    }
                }

                // 修改桩号下层级
                for (String str : dictId) {
                    // 查询层级
                    List<ProcessDictionaryBean> proList = DataSupport.where("dictId=?", str).find(ProcessDictionaryBean.class);
                    if (proList != null) {
                        for (ProcessDictionaryBean proBean : proList) {
                            String newLevelId = RandomUtil.randomUUID().replaceAll("-", "");
                            String parentIdAll = all.get(position).getParentIdAll() + "," + newLevelId;
                            String parentNameAll = all.get(position).getParentNameAll() + "," + proBean.getDictName();
                            addLocalLevel(newLevelId, proBean.getDictName(), levelId, "0", "1", proBean.getDictCode(), parentIdAll, parentNameAll, "2");
                            List<ProcessDictionaryBean> list = DataSupport.where("parentId=?", str).find(ProcessDictionaryBean.class);
                            for (ProcessDictionaryBean bean : list) {
                                addLocalProcess(bean, position, pileNo + "," + proBean.getDictName(), newLevelId, parentIdAll);
                            }
                        }
                    }
                }

                ta.ExpandOrCollapse(position);

                all.get(position).setLevelName(pileNo);
                if (all.get(position).getChildren() != null) {
                    List<Node> nodeList1 = all.get(position).getChildren();
                    for (Node node : nodeList1) {
                        allCache.remove(node);
                    }
                    all.get(position).getChildren().clear();
                }
                allCache.get(position).setLevelName(pileNo);
                all.get(position).setExpanded(false);
                all.get(position).setLoading(false);
                allCache.get(position).setExpanded(false);
                allCache.get(position).setLoading(false);
                ta.notifyDataSetChanged();
            }
        }
    }

    /**
     * 本地添加层级
     *
     * @param levelId
     * @param levelName
     * @param parentId
     * @param isFolder
     * @param canExpand
     */
    private ContractorBean addLocalLevel(String levelId, String levelName, String parentId, String isFolder, String canExpand, String levelCode, String parentIdAll, String parentNameAll, String levelLevel) {
        ContractorBean newPileNo = new ContractorBean();
        newPileNo.setLevelId(levelId);// 层级ID
        newPileNo.setLevelName(levelName);// 层级名称
        newPileNo.setLevelCode(levelCode);
        newPileNo.setParentId(parentId);// 父ID
        newPileNo.setParentIdAll(parentIdAll);// 父ID
        newPileNo.setParentNameAll(parentNameAll);
        newPileNo.setFolderFlag(isFolder);// 是否是文件夹flag 0:不是文件夹 1：是文件夹
        newPileNo.setProcessNum(0);// 工序数量
        newPileNo.setFinishedNum(0); // 已完成工序数量
        newPileNo.setSelect(false); // 是否被选中
        newPileNo.setLevelType(processType);// 质量或安全
        newPileNo.setCanExpand(canExpand);// 是否有子工序 1:有 0：无
        newPileNo.setIsLocalAdd(1);
        newPileNo.setLevelLevel(levelLevel);
        newPileNo.setUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        newPileNo.saveOrUpdate("levelId=?", levelId);
        return newPileNo;
    }

    /**
     * 保存工序
     *
     * @param processBean
     * @param point
     * @return
     */
    private WorkingBean addLocalProcess(ProcessDictionaryBean processBean, int point, String levelName, String levelId, String parentNameAll) {
        WorkingBean workingBean = new WorkingBean();
        String processId = RandomUtil.randomUUID().replaceAll("-", "");
        workingBean.setProcessId(processId);
        workingBean.setProcessName(processBean.getDictName());
        workingBean.setProcessCode(processBean.getDictCode());
        workingBean.setPhotoContent(processBean.getPhotoContent());
        workingBean.setPhotoDistance(processBean.getPhotoDistance());
        workingBean.setPhotoNumber(processBean.getPhotoNumber() + "");
        workingBean.setLevelId(levelId);
        workingBean.setLevelIdAll(parentNameAll);
        String levelNameAll = ta.getProcessPath(point).replaceAll("→", ",") + "," + levelName;
        workingBean.setLevelNameAll(levelNameAll);
        workingBean.setEnterTime(processBean.getCreateTime());
        workingBean.setWorkId(processId);
        workingBean.setCheckNameAll("未审核");
        workingBean.setType("1");
        workingBean.setIsLocalAdd(1);
        workingBean.setLevelLevel("3");
        workingBean.setUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        workingBean.setFileOperationFlag("1");
        workingBean.saveOrUpdate("processId=?", processBean.getDictId());
        return workingBean;
    }

    @Event({R.id.imgBtnLeft, R.id.btnQuerySelect, R.id.imgBtnRight})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnQuerySelect:
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
        super.onDestroy();
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
    }
}
