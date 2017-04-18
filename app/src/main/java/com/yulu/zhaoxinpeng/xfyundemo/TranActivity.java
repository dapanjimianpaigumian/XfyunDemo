package com.yulu.zhaoxinpeng.xfyundemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

//语音合成：文本转换为语音
public class TranActivity extends AppCompatActivity {

    @BindView(R.id.button_pause)
    Button mButtonPause;
    @BindView(R.id.button_Synthesizer)
    Button mButtonSynthesizer;
    @BindView(R.id.button_resume)
    Button mButtonResume;
    @BindView(R.id.button_stop)
    Button mButtonStop;
    private SpeechSynthesizer mSpeechSynthesizer;
    private Unbinder bind;

    /**
     * 1. 创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
     * 2. 合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
     * 3. 开始合成：合成的监听
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran);
        bind = ButterKnife.bind(this);

        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);

        setParameter();
    }

    // 设置SDK参数
    private void setParameter() {

        // 清除参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置发音人
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "yefang");

        // 语速
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");

        // 合成的语调、音量等
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");

        // 设置合成的引擎类型:云端
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

    }

    // 合成的监听器
    private SynthesizerListener mSynthesizerListener = new SynthesizerListener() {
        // 开始说话
        @Override
        public void onSpeakBegin() {
            Toast.makeText(TranActivity.this, "开始", Toast.LENGTH_SHORT).show();
        }

        // 缓冲的进度
        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        // 暂停说话
        @Override
        public void onSpeakPaused() {
            Toast.makeText(TranActivity.this, "暂停", Toast.LENGTH_SHORT).show();
        }

        //继续说话
        @Override
        public void onSpeakResumed() {
            Toast.makeText(TranActivity.this, "继续", Toast.LENGTH_SHORT).show();
        }

        // 合成进度
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        // 完成
        @Override
        public void onCompleted(SpeechError speechError) {
            Toast.makeText(TranActivity.this, "结束", Toast.LENGTH_SHORT).show();
        }

        // 会话事件
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    @OnClick({R.id.button_pause, R.id.button_Synthesizer, R.id.button_resume, R.id.button_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_pause:
                //暂停播放
                mSpeechSynthesizer.pauseSpeaking();
                break;
            case R.id.button_Synthesizer:
                //开始合成
                int code = mSpeechSynthesizer.startSpeaking(getString(R.string.words), mSynthesizerListener);
                if (code != ErrorCode.SUCCESS) {
                    Toast.makeText(this, "合成失败：" + code, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_resume:
                //继续播放
                mSpeechSynthesizer.resumeSpeaking();
                break;
            case R.id.button_stop:
                //停止播放
                mSpeechSynthesizer.stopSpeaking();
                mSpeechSynthesizer.destroy();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
