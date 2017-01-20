package com.example.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mp3player.login.LoginActivity;
import com.example.mp3player.service.MusicPlayerService;

public class BootActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BootActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BootActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true){
                    //直接登录，跳转到主页面
                }else{
                    //登录界面
                }
            }
        },2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, MusicPlayerService.class));
    }

}
