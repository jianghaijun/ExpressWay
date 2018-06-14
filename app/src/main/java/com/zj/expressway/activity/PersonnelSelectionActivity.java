package com.zj.expressway.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.PersonnelTreeAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.NextShowFlow;
import com.zj.expressway.bean.PersonnelBean;
import com.zj.expressway.model.PersonnelListModel;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonnelSelectionActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.btnRight)
    private Button btnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.txtNode)
    private TextView txtNode;
    @ViewInject(R.id.txtPersonalName)
    private TextView txtPersonalName;
    @ViewInject(R.id.txtNoPerson)
    private TextView txtNoPerson;
    @ViewInject(R.id.txtSelect)
    private TextView txtSelect;
    @ViewInject(R.id.llPersonal)
    private LinearLayout llPersonal;
    // 工序人员List
    @ViewInject(R.id.lvContractorList)
    private ListView lvPersonnelList;
    // 适配器
    private PersonnelTreeAdapter treeAdapter;
    private Activity mContext;
    private List<Node> allNode = new ArrayList<>();
    private List<Node> nodeList = new ArrayList<>();
    private Node selectNode;
    private String reviewNodeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_personnel);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_btn));
        txtTitle.setText("人员选择");
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("确认");

        lvPersonnelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonnelTreeAdapter.ViewHolder holder = (PersonnelTreeAdapter.ViewHolder) view.getTag();
                for (Node node : allNode) {
                    if (node.getLevelId().equals(holder.levelId)) {
                        if (!node.getFolderFlag().equals("2")) {
                            boolean isHave = false;
                            for (Node n : nodeList) {
                                if (n.getLevelId().equals(node.getLevelId())) {
                                    isHave = true;
                                    break;
                                }
                            }
                            if (!isHave) {
                                nodeList.add(node);
                                txtNode.setText(getClickableSpan(nodeList));
                            }
                        } else {
                            selectNode = node;
                            txtNoPerson.setVisibility(View.GONE);
                            txtPersonalName.setText(node.getLevelName());
                            llPersonal.setVisibility(View.VISIBLE);
                        }
                        treeAdapter.ExpandOrCollapse(node);
                        break;
                    }
                }
            }
        });

        if (ConstantsUtil.buttonModel != null && ConstantsUtil.buttonModel.getNextShowFlowInfoList().size() > 0) {
            txtSelect.setVisibility(View.VISIBLE);
            txtSelect.setText("流程操作：" + ConstantsUtil.buttonModel.getNextShowFlowInfoList().get(0).getNextNodeName());
            reviewNodeId = ConstantsUtil.buttonModel.getNextShowFlowInfoList().get(0).getNextNodeId();
        } else {
            txtSelect.setVisibility(View.GONE);
        }

        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            ToastUtil.showShort(mContext, getString(R.string.not_network));
        }

        txtNode.setClickable(true);
        nodeList.clear();
        Node node = new Node();
        node.setLevelName("全部");
        node.setLevelId("");
        nodeList.add(node);
        txtNode.setText(getClickableSpan(nodeList));
        txtNode.setMovementMethod(LinkMovementMethod.getInstance());
        txtNode.setHighlightColor(ContextCompat.getColor(mContext, android.R.color.transparent));
    }

    /**
     * 添加节点
     *
     * @param node
     */
    private void addNode(Node node) {
        if (node.getParent() != null) {
            allNode.add(node);
        }
        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(node.getChildren().get(i));
        }
    }

    /**
     * 局部点击
     *
     * @param nodeList
     * @return
     */
    private SpannableString getClickableSpan(List<Node> nodeList) {
        StringBuffer sb = new StringBuffer();
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            if (i < size - 1) {
                sb.append(nodeList.get(i).getLevelName() + "/");
            } else {
                sb.append(nodeList.get(i).getLevelName());
            }
        }
        SpannableString spannableInfo = new SpannableString(sb.toString());
        // 查找可点击文本
        String str = sb.toString();
        int start, end;
        for (int i = 0; i < size; i++) {
            start = str.indexOf(nodeList.get(i).getLevelName());
            end = start + nodeList.get(i).getLevelName().length();
            // 实现局部点击效果
            spannableInfo.setSpan(new Clickable(nodeList.get(i)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (i < size - 1) {
                // 用来消除点击文字下划线
                spannableInfo.setSpan(new NoUnderlineSpan(), start, end, Spanned.SPAN_MARK_MARK);
            } else {
                // 用来消除点击文字下划线
                spannableInfo.setSpan(new NoUnderlineAndColorSpan(), start, end, Spanned.SPAN_MARK_MARK);
            }
        }
        return spannableInfo;
    }

    /**
     * 局部点击事件
     */
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private Node node;

        public Clickable(Node node) {
            this.node = node;
        }

        @Override
        public void onClick(View v) {
            nodeList.clear();
            if (node.getLevelName().equals("全部")) {
                Node n = new Node();
                n.setLevelName("全部");
                n.setLevelId("");
                nodeList.add(n);
                txtNode.setText(getClickableSpan(nodeList));
                treeAdapter.ExpandOrCollapse(allNode.get(0).getParent());
            } else {
                Node n = new Node();
                n.setLevelName("全部");
                n.setLevelId("");
                nodeList.add(n);
                setParent(node);
                txtNode.setText(getClickableSpan(nodeList));
                treeAdapter.ExpandOrCollapse(node);
            }
        }
    }


    private void setParent(Node node) {
        nodeList.add(node);

        if (node.getParent() != null && node.getParent().getLevelId() != null) {
            setParent(node.getParent());
        }
    }

    @SuppressLint("ParcelCreator")
    public class NoUnderlineSpan extends UnderlineSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(mContext, R.color.main_bg));
            ds.setUnderlineText(false);
        }
    }

    @SuppressLint("ParcelCreator")
    public class NoUnderlineAndColorSpan extends UnderlineSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(mContext, R.color.black));
            ds.setUnderlineText(false);
        }
    }

    /**
     * 展示选择框
     */
    private void showSelectDialog() {
        int size = ConstantsUtil.buttonModel.getNextShowFlowInfoList().size();
        List<NextShowFlow> dataList = ConstantsUtil.buttonModel.getNextShowFlowInfoList();
        final String[] strList = new String[size];
        final String[] idList = new String[size];
        for (int i = 0; i < size; i++) {
            strList[i] = dataList.get(i).getNextNodeName();
            idList[i] = dataList.get(i).getNextNodeId();
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
                reviewNodeId = idList[index];
                txtSelect.setText("流程操作：" + strList[index]);
            }
        });
        picker.show();
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, "");
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PERSONNEL_LIST)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            uiThread(true, getString(R.string.server_exception));
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
                        final PersonnelListModel model = gson.fromJson(jsonData, PersonnelListModel.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 设置节点
                                setPersonnelNode(model.getData());
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        LoadingUtils.hideLoading();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (code) {
                                    case "3003":
                                    case "3004":
                                        ToastUtil.showLong(mContext, "Token过期请重新登录！");
                                        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                        ScreenManagerUtil.popAllActivityExceptOne();
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                        break;
                                    default:
                                        ToastUtil.showLong(mContext, msg);
                                        break;
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    uiThread(true, getString(R.string.data_error));
                    e.printStackTrace();
                }
            } else {
                uiThread(true, getString(R.string.json_error));
            }
        }
    };

    /**
     * 设置节点,可以通过循环或递归方式添加节点
     *
     * @param personnelNode
     */
    private void setPersonnelNode(PersonnelBean personnelNode) {
        // 添加节点
        if (personnelNode != null) {
            // 创建根节点
            Node root = new Node();
            root.setFolderFlag("1");
            personnelNode(personnelNode, root);
            addNode(root);
            if (selectNode != null) {
                txtNoPerson.setVisibility(View.GONE);
                txtPersonalName.setText(selectNode.getLevelName());
            } else {
                txtNoPerson.setVisibility(View.VISIBLE);
                llPersonal.setVisibility(View.GONE);
            }
            treeAdapter = new PersonnelTreeAdapter(this, root.getChildren());
            lvPersonnelList.setAdapter(treeAdapter);
        }
    }

    /**
     * 子节点
     *
     * @param personnelNode
     * @param root
     */
    private void personnelNode(PersonnelBean personnelNode, Node root) {
        // 创建子节点
        Node n = new Node();
        n.setParent(root);
        n.setLevelId(personnelNode.getValue());
        n.setLevelName(personnelNode.getLabel());
        n.setParentId(personnelNode.getValuePid());
        n.setFolderFlag(personnelNode.getType());
        n.setExpanded(false);
        root.add(n);
        String selectUserId = (String) SpUtil.get(mContext, ConstantsUtil.SELECT_USER_ID, "");
        if (selectUserId.equals(n.getLevelId())) {
            selectNode = n;
        }

        if (personnelNode.getChildren() != null && personnelNode.getChildren().size() > 0) {
            for (PersonnelBean node : personnelNode.getChildren()) {
                personnelNode(node, n);
            }
        }
    }

    /**
     * 子线程运行
     *
     * @param isDismiss 是否隐藏加载动画
     * @param msg       提示信息
     */
    private void uiThread(boolean isDismiss, final String msg) {
        if (isDismiss) {
            LoadingUtils.hideLoading();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(mContext, msg);
            }
        });
    }

    @Event({R.id.imgBtnLeft, R.id.ivDelete, R.id.btnRight, R.id.txtSelect})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.ivDelete:
                SpUtil.remove(mContext, ConstantsUtil.SELECT_USER_ID);
                treeAdapter.notifyDataSetChanged();
                txtNoPerson.setVisibility(View.VISIBLE);
                llPersonal.setVisibility(View.GONE);
                break;
            case R.id.btnRight:
                if (selectNode == null) {
                    ToastUtil.showShort(mContext, "请先选择人员");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("reviewNodeId", reviewNodeId);
                    intent.putExtra("userId", selectNode.getLevelId());
                    intent.putExtra("userName", selectNode.getLevelName());
                    intent.putExtra("type", selectNode.getFolderFlag());
                    setResult(Activity.RESULT_OK, intent);
                    this.finish();
                }
                break;
            case R.id.txtSelect:
                showSelectDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }

}
