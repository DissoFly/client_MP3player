package com.example.mp3player.windows.login;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mp3player.MD5;
import com.example.mp3player.R;
import com.example.mp3player.entity.AutoLoginSign;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/1/15.
 */

public class LoginActivity extends Activity implements View.OnClickListener{

    EditText accountEdit;
    EditText passwordEdit;
    String account;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_login);
        accountEdit=(EditText)findViewById(R.id.edit_account);
        passwordEdit=(EditText)findViewById(R.id.edit_password);
        initData();
        bindService(new Intent(this,LoginService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private void initData() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_login_back).setOnClickListener(this);
//        findViewById(R.id.btn_password_forget).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent itnt;
        switch (view.getId()){
            case R.id.btn_login_back:
                onBackPressed();
                break;
            case R.id.btn_login:
                //登录
                account=accountEdit.getText().toString();
                password=passwordEdit.getText().toString();
                if(!account.equals("")&&!password.equals(""))
                    connectToHttp();
                else
                    Toast.makeText(getApplication(),"请输入账户名或密码！",Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_register:
                //注册
                itnt=new Intent(this, RegisterActivity.class);
                startActivity(itnt);
                break;
//            case R.id.btn_password_forget:
//                //忘记密码
//                itnt=new Intent(this, ForgetPasswordActivity.class);
//                startActivity(itnt);
//                break;
            default:
                break;
        }
    }

    private void connectToHttp(){
        String meid=((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        System.out.println("号码："+meid);
        final String sign=MD5.getMD5(meid+account+password);
        RequestBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("passwordHash", MD5.getMD5(password))
                .add("meid",sign)
                .build();
        Request request= HttpService.requestBuilderWithPath("login").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (data) {
                                case "SUCCESS_IN_LOGIN":
                                    AutoLoginSign loginSign=new AutoLoginSign();
                                    loginSign.setAccount(account);
                                    loginSign.setSign(sign);
                                    save(loginSign);
                                    Toast.makeText(getApplication(),"欢迎，"+account,Toast.LENGTH_LONG).show();
                                    messenger.login();
                                    finish();
                                    break;
                                case "FAIL_WITH_NO_ACCOUNT":
                                    Toast.makeText(getApplication(),"此用户名未注册",Toast.LENGTH_LONG).show();
                                    break;
                                case "FAIL_WITH_WRONG_PASSWORD":
                                    Toast.makeText(getApplication(),"密码错误",Toast.LENGTH_LONG).show();
                                    break;
                                case "FAIL_WITH_FREEZE":
                                    Toast.makeText(getApplication()," 已被冻结 ",Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplication(), data, Toast.LENGTH_LONG).show();
                                    System.out.println(data);
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void save(AutoLoginSign loginSign){							//写入文件
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("autoLoginSign", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(new Gson().toJson(loginSign));
            System.out.println(new Gson().toJson(loginSign));
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
    }

    LoginService messenger;
    boolean bound;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger=((LoginService.ServicesBinder) service).getService();
            bound=true;
        }
    };

}
