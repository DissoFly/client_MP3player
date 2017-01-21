package com.example.mp3player.main;

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
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.service.MusicPlayerService;

import static com.example.mp3player.R.id.btn_footer_list;
import static com.example.mp3player.R.id.btn_footer_next;
import static com.example.mp3player.R.id.btn_footer_play;

/**
 * Created by DissoCapB on 2017/1/19.
 */

public class FooterPlayerFragment extends Fragment implements View.OnClickListener{
    View view;
    MusicPlayerService messenger;
    boolean bound;
    int openFragInMain = 0;
    final int OPEN_FOOTER_PLAYING_LIST_FRAGMENT = 12;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_main_footer, null);
            getActivity().bindService(new Intent(getActivity(),MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
            initData();
        }
        return view;
    }



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

    private void initData() {
        view.findViewById(btn_footer_play).setOnClickListener(this);
        view.findViewById(btn_footer_next).setOnClickListener(this);
        view.findViewById(btn_footer_list).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case btn_footer_play:
                Toast.makeText(getActivity(), "connect success!"+messenger.getConnect(), Toast.LENGTH_SHORT).show();

                break;
            case btn_footer_next:

                break;
            case btn_footer_list:
                openFragInMain = OPEN_FOOTER_PLAYING_LIST_FRAGMENT;
                OnBtnPlayingListClickedListener.OnBtnPlayingListClicked();

                break;
            default:
                break;
        }
    }
    public int getOpenFragmentInMain() {
        return openFragInMain;
    }



    public static interface OnBtnPlayingListClickedListener {
        void OnBtnPlayingListClicked();
    }

    OnBtnPlayingListClickedListener OnBtnPlayingListClickedListener;

    public void setOnBtnPlayingListClickedListener(OnBtnPlayingListClickedListener onBtnPlayingListClickedListener) {
        this.OnBtnPlayingListClickedListener = onBtnPlayingListClickedListener;
    }
}
