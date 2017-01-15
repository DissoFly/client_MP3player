package com.example.mp3player.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mp3player.MainActivity;
import com.example.mp3player.R;

/**
 * Created by DissoCapB on 2017/1/15.
 */

public class LoginActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_login);
        initData();
    }

    private void initData() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_password_forget).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent itnt;
        switch (view.getId()){
            case R.id.btn_login:
                //登录
                itnt=new Intent(this, MainActivity.class);
                startActivity(itnt);
                break;
            case R.id.btn_register:
                //注册
                itnt=new Intent(this, RegisterActivity.class);
                startActivity(itnt);
                break;
            case R.id.btn_password_forget:
                //忘记密码
                itnt=new Intent(this, ForgetPasswordActivity.class);
                startActivity(itnt);
                break;
            default:
                break;
        }
    }


}
