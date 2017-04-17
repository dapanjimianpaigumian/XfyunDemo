package com.yulu.zhaoxinpeng.xfyundemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实现语音识别功能
 */
public class RecoActivity extends AppCompatActivity {

    @BindView(R.id.button_start)
    Button mBtnStartReco;
    @BindView(R.id.tv_show)
    TextView mTvShow;

    private SpeechRecognizer mSpeechRecognizer;

    // 用于处理结果
    private HashMap<String, String> mResults = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);
        ButterKnife.bind(this);

        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(this, null);

        // 2. 设置SDK参数
        setParameter();
    }

    private void setParameter() {
        // 清空参数
        mSpeechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎：云端（在线）
        mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

        // 设置听写的语言
        mSpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言类型：中文
        mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 标点:0无标点，1有
        mSpeechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置语音前端点:用户多久没有说话，当超时处理
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, "3000");

        // 设置语音后端点：用户停止说话多久自动录音停止
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, "2000");
    }

    //设置监听器
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        // 声音变化的时候
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        // 开始录音
        @Override
        public void onBeginOfSpeech() {

        }

        // 结束录音
        @Override
        public void onEndOfSpeech() {

        }

        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.i("TAG", recognizerResult.getResultString());
            printResult(recognizerResult);
        }

        // 会话发生错误的时候
        @Override
        public void onError(SpeechError speechError) {

        }

        // 扩展用的接口
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mResults.keySet()) {
            resultBuffer.append(mResults.get(key));
        }

        mTvShow.setText(resultBuffer.toString());
    }

    @OnClick(R.id.button_start)
    public void onViewClicked() {
        // 开始进行录音和转换
        mSpeechRecognizer.startListening(mRecognizerListener);
    }
}
