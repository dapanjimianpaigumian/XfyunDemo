package com.yulu.zhaoxinpeng.xfyundemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 实现语音识别功能
 */
public class RecoActivity extends AppCompatActivity {

    @BindView(R.id.button_start)
    Button mBtnStartReco;
    @BindView(R.id.tv_show)
    TextView mTvShow;
    @BindView(R.id.button_push_contacts)
    Button mBtnPushContacts;

    private SpeechRecognizer mSpeechRecognizer;

    // 用于处理结果
    private HashMap<String, String> mResults = new LinkedHashMap<>();
    private RecognizerDialog mRecognizerDialog;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);
        bind = ButterKnife.bind(this);

        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(this, null);//mRecognizerListener

        //1.创建RecognizerDialog对象，第二个参数：本地听写时传InitListener
        mRecognizerDialog = new RecognizerDialog(this, null);

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
            Toast.makeText(RecoActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
        }

        // 结束录音
        @Override
        public void onEndOfSpeech() {
            Toast.makeText(RecoActivity.this, "结束录音", Toast.LENGTH_SHORT).show();
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

    // 处理结果
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

    @OnClick({R.id.button_start, R.id.button_push_contacts})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.button_start:
                // 开始进行录音和转换
                //mSpeechRecognizer.startListening(mRecognizerListener);

                mTvShow.setText(null);
                mResults.clear();
                setParameter();
                // 带UI效果的
                mRecognizerDialog.setListener(dialogListener);
                mRecognizerDialog.show();
                break;
            case R.id.button_push_contacts:
                upContacts();
                break;

        }

    }

    //---------------------------------上传联系人-----------------------------------------
    // 上传联系人
    private void upContacts() {
        //获取ContactManager实例化对象
        //异步查询联系人接口，通过onContactQueryFinish接口回调
        // 获取联系人监听器。
        // 上传联系人监听器。
        ContactManager contactManager = ContactManager.createManager(this, mContactListener);
        // 异步查询
        contactManager.asyncQueryAllContactsName();
    }

    // 联系人获取监听
    private ContactManager.ContactListener mContactListener = new ContactManager.ContactListener() {

        // 当获取结束的时候
        @Override
        public void onContactQueryFinish(String s, boolean b) {
            //指定引擎类型
            mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mSpeechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");

            // 重点的：更新词典
            int contact = mSpeechRecognizer.updateLexicon("contact", s, mLexiconListener);
            if (contact != ErrorCode.SUCCESS) {
                Log.i("TAG", "更新失败了" + contact);
            }
        }
    };

    // 上传联系人(更新词表的)监听
    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String s, SpeechError speechError) {
            if (speechError != null) {
                Log.i("TAG", "更新错误" + speechError.toString());
            } else {
                Log.i("TAG", "上传成功了");
            }
        }
    };

    //-------------------------------------UI听写的监听----------------------------------------
    // UI听写的监听
    private RecognizerDialogListener dialogListener = new RecognizerDialogListener() {

        // 拿到结果
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.i("TAG", recognizerResult.getResultString());
            printResult(recognizerResult);
        }

        // 错误的时候
        @Override
        public void onError(SpeechError speechError) {
            Toast.makeText(RecoActivity.this, speechError.getPlainDescription(true), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
