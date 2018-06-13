package com.zj.expressway.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.WorkingBean;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

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
 *       Created by HaiJun on 2018/6/11 16:45
 *       消息列表主界面
 */
public class MsgMainActivity extends BaseActivity {
    private MsgMainHolder holder;
    private Context mContext;

    // 子布局
    private View layoutLocalMsg, layoutPersonalMsg;
    private LocalMsgActivity localMsgActivity;
    private PersonalMsgActivity personalMsgActivity;
    // View列表
    private ArrayList<View> views;

    public MsgMainActivity(Context mContext, View layoutMsg) {
        this.mContext = mContext;
        holder = new MsgMainHolder();
        x.view().inject(holder, layoutMsg);

        //将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(mContext);
        layoutLocalMsg = viewLI.inflate(R.layout.layout_msg, null);
        layoutPersonalMsg = viewLI.inflate(R.layout.layout_msg, null);

        localMsgActivity = new LocalMsgActivity(mContext, layoutLocalMsg);
        personalMsgActivity = new PersonalMsgActivity(mContext, layoutPersonalMsg);

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layoutLocalMsg);
        views.add(layoutPersonalMsg);

        holder.vpMsgMain.setOnPageChangeListener(new MyOnPageChangeListener());
        holder.vpMsgMain.setAdapter(mPagerAdapter);

        holder.btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.vpMsgMain.setCurrentItem(0);
            }
        });

        holder.btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.vpMsgMain.setCurrentItem(1);
            }
        });

    }

    /**
     * 填充ViewPager的数据适配器
     */
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    };

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            setStates(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 设置背景
     *
     * @param option
     */
    private void setStates(int option) {
        holder.btnLocalMsg.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));
        holder.vLocalMsg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));
        holder.btnPersonalMsg.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));
        holder.vPersonalMsg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));
        switch (option) {
            case 0:
                holder.btnLocalMsg.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                holder.vLocalMsg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                localMsgActivity.setDate(false);
                break;
            case 1:
                holder.btnPersonalMsg.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                holder.vPersonalMsg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                personalMsgActivity.setDate(false);
                break;
        }
    }

    public void setDate(WorkingBean workingBean) {
        holder.txtLocalNum.setText(workingBean == null || workingBean.getSystemNum() == null ? "0" : workingBean.getSystemNum());
        holder.txtPersonalNum.setText(workingBean == null || workingBean.getPersonalNum() == null ? "0" : workingBean.getPersonalNum());
        holder.vpMsgMain.setCurrentItem(0);
        localMsgActivity.setDate(true);
        personalMsgActivity.setDate(true);
    }

    /**
     * 容纳器
     */
    private class MsgMainHolder {
        @ViewInject(R.id.btnLocal)
        private Button btnLocal;
        @ViewInject(R.id.btnLocalMsg)
        private Button btnLocalMsg;
        @ViewInject(R.id.txtLocalNum)
        private TextView txtLocalNum;
        @ViewInject(R.id.vLocalMsg)
        private View vLocalMsg;

        @ViewInject(R.id.btnPersonal)
        private Button btnPersonal;
        @ViewInject(R.id.btnPersonalMsg)
        private Button btnPersonalMsg;
        @ViewInject(R.id.txtPersonalNum)
        private TextView txtPersonalNum;
        @ViewInject(R.id.vPersonalMsg)
        private View vPersonalMsg;

        @ViewInject(R.id.vpMsgMain)
        private ViewPager vpMsgMain;
    }
}
