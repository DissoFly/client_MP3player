package com.example.mp3player.windows.main;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.windows.playMusic.PlayingActivity;
import com.example.mp3player.R;
import com.example.mp3player.service.MusicPlayerService;

import java.util.List;

import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_FOOTER_PLAYING_LIST_FRAGMENT;


/**
 * Created by DissoCapB on 2017/1/19.
 */

public class FooterPlayerFragment extends Fragment implements View.OnClickListener {
    View view;
    MusicPlayerService messenger;
    boolean bound;
    int openFragInMain = 0;
    int REFLASH_TIME = 100;

    private List<PlayingItem> audioList = null;
    private int listPosition = -1;

    TextView musicName;
    TextView musicArtist;

    ImageView btnPlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_footer, null);
            getActivity().bindService(new Intent(getActivity(), MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
            musicName = (TextView) view.findViewById(R.id.text_footer_music_name);
            musicArtist = (TextView) view.findViewById(R.id.text_footer_music_artist);
            btnPlay= (ImageView) view.findViewById(R.id.btn_footer_play);
            initData();
        }
        return view;
    }


    private ServiceConnection connection = new ServiceConnection() {                //判断有没有绑定Service

        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定后可调用MusicPlayerService的方法来达到控制
            messenger = ((MusicPlayerService.ServiceBinder) service).getService();
            handler.postDelayed(runnable, REFLASH_TIME);
            bound = true;
        }
    };

    private void initData() {
        view.findViewById(R.id.to_playing_activity).setOnClickListener(this);
        view.findViewById(R.id.btn_footer_play).setOnClickListener(this);
        view.findViewById(R.id.btn_footer_next).setOnClickListener(this);
        view.findViewById(R.id.btn_footer_list).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.to_playing_activity:
                Intent itnt = new Intent(getActivity(), PlayingActivity.class);
                startActivity(itnt);
                break;
            case R.id.btn_footer_play:
                messenger.playOrPause();
                break;
            case R.id.btn_footer_next:
                messenger.next();
                break;
            case R.id.btn_footer_list:
                openFragInMain = OPEN_FOOTER_PLAYING_LIST_FRAGMENT;
                OnBtnPlayingListClickedListener.OnBtnPlayingListClicked();

                break;
            default:
                break;
        }
    }

    /////////////////////////////////////////返回调取此fragment的方法↓/////////////////////////////////////////////////
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

    /////////////////////////////////////////返回调取此fragment的方法↑/////////////////////////////////////////////////
    /////////////////////////////////////////定时刷新↓/////////////////////////////////////////////////
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            audioList = messenger.getAudioList();
            listPosition = messenger.getPlayingListPosition();

            if (listPosition == -1) {
                musicName.setText("音乐，让生活更美好");
                musicArtist.setText("MP3Player");
            }else {
                musicName.setText(audioList.get(listPosition).getSongName());
                musicArtist.setText(audioList.get(listPosition).getArtist());
            }
            if(messenger.isPlaying()){
                btnPlay.setImageResource(R.mipmap.ic_pause);
            }else{
                btnPlay.setImageResource(R.mipmap.ic_play);
            }

            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };


    /////////////////////////////////////////定时刷新↑/////////////////////////////////////////////////


}
