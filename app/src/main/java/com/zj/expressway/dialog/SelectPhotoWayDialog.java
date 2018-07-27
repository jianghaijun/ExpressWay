package com.zj.expressway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.listener.PromptListener;

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
 *       Created by HaiJun on 2018/6/11 17:59
 *       选择照片方式dialog
 */
public class SelectPhotoWayDialog extends Dialog implements View.OnClickListener {
    private PromptListener selectListener;

    public SelectPhotoWayDialog(Context context, PromptListener selectListener) {
        super(context);
        this.selectListener = selectListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_photos_way);

        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        TextView txtAlbum = (TextView) findViewById(R.id.txtAlbum);
        TextView txtPhotograph = (TextView) findViewById(R.id.txtPhotograph);

        btnCancel.setOnClickListener(this);
        txtAlbum.setOnClickListener(this);
        txtPhotograph.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 取消
            case R.id.btnCancel:
                dismiss();
                break;
            // 拍照
            case R.id.txtPhotograph:
                dismiss();
                selectListener.returnTrueOrFalse(true);
                break;
            // 相册
            case R.id.txtAlbum:
                dismiss();
                selectListener.returnTrueOrFalse(false);
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
    }
}
