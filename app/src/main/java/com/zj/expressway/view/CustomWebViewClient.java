package com.zj.expressway.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebViewClient
 *
 * @author JiangHaiJun
 * @date 2016-1-19
 */
public class CustomWebViewClient extends WebViewClient {
    private int onPageStartedCount;
    private int onPageFinishedCount;
    private int onReceivedErrorCount;
    public WebViewClientListener mListener;
    private Context context;

    public interface WebViewClientListener {
        public void onPageStared(WebView view, String url, Bitmap favicon);

        public void onPageFinished(WebView view, String url);

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error);
    }

    public CustomWebViewClient(WebViewClientListener mListener, Context context) {
        super();
        this.mListener = mListener;
        this.context = context;
        onPageStartedCount = 0;
        onPageFinishedCount = 0;
        onReceivedErrorCount = 0;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel:")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else if(url.startsWith("http:") || url.startsWith("https:")) {
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (onPageStartedCount == 0) {
            super.onPageStarted(view, url, favicon);
            mListener.onPageStared(view, url, favicon);
            onPageStartedCount++;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        /*if (onPageFinishedCount == 0) {
            super.onPageFinished(view, url);
            String uuid = "";
            try {
                uuid = Utility.getUuid(context);
                // 1.宋雷.测试代码-打版请切换
                *//*用户heys*//*
                //uuid = "75ae1fec-0100-1000-e000-0009c0a85893";
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("platform", "oa");
                obj.put("AccessKey", "bmV1YW5kMDAwMTIwMjUxMjE1NDU1MzQ4MjA=");
                obj.put("uuid", uuid);
                obj.put("baseUrl", ConstantsUtil.BASE_SERVER_URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            url = url.replace("#refresh_section", "");
            mListener.onPageFinished(view, url);
            onPageFinishedCount++;
        }*/
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (onReceivedErrorCount == 0) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onReceivedError(view, errorCode, description, failingUrl);
            onReceivedErrorCount++;
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();  // 接受所有网站的证书
    }
}
