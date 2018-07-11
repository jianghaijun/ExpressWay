package com.zj.expressway.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zj.expressway.R;
import com.zj.expressway.activity.ShowPhotosActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.listener.PromptListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HaiJun on 2018/6/11 17:10
 * 待上传照片列表
 */
public class UpLoadPhotosAdapter extends RecyclerView.Adapter<UpLoadPhotosAdapter.UpLoadPhoneHolder> {
    private Activity mContext;
    private RequestOptions options;
    private List<PhotosBean> upLoadPhoneList;

    public UpLoadPhotosAdapter(Context mContext, List<PhotosBean> upLoadPhoneList) {
        this.mContext = (Activity) mContext;
        this.upLoadPhoneList = upLoadPhoneList;
        options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);
    }

    @Override
    public UpLoadPhoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UpLoadPhoneHolder(LayoutInflater.from(mContext).inflate(R.layout.item_uo_laod_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(UpLoadPhoneHolder holder, final int position) {
        ObjectAnimator anim = ObjectAnimator.ofInt(holder.ivUpLoadPhone, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        Glide.with(mContext)
                .load(upLoadPhoneList.get(position).getThumbUrl())
                .apply(options)
                .thumbnail(0.1f)
                .into(holder.ivUpLoadPhone);

        holder.ivUpLoadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 图片浏览
                ArrayList<String> urls = new ArrayList<>();
                int len = upLoadPhoneList.size();
                for (int i = 0; i < len; i++) {
                    String fileUrl = upLoadPhoneList.get(i).getUrl();
                    /*if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                        fileUrl = ConstantsUtil.BASE_URL + fileUrl;
                    }*/
                    urls.add(fileUrl);
                }
                Intent intent = new Intent(mContext, ShowPhotosActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(position));
                mContext.startActivity(intent);
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
                            DataSupport.deleteAll(PhotosBean.class, "url=?", upLoadPhoneList.get(position).getUrl());
                            upLoadPhoneList.remove(position);
                            UpLoadPhotosAdapter.this.notifyDataSetChanged();
                        }
                    }
                }, "提示", "是否删除此照片？", "否", "是");
                promptDialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return upLoadPhoneList.size();
    }

    public class UpLoadPhoneHolder extends RecyclerView.ViewHolder {
        private ImageView ivUpLoadPhone;

        public UpLoadPhoneHolder(View itemView) {
            super(itemView);
            ivUpLoadPhone = (ImageView) itemView.findViewById(R.id.ivUpLoadPhone);
        }
    }

}
