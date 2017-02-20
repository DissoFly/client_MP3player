package com.example.mp3player.main;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.inputcells.AvatarView;
import com.example.mp3player.login.LoginActivity;
import com.example.mp3player.login.MyDataActivity;
import com.example.mp3player.service.LoginService;

/**
 * Created by DissoCapB on 2017/2/18.
 */

public class LeftDrawerHeadMessageFragment extends Fragment implements View.OnClickListener{
    View view;
    TextView headAccount;
    AvatarView headAvatar;
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
            getActivity().bindService(new Intent(getActivity(),LoginService.class), connection, Context.BIND_AUTO_CREATE);
            headAccount=(TextView)view.findViewById(R.id.text_head_account);
            headAvatar=(AvatarView)view.findViewById(R.id.head_avatar);
            headAccount.setText("正在登录");
            initData();
        }
        return view;
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
                        headAvatar.load(messenger.getUser());
                        statue=CONNECT_SUCCESS;
                    } else {
                        headAccount.setText(messenger.getConnectResult());
                        statue=CONNECT_FAIL;
                        headAvatar.load((String) null);
                    }
                    isChanges=true;
                }
            }
            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };
}
