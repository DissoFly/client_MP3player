package com.example.mp3player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.mp3player.entity.AutoLoginSign;
import com.example.mp3player.entity.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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

    Boolean isFinishConnect=false;
    boolean isGetFinishConnect=false;
    String connectResult="";
    AutoLoginSign readSign = null;
    User user=null;
    private final IBinder binder = new ServicesBinder();

    //
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("------------LoginService Building--------------------");
        login();
        return binder;
    }

    public class ServicesBinder extends Binder {
        public LoginService getService() {
            return LoginService.this;
        }
    }

    public void login(){
        isFinishConnect=false;
        isGetFinishConnect=false;
        load();
        if (readSign == null) {
            connectResult="请登录";
            user=null;
            isFinishConnect = true;
            Toast.makeText(getApplication(), "无sign", Toast.LENGTH_SHORT).show();//无sign
        }else
            connect();
    }

    public void logout(){
        isFinishConnect=false;
        isGetFinishConnect=false;
        connectResult="FILE_WITH_LOGOUT";
        //退出登录connect
        user=null;
        cleanSign();
        isFinishConnect = true;
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
            if(content.toString().endsWith("}"))
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
                isFinishConnect=true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();
                    getRequest(data);

                } catch (Exception e) {
                    System.out.println(e);
                }finally {
                    isFinishConnect=true;
                }
            }
        });
    }

    public void getRequest(final String data) {
        if (data.endsWith("}")) {
            System.out.println("可以自動登陸");
            System.out.println(data);
            user=new Gson().fromJson(data,User.class);
            connectResult="SUCCESS_IN_AUTOLOGIN";
        } else {
            switch (data) {
                case "FAIL_WITH_NO_SIGN":
                    System.out.println("需要重新登录");
                    connectResult="FAIL_WITH_NO_SIGN";
                    break;
                case "FAIL_WITH_LOGIN_OUTTIME":
                    System.out.println("长时间未手动登录");
                    connectResult="FAIL_WITH_LOGIN_OUTTIME";
                    break;
                case "FAIL_WITH_DIFFERENT_SIGN":
                    System.out.println("在其他地方登录过，需要重新登录");
                    connectResult="FAIL_WITH_DIFFERENT_SIGN";
                    break;
                default:
                    System.out.println("未知错误，请重新登录");
                    connectResult="FAIL_WITH_UNKNOW_WRONG";
                    break;
            }
            user=null;
            cleanSign();
        }

    }

    public Boolean getFinishConnect() {
        if(isGetFinishConnect&&isFinishConnect)
            return true;
        else {
            isGetFinishConnect=isFinishConnect;
            return false;
        }
    }

    public String getConnectResult(){
        return  connectResult;
    }

    public User getUser() {
        if(connectResult.equals("SUCCESS_IN_AUTOLOGIN"))
            return user;
        else
            return null;
    }

    public void cleanSign(){
        //清空sign
        BufferedWriter writer=null;
        try{
            FileOutputStream out=openFileOutput("autoLoginSign", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write("");
            writer.newLine();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(writer!=null){
                    writer.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        System.out.println("-----------------clean sign------------------");
    }
}
