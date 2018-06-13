package com.zj.expressway.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.zj.expressway.R;

/**
 * 作者:
 * <p/>
 * 工号: ${USER_NAME}
 */
public class SoundUtils {
    //定义左右声道的音量大小
    public final static float LEFT_VOLUME = 1.0f;
    public final static float RIGHT_VOLUME = 1.0f;

    public final static void playerScanOkWav(Context context, int type){
        int sound = R.raw.camera;
        MediaPlayer mediaPlayer = MediaPlayer.create(context,sound);
        mediaPlayer.setVolume(LEFT_VOLUME, RIGHT_VOLUME);
        mediaPlayer.start();
    }
}
