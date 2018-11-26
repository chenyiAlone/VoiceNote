package com.chenyialone.voicewritet.util;

/**
 * 封装的语音合成类
 * Created by chenyiAlone on 2018/5/20.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;


public class Speaker {

    // 上下文
    private Context mContext;
    // 语音合成对象
    public static SpeechSynthesizer mspeechSynthesizer;
    // Tosat对象
    private Toast toast;
    // Log标签
    private static final String TAG = "Speaker";
    // 发音人
    public final static String[] COLOUD_VOICERS_VALUE = {"xiaoyan", "xiaoyu", "catherine", "henry", "vimary", "vixy", "xiaoqi", "vixf", "xiaomei",
            "xiaolin", "xiaorong", "xiaoqian", "xiaokun", "xiaoqiang", "vixying", "xiaoxin", "nannan", "vils",};


    /**
     * Speaker 的构造方法
     * @param context
     */
    public Speaker(Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=5addc52a");
        // 上下文
        mContext = context;
        // Toast的初始化
        toast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
        // 初始化合成对象
        mspeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Log.d(TAG, "初始化失败,错误码：" + code);
                }
                Log.d(TAG, "初始化失败,q错误码：" + code);
            }
        });
    }


    /**
     * 语音合成方法
     * @param text
     */
    public void speak(String text) {
        // 非空判断
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int code = mspeechSynthesizer.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                showTip("没有安装语音+ code = " + code);
            } else {
                showTip("语音合成失败,错误码: " + code);
            }
        }
    }

    /**
     * 停止语音合成
     */
    public static void stopSpeaking() {
        // 对象非空并且正在说话
        if (null != mspeechSynthesizer && mspeechSynthesizer.isSpeaking()) {
            // 停止说话
            mspeechSynthesizer.stopSpeaking();
        }
    }

    /**
     * 提醒显示str的内容
     * @param str   要显示的内容
     */
    public void showTip(String str){
        toast.setText(str);
        toast.show();
    }

    // 语音合成的回调对象
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {}

        @Override
        public void onSpeakPaused() {}

        @Override
        public void onSpeakResumed() {}

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // TODO 缓冲的进度
            Log.i(TAG, "缓冲 : " + percent);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // TODO 说话的进度
            Log.i(TAG, "合成 : " + percent);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.i(TAG, "播放完成");

            } else if (error != null) {
                showTip(error.getPlainDescription(true));
                Log.i(TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    /**
     * 语音合成器参数设置
     */
    private void setParam() {
        // 清空参数
        mspeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        // 引擎类型 网络
        mspeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置发音人
        mspeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, COLOUD_VOICERS_VALUE[0]);
        // 设置语速
        mspeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        // 设置音调
        mspeechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
        // 设置音量
        mspeechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
        // 设置播放器音频流类型
        mspeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/KRobot/wavaudio.pcm");
        // 背景音乐  1有 0 无
        // mTts.setParameter("bgs", "1");
    }
}