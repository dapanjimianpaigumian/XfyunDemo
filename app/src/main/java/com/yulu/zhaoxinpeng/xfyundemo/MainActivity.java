package com.yulu.zhaoxinpeng.xfyundemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.button_Reco, R.id.button_Tran})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_Reco:
                startActivity(new Intent(MainActivity.this,RecoActivity.class));
                break;
            case R.id.button_Tran:
                startActivity(new Intent(MainActivity.this,TranActivity.class));
                break;
        }
    }
}
