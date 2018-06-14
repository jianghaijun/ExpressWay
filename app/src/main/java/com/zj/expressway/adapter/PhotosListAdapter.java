package com.zj.expressway.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.activity.LoginActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.PictureBean;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ShowPhotoListener;
import com.zj.expressway.model.PictureModel;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

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
 *       Created by HaiJun on 2018/6/11 17:11
 *       工序详情照片列表适配器
 */
public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.ContractorDetailsHolder> {
    private Activity mContext;
    private ShowPhotoListener listener;
    private List<PhotosBean> phoneListBean;
    private RequestOptions options;
    private String levelId;
    private String status;

    public PhotosListAdapter(Context mContext, List<PhotosBean> phoneListBean, ShowPhotoListener listener, String levelId, String status) {
        this.mContext = (Activity) mContext;
        this.listener = listener;
        this.levelId = levelId;
        this.status = status;
        this.phoneListBean = phoneListBean;
        options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);
    }

    @Override
    public ContractorDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContractorDetailsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contractor_delite, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContractorDetailsHolder holder, final int position) {
        ObjectAnimator anim = ObjectAnimator.ofInt(holder.ivUpLoadPhone, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        String fileUrl = phoneListBean.get(position).getThumb_path();
        if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
            fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
        }

        holder.txtStatus.setVisibility(View.VISIBLE);
        if (phoneListBean.get(position).getIsToBeUpLoad() == 1) {
            holder.txtStatus.setText("待上传");
        } else {
            switch (status) {
                case "1":
                    holder.txtStatus.setText("已上传");
                    break;
                case "2":
                    holder.txtStatus.setText("初审中");
                    break;
                case "3":
                    holder.txtStatus.setText("初审驳回");
                    break;
                case "4":
                    if (!phoneListBean.get(position).getRole_flag().equals("2")) {
                        holder.txtStatus.setText("自检完成");
                    } else {
                        holder.txtStatus.setText("已上传");
                    }
                    break;
                case "5":
                    holder.txtStatus.setText("复审驳回");
                    break;
                case "6":
                    holder.txtStatus.setText("终审中");
                    break;
                case "7":
                    holder.txtStatus.setText("终审驳回");
                    break;
                case "8":
                    if (!phoneListBean.get(position).getRole_flag().equals("2")) {
                        holder.txtStatus.setText("自检完成");
                    } else {
                        holder.txtStatus.setText("抽检完成");
                    }
                    break;
            }
        }

        Glide.with(mContext)
                .load(fileUrl)
                .apply(options)
                .thumbnail(0.1f)
                .into(holder.ivUpLoadPhone);

        // 图片点击事件
        holder.ivUpLoadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 图片浏览方式
                listener.selectWayOrShowPhoto(false, String.valueOf(position), "", phoneListBean.get(position).getIsToBeUpLoad());
            }
        });

        /**
         * 长按事件
         */
        holder.ivUpLoadPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PromptDialog promptDialog = new PromptDialog(mContext, new PromptListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            // 删除照片
                            if (1 == phoneListBean.get(position).getIsToBeUpLoad()) {
                                DataSupport.deleteAll(PhotosBean.class, "photoAddress=?", phoneListBean.get(position).getPhoto_address());
                                phoneListBean.remove(position);
                                PhotosListAdapter.this.notifyDataSetChanged();
                            } else {
                                deletePhoto(phoneListBean.get(position), position);
                            }
                        }
                    }
                }, "提示", "是否删除此照片？", "否", "是");
                promptDialog.show();
                return true;
            }
        });
    }

    /**
     * 删除服务器上图片
     * @param bean
     * @param point
     */
    private void deletePhoto(final PhotosBean bean, final int point) {
        LoadingUtils.showLoading(mContext);
        // 参数
        PictureModel model = new PictureModel();
        model.setSelectUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        model.setRootLevelId(levelId);
        model.setProcessId(bean.getProcess_id());
        List<PictureBean> beanList = new ArrayList<>();
        PictureBean picBean = new PictureBean();
        picBean.setPhotoId(bean.getPhoto_id());
        beanList.add(picBean);
        model.setSxZlPhotoList(beanList);

        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(model).toString());

        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.DELETE_PHOTOS)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();

        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showShort(mContext, mContext.getString(R.string.server_exception));
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
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, "删除成功！");
                                    phoneListBean.remove(point);
                                    DataSupport.deleteAll(PhotosBean.class, "photoId=?", bean.getPhoto_id());
                                    PhotosListAdapter.this.notifyDataSetChanged();
                                }
                            });
                        } else {
                            mContext.runOnUiThread(new Runnable() {
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
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, mContext.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
                        }
                    });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return phoneListBean.size();
    }

    public class ContractorDetailsHolder extends RecyclerView.ViewHolder {
        private ImageView ivUpLoadPhone;
        private TextView txtStatus;

        public ContractorDetailsHolder(View itemView) {
            super(itemView);
            ivUpLoadPhone = (ImageView) itemView.findViewById(R.id.ivUpLoadPhone);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
        }
    }

}
