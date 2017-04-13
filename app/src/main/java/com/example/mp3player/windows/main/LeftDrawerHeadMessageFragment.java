package com.example.mp3player.windows.main;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.windows.login.LoginActivity;
import com.example.mp3player.windows.login.MyDataActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/2/18.
 */

public class LeftDrawerHeadMessageFragment extends Fragment implements View.OnClickListener{
    View view;
    TextView headAccount;
    ImageView headAvatar;
    final int REFLASH_TIME=200;
    final int CONNECTING=10;
    final int CONNECT_SUCCESS=11;
    final int CONNECT_FAIL=12;
    int statue;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            statue=CONNECTING;
            view = inflater.inflate(R.layout.fragment_main_leftdrawer_head_message, null);
            headAccount=(TextView)view.findViewById(R.id.text_head_account);
            headAvatar=(ImageView)view.findViewById(R.id.head_avatar);
            headAccount.setText("正在登录");
            initData();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(),LoginService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private void initData() {
        view.findViewById(R.id.head_message).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_message:
                switch (statue){
                    case CONNECTING:
                        Toast.makeText(getActivity().getApplicationContext(),"正在连接。。。。",Toast.LENGTH_SHORT).show();
                        break;
                    case CONNECT_SUCCESS:
                        Intent itnt=new Intent(getActivity(), MyDataActivity.class);
                        startActivity(itnt);
                        break;
                    case CONNECT_FAIL:
                        Intent intent=new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
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
            handler.postDelayed(runnable, REFLASH_TIME);
            bound=true;
        }
    };
    Boolean isChanges=false;
    Handler handler =new Handler();
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            if (!messenger.getFinishConnect()) {
                isChanges=false;
                statue=CONNECTING;
            }else{
                if (!isChanges) {
                    if (messenger.getConnectResult().equals("SUCCESS_IN_AUTOLOGIN")) {
                        headAccount.setText("你好，" + messenger.getUser().getAccount());
                        userAvatarConnect(messenger.getUser().getUserId());
                        statue=CONNECT_SUCCESS;
                    } else {
                        headAccount.setText(messenger.getConnectResult());
                        statue=CONNECT_FAIL;
                        setAvatarNull();
                    }
                    isChanges=true;
                }
            }
            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };
    public void userAvatarConnect(int userId) {
        OkHttpClient client = HttpService.getClient();

        Request request = HttpService.requestBuilderWithPath("avatar/" + userId).get().build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try {
                    byte[] bytes = arg1.body().bytes();
                    final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            headAvatar.setImageBitmap(bmp);
                        }
                    });
                } catch (Exception ex) {
                    setAvatarNull();
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                setAvatarNull();
            }
        });
    }

    void setAvatarNull(){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    headAvatar.setImageResource(R.mipmap.user_null);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
