package com.example.mp3player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.example.mp3player.service.DataService;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.service.MusicPlayerService;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class BootActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
//        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(BootActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
//        findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(BootActivity.this, MainActivity.class);
//                startActivity(intent);
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true){
                    bindService(new Intent(BootActivity.this,LoginService.class), connection, Context.BIND_AUTO_CREATE);
                    Intent intent=new Intent(BootActivity.this, MainActivity.class);
                    startActivity(intent);//直接登录，跳转到主页面
                }else{
                    //登录界面
                }
            }
        },1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("------------LoginService and MusicPlayerService--------------------");
        DataService dataService= OpenHelperManager.getHelper(BootActivity.this,DataService.class);
        startService(new Intent(this, MusicPlayerService.class));
        startService(new Intent(this, LoginService.class));
    }

    LoginService messenger;
    boolean bound;

    private ServiceConnection connection = new ServiceConnection() {				//判断有没有绑定Service

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定后可调用MusicPlayerService的方法来达到控制
            messenger=((LoginService.ServicesBinder) service).getService();
            bound=true;
        }
    };

}
