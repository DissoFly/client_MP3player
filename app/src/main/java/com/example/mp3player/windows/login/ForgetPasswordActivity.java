package com.example.mp3player.windows.login;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mp3player.MD5;
import com.example.mp3player.R;
import com.example.mp3player.service.HttpService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class ForgetPasswordActivity extends Activity implements View.OnClickListener{

    EditText accountEdit;
    EditText phoneNumberEdit;
    EditText passwordEdit;
    EditText passwordConfirmEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forget_password);
        accountEdit=(EditText)findViewById(R.id.edit_account) ;
        phoneNumberEdit=(EditText)findViewById(R.id.edit_phonenumber) ;
        passwordEdit=(EditText)findViewById(R.id.edit_password) ;
        passwordConfirmEdit=(EditText)findViewById(R.id.edit_password_confirm) ;
        initData();
    }

    private void initData() {
        findViewById(R.id.btn_forget_password_back).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forget_password_back:
                onBackPressed();
            case R.id.btn_confirm:
                forgetPassword();

            default:
                    break;
        }
    }

    private void forgetPassword() {
        String account=accountEdit.getText().toString();
        String password=passwordEdit.getText().toString();
        String passwordConfirm=passwordConfirmEdit.getText().toString();
        long phoneNumber=0;
        List<String> errors=new ArrayList<>();
        //未解决其他符号问题
        if (account.equals("")){
            errors.add("请输入账号");
        }
        try {//未验证11位数的手机号
            phoneNumber= Long.parseLong(phoneNumberEdit.getText().toString());
        }catch (Exception e){
            errors.add("请输入正确的手机号");
        }
        if (password.equals("")){
            errors.add("请输入密码");
        }else if (password.length()<6){
            errors.add("密码长度必须大于6位");
        }else if (passwordConfirm.equals("")){
            errors.add("请输入确认密码");
        }else if (!password.equals(passwordConfirm)){
            errors.add("两次密码输入不一致");
        }
        String errorOutput="";
        for (String error:errors){
            errorOutput=errorOutput.concat("·"+error+"\n");
        }
        if (errorOutput.equals("")) {
            connectToHttp(account,phoneNumber,password);
        }
        else {
            Toast.makeText(getApplication(), errorOutput, Toast.LENGTH_LONG).show();
        }

    }

    private void connectToHttp(String account,long phoneNumber,String password){
        String meid=((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        RequestBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("passwordHash", MD5.getMD5(password))
                .add("phoneNumber", String.valueOf(phoneNumber))
                .build();
        Request request= HttpService.requestBuilderWithPath("forget_password").post(formBody).build();
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
                                case "SUCCESS":
                                    Toast.makeText(getApplication(),"成功修改密码",Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                    break;
                                case "FAIL_WITH_NO_ACCOUNT":
                                    Toast.makeText(getApplication(),"此用户名不存在",Toast.LENGTH_LONG).show();
                                    break;
                                case "FAIL_WITH_WRONG_PHONENUMBER":
                                    Toast.makeText(getApplication(),"手机号错误",Toast.LENGTH_LONG).show();
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

}