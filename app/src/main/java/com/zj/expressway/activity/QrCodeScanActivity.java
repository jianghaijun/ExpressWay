package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.ToastUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.hutool.core.util.StrUtil;


/**
 * Created by HaiJun on 2018/6/11 16:32
 */
public class QrCodeScanActivity extends BaseActivity implements QRCodeView.Delegate {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.zxView)
    private ZXingView zxView;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qr_code_scan);
        x.view().inject(this);
        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        BGAQRCodeUtil.setDebug(false);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("二维码扫描");

        imgBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        zxView.setDelegate(this);

        if (Build.VERSION.SDK_INT >= 23) {
            requestAuthority(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void agree() {
                    zxView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
                    zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                }

                @Override
                public void refuse(List<String> refusePermission) {
                    ToastUtil.showLong(mContext, "您已拒绝使用相机功能!");
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        zxView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
    }

    @Override
    protected void onStop() {
        zxView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (StrUtil.containsAny(result, "qrcode/")) {
            Intent intent = new Intent(mContext, IDNumberActivity.class);
            intent.putExtra("url", result);
            startActivity(intent);
        } else {
            Uri uri = Uri.parse(result);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            this.finish();
        }
        //zxView.startSpot(); // 延迟0.5秒后开始识别
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        ToastUtil.showLong(mContext, "打开相机出错!");
    }

    /*public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_preview:
                zxView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
                break;
            case R.id.stop_preview:
                zxView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
                break;
            case R.id.start_spot:
                zxView.startSpot(); // 延迟0.5秒后开始识别
                break;
            case R.id.stop_spot:
                zxView.stopSpot(); // 停止识别
                break;
            case R.id.start_spot_showrect:
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.stop_spot_hiddenrect:
                zxView.stopSpotAndHiddenRect(); // 停止识别，并且隐藏扫描框
                break;
            case R.id.show_scan_rect:
                zxView.showScanRect(); // 显示扫描框
                break;
            case R.id.hidden_scan_rect:
                zxView.hiddenScanRect(); // 隐藏扫描框
                break;
            case R.id.decode_scan_box_area:
                zxView.getScanBoxView().setOnlyDecodeScanBoxArea(true); // 仅识别扫描框中的码
                break;
            case R.id.decode_full_screen_area:
                zxView.getScanBoxView().setOnlyDecodeScanBoxArea(false); // 识别整个屏幕中的码
                break;
            case R.id.open_flashlight:
                zxView.openFlashlight(); // 打开闪光灯
                break;
            case R.id.close_flashlight:
                zxView.closeFlashlight(); // 关闭闪光灯
                break;
            case R.id.scan_one_dimension:
                zxView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
                zxView.setType(BarcodeType.ONE_DIMENSION, null); // 只识别一维条码
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_two_dimension:
                zxView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
                zxView.setType(BarcodeType.TWO_DIMENSION, null); // 只识别二维条码
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_qr_code:
                zxView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
                zxView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_code128:
                zxView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
                zxView.setType(BarcodeType.ONLY_CODE_128, null); // 只识别 CODE_128
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_ean13:
                zxView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
                zxView.setType(BarcodeType.ONLY_EAN_13, null); // 只识别 EAN_13
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_high_frequency:
                zxView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
                zxView.setType(BarcodeType.HIGH_FREQUENCY, null); // 只识别高频率格式，包括 QR_CODE、EAN_13、CODE_128
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_all:
                zxView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
                zxView.setType(BarcodeType.ALL, null); // 识别所有类型的码
                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.scan_custom:
                zxView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式

                Map<DecodeHintType, Object> hintMap = new EnumMap<>(DecodeHintType.class);
                List<BarcodeFormat> formatList = new ArrayList<>();
                formatList.add(BarcodeFormat.QR_CODE);
                formatList.add(BarcodeFormat.EAN_13);
                formatList.add(BarcodeFormat.CODE_128);
                hintMap.put(DecodeHintType.POSSIBLE_FORMATS, formatList); // 可能的编码格式
                hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
                hintMap.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 编码字符集
                zxView.setType(BarcodeType.CUSTOM, hintMap); // 自定义识别的类型

                zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
                break;
            case R.id.choose_qrcde_from_gallery:
                *//*
                从相册选取二维码图片，这里为了方便演示，使用的是
                https://github.com/bingoogolapple/BGAPhotoPicker-Android
                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
                 *//*
                Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                        .cameraFileDir(null)
                        .maxChooseCount(1)
                        .selectedPhotos(null)
                        .pauseOnScroll(false)
                        .build();
                startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                break;
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        zxView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
        if (resultCode == Activity.RESULT_OK && requestCode == 666) {
            //final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
            // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
            //zxView.decodeQRCode(picturePath);

            /*
            没有用到 QRCodeView 时可以调用 QRCodeDecoder 的 syncDecodeQRCode 方法
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
            .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(TestScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(TestScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
        }
    }

    @Override
    protected void onDestroy() {
        zxView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        zxView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
