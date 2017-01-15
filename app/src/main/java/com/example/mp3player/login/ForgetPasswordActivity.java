package com.example.mp3player.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mp3player.R;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class ForgetPasswordActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forget_password);
        initData();
    }

    private void initData() {
        // findViewById(R.id.btn_login).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent itnt;
        switch (view.getId()) {
            default:
                break;
        }
    }
}