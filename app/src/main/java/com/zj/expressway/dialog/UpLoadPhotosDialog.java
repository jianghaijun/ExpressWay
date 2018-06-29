package com.zj.expressway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.model.LoginModel;
import com.zj.expressway.model.UploadFileModel;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 18:02
 * 照片上传Dialog
 */
public class UpLoadPhotosDialog extends Dialog {
    private List<PhotosBean> upLoadPhotosBeenList;
    private int type;
    private Context mContext;
    private Handler upLoadPhotosHandler;
    private TextView txtNum;
    private ProgressBar proBarUpLoadPhotos;
    private PromptListener choiceListener;

    /**
     *
     * @param context
     * @param type  工序--质量
     * @param upLoadPhotosBeenList
     * @param choiceListener
     */
    public UpLoadPhotosDialog(Context context, int type, List<PhotosBean> upLoadPhotosBeenList, PromptListener choiceListener) {
        super(context);
        this.type = type;
        this.mContext = context;
        this.choiceListener = choiceListener;
        this.upLoadPhotosBeenList = upLoadPhotosBeenList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_up_load_photos);

        proBarUpLoadPhotos = (ProgressBar) this.findViewById(R.id.proBarUpLoadPhotos);
        txtNum = (TextView) this.findViewById(R.id.txtNum);

        upLoadPhotosHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        UpLoadPhotosDialog.this.dismiss();
                        choiceListener.returnTrueOrFalse(true);
                        ToastUtil.showShort(mContext, "文件上传失败！");
                        break;
                    case -2:
                        UpLoadPhotosDialog.this.dismiss();
                        ToastUtil.showShort(mContext, mContext.getString(R.string.json_error));
                        break;
                    case -3:
                        UpLoadPhotosDialog.this.dismiss();
                        String val = msg.getData().getString("key");
                        ToastUtil.showShort(mContext, val);
                        break;
                    case 200:
                        // 移除已经上传的照片
                        for (PhotosBean fileBean : upLoadPhotosBeenList) {
                            fileBean.delete();
                        }
                        UpLoadPhotosDialog.this.dismiss();
                        choiceListener.returnTrueOrFalse(true);
                        ToastUtil.showShort(mContext, "文件上传成功！");
                        break;
                    case 100:
                        txtNum.setText("已上传：100%");
                        LoadingUtils.showLoading(mContext);
                        break;
                    default:
                        txtNum.setText("已上传：" + msg.what + "%");
                        break;
                }
            }
        };

        if (null != upLoadPhotosBeenList && upLoadPhotosBeenList.size() > 0) {
            proBarUpLoadPhotos.setMax(100000000);
            UpLoadPhotoLists();
        }
    }

    /**
     * 上传文件
     */
    private void UpLoadPhotoLists() {
        Gson gson = new Gson();
        Map<String, File> fileMap = new HashMap<>();
        for (PhotosBean fileBean : upLoadPhotosBeenList) {
            Map<String, Object> map = new HashMap<>();
            map.put("processId", fileBean.getOtherId());
            map.put("troubleId", fileBean.getOtherId());
            map.put("dangerId", fileBean.getOtherId());
            map.put("fileDesc", fileBean.getPhotoDesc());
            map.put("otherId", fileBean.getOtherId());
            map.put("longitude", fileBean.getLongitude());
            map.put("latitude", fileBean.getLatitude());
            map.put("location", fileBean.getLocation() == null ? "" : fileBean.getLocation());
            map.put("fileName", fileBean.getPhotoName());
            map.put("fileType", fileBean.getPhotoType());
            if (type == 3 || type == 4) {
                fileMap.put(fileBean.getPhotoName(), new File(fileBean.getUrl()));
            } else {
                fileMap.put(gson.toJson(map), new File(fileBean.getUrl()));
            }
        }

        String url = ConstantsUtil.BASE_URL;
        switch (type) {
            case 1:
                url += ConstantsUtil.UP_LOAD_PHOTOS;
                break;
            case 3:
            case 4:
                url += ConstantsUtil.upload;
                break;
        }

        OkHttpUtils.post()
                .files("filesName", fileMap)
                .addParams("projectId", "zz")
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .url(url)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        LoadingUtils.hideLoading();
                        String jsonData = response.body().string().toString();
                        if (JsonUtils.isGoodJson(jsonData)) {
                            Gson gson = new Gson();
                            BaseModel loginModel = gson.fromJson(jsonData, BaseModel.class);
                            if (!loginModel.isSuccess()) {
                                Message jsonErr = new Message();
                                jsonErr.what = -3;
                                Bundle bundle = new Bundle();
                                bundle.putString("key", loginModel.getMessage());
                                jsonErr.setData(bundle);
                                upLoadPhotosHandler.sendMessage(jsonErr);
                            } else {
                                JSONObject obj = new JSONObject(jsonData);
                                if (type == 3 || type == 4) {
                                    String data = String.valueOf(obj.getJSONArray("data"));
                                    SpUtil.put(mContext, "uploadImgData", data);
                                }
                                Message success = new Message();
                                success.what = 200;
                                upLoadPhotosHandler.sendMessage(success);
                            }
                        } else {
                            Message jsonErr = new Message();
                            jsonErr.what = -2;
                            upLoadPhotosHandler.sendMessage(jsonErr);
                        }
                        return null;
                    }

                    @Override
                    public void onError(final Call call, final Exception e, final int id) {
                        LoadingUtils.hideLoading();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                choiceListener.returnTrueOrFalse(false);
                                UpLoadPhotosDialog.this.dismiss();
                            }
                        }).start();
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        LoadingUtils.hideLoading();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        proBarUpLoadPhotos.setProgress((int) (progress * 100000000));
                        float result = (float) proBarUpLoadPhotos.getProgress() / (float) proBarUpLoadPhotos.getMax();
                        int p = (int) (result * 100);
                        Message message = new Message();
                        message.what = p;
                        upLoadPhotosHandler.sendMessage(message);
                    }
                });
    }
}
