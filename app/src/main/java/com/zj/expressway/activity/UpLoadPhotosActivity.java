package com.zj.expressway.activity;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.adapter.UpLoadPhotosAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.UpLoadPhotosDialog;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

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
 *       Created by HaiJun on 2018/6/11 16:57
 *       待上传照片
 */
public class UpLoadPhotosActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnRight)
    private Button btnRight;

    @ViewInject(R.id.rvUpLoadPhone)
    private RecyclerView rvUpLoadPhone;

    private Context mContext;
    private UpLoadPhotosAdapter adapter;

    private List<PhotosBean> upLoadPhotosBeenList = new ArrayList<>();

    private boolean isCanUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_up_load_photos);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(getResources().getDrawable(R.drawable.back_btn));
        txtTitle.setText(R.string.show_photo);
        btnRight.setText("上传");
        btnRight.setVisibility(View.VISIBLE);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 获取当前登录人员需要上传的图片
        upLoadPhotosBeenList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "")).find(PhotosBean.class);

        if (Build.VERSION.SDK_INT >= 23) {
            requestAuthority(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void agree() {
                    adapter = new UpLoadPhotosAdapter(mContext, upLoadPhotosBeenList);
                    rvUpLoadPhone.setLayoutManager(new GridLayoutManager(mContext, 4));
                    rvUpLoadPhone.setAdapter(adapter);
                }

                @Override
                public void refuse(List<String> refusePermission) {
                    for (String refuse : refusePermission) {
                        ToastUtil.showLong(mContext, "您已拒绝：" + refuse + "权限!");
                    }
                }
            });
        } else {
            adapter = new UpLoadPhotosAdapter(mContext, upLoadPhotosBeenList);
            rvUpLoadPhone.setLayoutManager(new GridLayoutManager(mContext, 4));
            rvUpLoadPhone.setAdapter(adapter);
        }
    }

    @Event({R.id.imgBtnLeft, R.id.btnRight})
    private void onClick(View view) {
        switch (view.getId()) {
            // 上传图片
            case R.id.btnRight:
                if (!isCanUpload) {
                    isCanUpload = true;
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                        if (upLoadPhotosBeenList.size() > 0) {
                            if (!JudgeNetworkIsAvailable.GetNetworkType(this).equals("WIFI")) {
                                PromptDialog promptDialog = new PromptDialog(mContext, netWorkTypeListener, "提示", "当前网络为移动网络,是否继续上传?", "否", "是");
                                promptDialog.setCancelable(false);
                                promptDialog.setCanceledOnTouchOutside(false);
                                promptDialog.show();
                            } else {
                                upLoadPhoto();
                            }
                        } else {
                            isCanUpload = false;
                            ToastUtil.showLong(mContext, "暂无可上传照片!");
                        }
                    } else {
                        isCanUpload = false;
                        ToastUtil.showLong(mContext, getString(R.string.not_network));
                    }
                }
                break;
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }

    /**
     * 是否使用移动网络上传照片
     */
    private PromptListener netWorkTypeListener = new PromptListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                upLoadPhoto();
            } else {
                isCanUpload = false;
            }
        }
    };

    /**
     * 上传照片
     */
    private void upLoadPhoto() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestAuthority(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void agree() {
                    if (upLoadPhotosBeenList != null && upLoadPhotosBeenList.size() > 50) {
                        PromptDialog promptDialog = new PromptDialog(mContext, listener, "提示", "当前照片数量过多，是否先上传前50张？", "否", "是");
                        promptDialog.show();
                    } else {
                        UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, 1, upLoadPhotosBeenList, choiceListener);
                        upLoadPhotosDialog.setCancelable(false);
                        upLoadPhotosDialog.setCanceledOnTouchOutside(false);
                        upLoadPhotosDialog.show();
                    }
                }

                @Override
                public void refuse(List<String> refusePermission) {
                    isCanUpload = false;
                    for (String refuse : refusePermission) {
                        ToastUtil.showLong(mContext, "您已拒绝：" + refuse + "权限!");
                    }
                }
            });
        } else {
            if (upLoadPhotosBeenList != null && upLoadPhotosBeenList.size() > 50) {
                PromptDialog promptDialog = new PromptDialog(mContext, listener, "提示", "当前照片数量过多，是否先上传前50张？", "否", "是");
                promptDialog.show();
            } else {
                UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, 1, upLoadPhotosBeenList, choiceListener);
                upLoadPhotosDialog.setCancelable(false);
                upLoadPhotosDialog.setCanceledOnTouchOutside(false);
                upLoadPhotosDialog.show();
            }
        }
    }

    /**
     * 文件上传
     */
    private PromptListener listener = new PromptListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            isCanUpload = false;
            if (trueOrFalse) {
                upLoadPhotosBeenList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? order by createTime desc limit 0, 50", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "")).find(PhotosBean.class);
                UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, 1, upLoadPhotosBeenList, choiceListener);
                upLoadPhotosDialog.setCancelable(false);
                upLoadPhotosDialog.setCanceledOnTouchOutside(false);
                upLoadPhotosDialog.show();
            }
        }
    };

    /**
     * 文件上传成功更新UI
     */
    private PromptListener choiceListener = new PromptListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            isCanUpload = false;
            if (trueOrFalse) {
                upLoadPhotosBeenList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "")).find(PhotosBean.class);
                adapter = new UpLoadPhotosAdapter(mContext, upLoadPhotosBeenList);
                rvUpLoadPhone.setLayoutManager(new GridLayoutManager(mContext, 4));
                rvUpLoadPhone.setAdapter(adapter);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, "上传失败！");
                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
