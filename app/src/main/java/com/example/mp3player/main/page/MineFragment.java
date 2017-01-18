package com.example.mp3player.main.page;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mp3player.R;

import static com.example.mp3player.R.id.btn_local_music;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    View view;
    int openFragInMain = 0;
    final int OPEN_LOCAL_MUSIC_FRAGMENT = 11;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_page_mine, null);
            initData();
        }
        return view;
    }

    private void initData() {
        view.findViewById(btn_local_music).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case btn_local_music:
                openFragInMain = OPEN_LOCAL_MUSIC_FRAGMENT;
                OnBtnLocalMusicClickedListener.OnBtnLocalMusicClicked();

                break;
            default:
                break;
        }
    }

    public int getOpenFragmentInMain() {
        return openFragInMain;
    }


    public static interface OnBtnLocalMusicClickedListener {
        void OnBtnLocalMusicClicked();
    }

    OnBtnLocalMusicClickedListener OnBtnLocalMusicClickedListener;

    public void setOnBtnLocalMusicClickedListener(OnBtnLocalMusicClickedListener onBtnLocalMusicClickedListener) {
        this.OnBtnLocalMusicClickedListener = onBtnLocalMusicClickedListener;
    }
}
