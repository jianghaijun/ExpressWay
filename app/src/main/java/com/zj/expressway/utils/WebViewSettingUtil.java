package com.zj.expressway.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Create dell By 2018/7/6 11:07
 */

public class WebViewSettingUtil {

    /**
     * 设置webView属性
     *
     * @param webView
     */
    public static void setSetting(final WebView webView) {
        if (webView != null) {
            WebSettings settings = webView.getSettings();
            // 设置支持JavaScript
            settings.setJavaScriptEnabled(true);
            // WebView双击变大，再双击后变小，当手动放大后，双击可以恢复到原始大小
            settings.setUseWideViewPort(true);
            // 设置webView自适应屏幕大小
            settings.setLoadWithOverviewMode(true);
            // 设置文字默认编码格式
            settings.setDefaultTextEncodingName("UTF-8");
            // 设置的WebView是否支持变焦
            settings.setSupportZoom(true);
            // 设置WebView支持多窗口
            settings.supportMultipleWindows();
            // 设置WebView可触摸放大缩小
            settings.setBuiltInZoomControls(true);
            // 设置允许访问文件
            settings.setAllowFileAccess(true);
            // 禁止 file 协议加载 JavaScript
            /*if (url.startsWith("file://") {
                setJavaScriptEnabled(false);
            } else {
                setJavaScriptEnabled(true);
            }*/

            settings.setLoadWithOverviewMode(true);
            // 开启缓存功能
            settings.setAppCacheEnabled(false);
            // 开启 DOM storage API 功能
            settings.setDomStorageEnabled(true);
            // 设置数据库启用
            settings.setDatabaseEnabled(true);
            // 支持通过JS打开新窗口
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            // 提高渲染的优先级
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            // 启用地理定位
            settings.setGeolocationEnabled(true);

            // 用户调试模式
            settings.setSupportMultipleWindows(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 用户调试模式
            settings.setAllowUniversalAccessFromFileURLs(true);
            webView.setWebViewClient(new WebViewClient() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith("https://")) {
                        view.loadUrl(request.getUrl().toString());
                        webView.stopLoading();
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
        }
    }

    /**
     * 设置webView属性
     *
     * @param webView
     */
    public static void setSettingNoZoom(Context mContext, WebView webView) {
        if (webView != null) {
            WebSettings settings = webView.getSettings();
            // 开启javascript设置
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAppCacheMaxSize(1024*1024*8);
            String appCachePath = mContext.getApplicationContext().getCacheDir().getAbsolutePath();
            settings.setAppCachePath(appCachePath);
            settings.setAllowFileAccess(true);
            settings.setAppCacheEnabled(true);



            /*// 设置支持JavaScript
            settings.setJavaScriptEnabled(true);
            // WebView双击变大，再双击后变小，当手动放大后，双击可以恢复到原始大小
            settings.setUseWideViewPort(false);
            // 设置webView自适应屏幕大小
            settings.setLoadWithOverviewMode(true);
            // 设置文字默认编码格式
            settings.setDefaultTextEncodingName("UTF-8");
            // 设置的WebView是否支持变焦
            //settings.setSupportZoom(true);
            // 设置WebView支持多窗口
            settings.supportMultipleWindows();
            // 设置WebView可触摸放大缩小
            //settings.setBuiltInZoomControls(true);
            // 设置允许访问文件
            settings.setAllowFileAccess(true);
            settings.setLoadWithOverviewMode(true);
            // 开启缓存功能
            settings.setAppCacheEnabled(false);
            // 开启 DOM storage API 功能
            settings.setDomStorageEnabled(true);
            // 设置数据库启用
            settings.setDatabaseEnabled(true);
            // 支持通过JS打开新窗口
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            // 提高渲染的优先级
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            // 启用地理定位
            settings.setGeolocationEnabled(true);

            // 用户调试模式
            settings.setSupportMultipleWindows(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 用户调试模式
            *//*String cacheDirPath = mContext.getFilesDir().getAbsolutePath()+ "/webcache";
            //设置数据库缓存路径
            webView.getSettings().setDatabasePath(cacheDirPath);
            //设置  Application Caches 缓存目录
            webView.getSettings().setAppCachePath(cacheDirPath);
            //开启 Application Caches 功能
            webView.getSettings().setAppCacheEnabled(true);*//*
            settings.setAllowUniversalAccessFromFileURLs(true);*/
            webView.setWebViewClient(new WebViewClient() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
        }
    }
}
