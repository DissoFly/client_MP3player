package com.example.mp3player.main.inPage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mp3player.R;
import com.example.mp3player.main.inPage.localMusic.FindMusicInLocalActivity;

import static com.example.mp3player.R.id.btn_local_music_back;
import static com.example.mp3player.R.id.btn_local_music_find_music;
import static com.example.mp3player.R.id.layout_local_music;

/**
 * Created by DissoCapB on 2017/1/17.
 */

public class LocalMusicFragment extends Fragment implements View.OnClickListener {
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_page_mine_local_music, null);

            initData();
        }

        return view;
    }

    private void initData() {
        view.findViewById(btn_local_music_back).setOnClickListener(this);
        view.findViewById(layout_local_music).setOnClickListener(this);
        view.findViewById(btn_local_music_find_music).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        //自动更新数据
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case btn_local_music_back:
                getActivity().onBackPressed();
                break;
            case btn_local_music_find_music:
                Intent itnt=new Intent(getActivity(), FindMusicInLocalActivity.class);
                startActivity(itnt);

                break;
            default:
                break;
        }
    }
}
