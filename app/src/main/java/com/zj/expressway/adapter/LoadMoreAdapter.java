package com.zj.expressway.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseAdapter;
import com.zj.expressway.listener.ILoadCallback;
import com.zj.expressway.listener.OnLoad;

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
 *       Created by HaiJun on 2018/6/11 17:06
 *       加载更多适配器
 */
public class LoadMoreAdapter extends BaseAdapter<String> {
    private BaseAdapter mAdapter;
    private static final int mPageSize = 10;
    private int mPagePosition = 1;
    private boolean hasMoreData = true;
    private OnLoad mOnLoad;

    public LoadMoreAdapter(BaseAdapter adapter, OnLoad onLoad) {
        mAdapter = adapter;
        mOnLoad = onLoad;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.end) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new NoMoreItemVH(view);
        } else if (viewType == R.layout.more) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new LoadingItemVH(view);
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingItemVH) {
            requestData(mPagePosition, mPageSize);
        } else if (holder instanceof NoMoreItemVH) {

        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    private void requestData(int pagePosition, int pageSize) {
        // 网络请求,如果是异步请求，则在成功之后的回调中添加数据，并且调用notifyDataSetChanged方法，hasMoreData为true
        // 如果没有数据了，则hasMoreData为false，然后通知变化，更新recylerview
        if (mOnLoad != null) {
            mOnLoad.load(pagePosition, pageSize, new ILoadCallback() {
                @Override
                public void onSuccess() {
                    notifyDataSetChanged();
                    mPagePosition++; //= (mPagePosition + 1) * mPageSize;
                    hasMoreData = true;
                }

                @Override
                public void onFailure() {
                    hasMoreData = false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            if (hasMoreData) {
                return R.layout.more;
            } else {
                return R.layout.end;
            }
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 1;
    }

    static class LoadingItemVH extends RecyclerView.ViewHolder {
        public LoadingItemVH(View itemView) {
            super(itemView);
        }
    }

    static class NoMoreItemVH extends RecyclerView.ViewHolder {
        public NoMoreItemVH(View itemView) {
            super(itemView);
        }
    }

}


