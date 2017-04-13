package com.example.mp3player.windows.login;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.windows.inputcells.PictureInputCellFragment;

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
 * Created by DissoCapB on 2017/4/13.
 */

public class ChangeActivity extends Activity implements View.OnClickListener {
    EditText emailEdit;
    EditText phoneNumberEdit;
    PictureInputCellFragment fragInputAvatar;
    String email;
    long phoneNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        emailEdit = (EditText) findViewById(R.id.register_edit_email);
        phoneNumberEdit = (EditText) findViewById(R.id.register_edit_phonenumber);
        fragInputAvatar = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.register_avatar);

        bindService(new Intent(this, LoginService.class), connection, Context.BIND_AUTO_CREATE);
        initData();
    }

    private void initData() {
        findViewById(R.id.btn_change_confirm).setOnClickListener(this);
        findViewById(R.id.btn_change_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_confirm:
                change();
                break;
            case R.id.btn_change_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    void change() {
        email = emailEdit.getText().toString();
        List<String> errors = new ArrayList<>();
        try {//未验证11位数的手机号
            phoneNumber = Long.parseLong(phoneNumberEdit.getText().toString());
        } catch (Exception e) {
            errors.add("请输入正确的手机号");
        }
        //未解决其他符号问题
        String errorOutput = "";
        for (String error : errors) {
            errorOutput = errorOutput.concat("·" + error + "\n");
        }
        if (errorOutput.equals("")) {
            connectToHttp();
        } else {
            Toast.makeText(getApplication(), errorOutput, Toast.LENGTH_LONG).show();
        }
    }

    void setMessenge() {
        emailEdit.setText(messenger.getUser().getEmail());
        phoneNumberEdit.setText(messenger.getUser().getPhoneNumber() + "");
    }

    private void connectToHttp() {
        MultipartBody.Builder formBody = new MultipartBody.Builder()
                .addFormDataPart("userId", messenger.getUser().getUserId() + "")
                .addFormDataPart("email", email)
                .addFormDataPart("phoneNumber", String.valueOf(phoneNumber));

        if (fragInputAvatar.getPngData() != null) {
            formBody
                    .addFormDataPart(
                            "avatar", "avatar",
                            RequestBody
                                    .create(MediaType.parse("image/png"),
                                            fragInputAvatar.getPngData()));
        }
        Request request = HttpService.requestBuilderWithPath("userChange").post(formBody.build()).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data.equals("SUCCESS_IN_USERCHANGE")) {
                                Toast.makeText(getApplication(), "修改个人信息成功!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                System.out.println(data);
                                Toast.makeText(getApplication(), "fail", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplication(), "fail", Toast.LENGTH_LONG).show();
                }
            }
        });
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
