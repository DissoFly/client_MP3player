package com.example.mp3player.windows.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.windows.inputcells.AvatarView;

/**
 * Created by DissoCapB on 2017/2/20.
 */

public class MyDataActivity extends Activity implements View.OnClickListener {
    TextView name;
    AvatarView headAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_my_data);
        name = (TextView) findViewById(R.id.text_zone_username);
        headAvatar = (AvatarView) findViewById(R.id.head_avatar);
        bindService(new Intent(this, LoginService.class), connection, Context.BIND_AUTO_CREATE);
        initData();
    }

    private void initData() {
        findViewById(R.id.btn_data_back).setOnClickListener(this);
        findViewById(R.id.btn_mydata_logout).setOnClickListener(this);
        findViewById(R.id.btn_mydata_changes).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_data_back:
                onBackPressed();
                break;
            case R.id.btn_mydata_changes:
                Intent itnt=new Intent(this, ChangeActivity.class);
                startActivity(itnt);
                break;
            case R.id.btn_mydata_logout:
                showNormalDialog();
                break;
            default:
                break;
        }
    }

    private void showNormalDialog() {

        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setTitle("注销");
        normalDialog.setMessage("是否确定注销?");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messenger.logout();
                finish();
            }
        });
        normalDialog.setNegativeButton("返回", null);
        normalDialog.show();
    }


    void setMessenge() {
        name.setText(messenger.getUser().getAccount());
        headAvatar.load(messenger.getUser().getUserId());
    }

    LoginService messenger;
    boolean bound;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = ((LoginService.ServicesBinder) service).getService();
            bound = true;
            setMessenge();
        }
    };
}
