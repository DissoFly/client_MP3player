package com.example.mp3player.windows.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mp3player.MD5;
import com.example.mp3player.R;
import com.example.mp3player.windows.inputcells.PictureInputCellFragment;
import com.example.mp3player.service.HttpService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class RegisterActivity extends Activity implements View.OnClickListener{

    EditText accountEdit;
    EditText passwordEdit;
    EditText passwordConfirmEdit;
    EditText emailEdit;
    EditText phoneNumberEdit;
    String account;
    String password;
    String passwordConfirm;
    String email;
    long phoneNumber;

    PictureInputCellFragment fragInputAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        accountEdit=(EditText)findViewById(R.id.register_edit_account);
        passwordEdit=(EditText)findViewById(R.id.register_edit_password);
        passwordConfirmEdit=(EditText)findViewById(R.id.register_edit_password_confirm);
        emailEdit=(EditText)findViewById(R.id.register_edit_email);
        phoneNumberEdit=(EditText)findViewById(R.id.register_edit_phonenumber);
        fragInputAvatar=(PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.register_avatar);
        initData();
    }

    private void initData() {
        findViewById(R.id.btn_register_confirm).setOnClickListener(this);
        findViewById(R.id.btn_local_music_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register_confirm:
                register();
                break;
            case R.id.btn_local_music_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void register(){
        account=accountEdit.getText().toString();
        password=passwordEdit.getText().toString();
        passwordConfirm=passwordConfirmEdit.getText().toString();
        email=emailEdit.getText().toString();
        List<String> errors=new ArrayList<>();
        try {//未验证11位数的手机号
            phoneNumber= Long.parseLong(phoneNumberEdit.getText().toString());
        }catch (Exception e){
            errors.add("请输入正确的手机号");
        }
        //未解决其他符号问题
        if (account.equals("")){
            errors.add("请输入账号");
        }
        if (password.equals("")){
            errors.add("请输入密码");
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
            connectToHttp();
        }
        else {
            Toast.makeText(getApplication(), errorOutput, Toast.LENGTH_LONG).show();
        }


    }


    private void connectToHttp(){
        MultipartBody.Builder formBody = new MultipartBody.Builder()
                .addFormDataPart("account", account)
                .addFormDataPart("passwordHash", MD5.getMD5(password))
                .addFormDataPart("email", email)
                .addFormDataPart("phoneNumber", String.valueOf(phoneNumber))

                ;
        if (fragInputAvatar.getPngData() != null) {
            formBody
                    .addFormDataPart(
                            "avatar", "avatar",
                            RequestBody
                                    .create(MediaType.parse("image/png"),
                                            fragInputAvatar.getPngData()));
        }
        Request request=HttpService.requestBuilderWithPath("register").post(formBody.build()).build();
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
                            if(data.equals("SUCCESS_IN_REGISTER")) {
                                Toast.makeText(getApplication(), "success register!", Toast.LENGTH_LONG).show();
                                finish();
                            }else
                                Toast.makeText(getApplication(),data,Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }
}