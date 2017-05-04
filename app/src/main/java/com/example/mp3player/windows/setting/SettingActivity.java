package com.example.mp3player.windows.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mp3player.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static com.example.mp3player.service.HttpService.serverAddressChanges;

/**
 * Created by DissoCapB on 2017/5/4.
 */

public class SettingActivity extends Activity implements View.OnClickListener{

    String networkAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initData();
    }


    private void initData() {
        findViewById(R.id.btn_setting_back).setOnClickListener(this);
        findViewById(R.id.btn_net_setting).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent itnt;
        switch (view.getId()){
            case R.id.btn_setting_back:
                onBackPressed();
                break;
            case R.id.btn_net_setting:
                load();
                EditText eText = new EditText(this);
                eText.setText(networkAddress);
                final EditText editText = eText;
                new AlertDialog.Builder(this)
                        .setTitle("请输入服务器地址")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = editText.getText().toString();
                                if (text.equals("")) {
                                    Toast.makeText(SettingActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    networkAddress=text;
                                    save();
                                    serverAddressChanges();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.btn_about:

                break;
            default:
                break;
        }
    }

    /////////////////////////////////本地文件路径储存↓///////////////////////////////////////

    public void save() {                            //写入文件
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("networkAddressData", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(networkAddress);
            Toast.makeText(SettingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(SettingActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        networkAddress="192.168.253.3:8080";
        try {
            in = openFileInput("networkAddressData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            networkAddress = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
