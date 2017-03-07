package com.example.mp3player.windows.main.page;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.MusicPlayerService;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class FriendsFragment extends Fragment implements View.OnClickListener{
    View view;
    TextView textView;
    Button button;
    Button button2;
    Button button3;
    Button button4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null)
            view=inflater.inflate(R.layout.fragment_main_page_friends,null);
        textView=(TextView)view.findViewById(R.id.textView);
        button=(Button)view.findViewById(R.id.btn_test);
        button2=(Button)view.findViewById(R.id.btn_test2);
        button3=(Button)view.findViewById(R.id.btn_test3);
        button4=(Button)view.findViewById(R.id.btn_test4);
        initData();
        getActivity().bindService(new Intent(getActivity(),MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
        return view;
    }

    private void initData() {
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    private List<String> audioList = null;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                //messenger.testConnect();
                break;
            case R.id.btn_test2:
                //messenger.testConnect2();
                break;
            case R.id.btn_test3:
                //messenger.testConnect3();
                break;
            case R.id.btn_test4:
                //messenger.testConnect4();
//                audioList=new ArrayList<>();
//                audioList.add(HttpService.serverAddress + "api/online_song/1");
//                audioList.add(HttpService.serverAddress + "api/online_song/2");
//                audioList.add(HttpService.serverAddress + "api/online_song/3");
//                audioList.add(HttpService.serverAddress + "api/online_song/4");
//                audioList.add(HttpService.serverAddress + "api/online_song/5");
//                audioList.add(HttpService.serverAddress + "api/online_song/6");
//                audioList.add(HttpService.serverAddress + "api/online_song/7");
//                audioList.add(HttpService.serverAddress + "api/online_song/8");
//                messenger.setNewMusic(0,audioList);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        reload();
    }


    void reload() {
        String string="666";
        MultipartBody formBody = new MultipartBody.Builder().addFormDataPart("testString", string).build()
                ;

        Request request=HttpService.requestBuilderWithPath("test").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                textView.setText(e.toString());
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(data);
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }

    MusicPlayerService messenger;
    boolean bound;
    private ServiceConnection connection = new ServiceConnection() {				//判断有没有绑定Service

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定后可调用MusicPlayerService的方法来达到控制
            messenger=((MusicPlayerService.ServiceBinder) service).getService();
            bound=true;
        }
    };


}
