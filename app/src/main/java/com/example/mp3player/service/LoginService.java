package com.example.mp3player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.mp3player.entity.AutoLoginSign;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/2/18.
 */

public class LoginService extends Service {

    AutoLoginSign readSign = null;
    private final IBinder binder = new ServicesBinder();

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("------------LoginService Building--------------------");
        load();
        if (readSign == null)
            Toast.makeText(getApplication(), "无sign", Toast.LENGTH_SHORT).show();//无sign
        else
            connect();

        return binder;
    }

    public class ServicesBinder extends Binder {
        public LoginService getService() {
            return LoginService.this;
        }
    }


    public void load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("autoLoginSign");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            System.out.println("读取:" + content.toString());
            readSign = new Gson().fromJson(content.toString(), AutoLoginSign.class);


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取失败：可能没sign");
        }
    }

    public void connect() {
        RequestBody formBody = new FormBody.Builder()
                .add("account", readSign.getAccount())
                .add("meid", readSign.getSign())
                .build();
        Request request = HttpService.requestBuilderWithPath("autoLogin").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();
                    System.out.println(data);
                    getRequest(data);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    public void getRequest(final String data) {
        if (data.endsWith("}")) {
            System.out.println("可以自動登陸");
        } else {
            switch (data) {
                case "FAIL_WITH_NO_SIGN":
                    System.out.println("需要重新登录");
                    break;
                case "FAIL_WITH_LOGIN_OUTTIME":
                    System.out.println("长时间未手动登录");
                    break;
                case "FAIL_WITH_DIFFERENT_SIGN":
                    System.out.println("在其他地方登录过，需要重新登录");
                    break;
                default:
                    System.out.println("未知错误，请重新登录");
                    break;
            }
        }
    }
}
