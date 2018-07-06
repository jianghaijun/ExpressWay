package com.zj.expressway.activity;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.zj.expressway.R;
import com.zj.expressway.adapter.AppInfoAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.AppInfoBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.loader.GlideImageLoader;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HaiJun on 2018/6/11 16:32
 * 应用图标列表
 */
public class AppActivity extends BaseActivity {
    private AppHold hold;
    private Activity mContext;
    private AppInfoAdapter appInfoAdapter;

    public AppActivity(Activity mContext, View layoutApp) {
        this.mContext = mContext;
        hold = new AppHold();
        x.view().inject(hold, layoutApp);
    }

    public void setDate(List<String> objList, final WorkingBean data) {
        //设置banner样式
        hold.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        hold.banner.setImageLoader(new GlideImageLoader());
        //设置banner动画效果
        hold.banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        hold.banner.isAutoPlay(true);
        //设置轮播时间
        hold.banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        hold.banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置图片集合
        hold.banner.setImages(objList);
        hold.banner.start();

        // 开启跑马灯
        hold.txtHorseRaceLamp.setText("中国交通建设股份有限公司成立于2006年10月8日，经国务院批准，由中国交通建设集团有限公司（国务院国资委监管的中央企业）整体重组改制并独家发起设立的股份有限公司，并于2006年12月15日在香港联合交易所主板挂牌上市交易，成为中国第一家实现境外整体上市的特大型国有基建企业。");
        hold.txtHorseRaceLamp.setSelected(true);

        final List<String> stringList = new ArrayList<>();
        stringList.add("2005至今，公司先后获得567项自主知识产权专利，荣获20项国家科学技术进步奖，279项省部级科技进步奖，16项鲁班奖，30项詹天佑土木工程大奖，59项国家优质工程奖（其中金奖7项），242项省部级优质工程奖，35项国家级工法。");
        stringList.add("2011年，公司名列世界500强第211位，排名较上年提升了13位；位居ENR全球最大225家国际承包商第11位，连续5年位居中国上榜企业第1名；位居中国企业500强第19位。");
        stringList.add("2010年，公司入选“福布斯全球2000强企业”榜单，排名位列第297位，居中国内地建筑企业首位。");

        hold.txtEnterPriseInfo.setText(stringList.get(0));

        // 活动监听
        hold.banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                hold.txtEnterPriseInfo.setText(stringList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // 添加图片、标题
        List<AppInfoBean> appInfoList = new ArrayList<>();
        AppInfoBean bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.concealment_project);
        bean.setTitle(mContext.getString(R.string.process_inspection));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.quality_testing);
        bean.setTitle(mContext.getString(R.string.qualityPatrol));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.hidden_trouble_investigation);
        bean.setTitle(mContext.getString(R.string.securityPatrol));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.audit_management);
        bean.setTitle(mContext.getString(R.string.audit_management));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.data_report);
        bean.setTitle("数据报表");
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.qr_code);
        bean.setTitle(mContext.getString(R.string.qr_code));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.bid_management);
        bean.setTitle("标段管理");
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.group_management);
        bean.setTitle("资料库");
        appInfoList.add(bean);

        appInfoAdapter = new AppInfoAdapter(mContext, appInfoList);
        hold.rvAppInfo.setLayoutManager(new GridLayoutManager(mContext, 4));
        hold.rvAppInfo.setAdapter(appInfoAdapter);
    }

    /**
     * 开始轮播
     */
    public void startBanner() {
        //开始轮播
        if (hold != null && hold.banner != null) {
            hold.banner.startAutoPlay();
        }
    }

    /**
     * 停止轮播
     */
    public void stopBanner() {
        if (hold != null && hold.banner != null) {
            hold.banner.stopAutoPlay();
        }
    }

    /**
     * 容纳器
     */
    private class AppHold {
        @ViewInject(R.id.banner)
        private Banner banner;

        @ViewInject(R.id.rvAppInfo)
        private RecyclerView rvAppInfo;
        @ViewInject(R.id.txtEnterPriseInfo)
        private TextView txtEnterPriseInfo;
        @ViewInject(R.id.txtHorseRaceLamp)
        private TextView txtHorseRaceLamp;

    }
}
