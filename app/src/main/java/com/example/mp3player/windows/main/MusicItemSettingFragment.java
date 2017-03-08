package com.example.mp3player.windows.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;

/**
 * Created by DissoCapB on 2017/3/8.
 */

public class MusicItemSettingFragment extends Fragment implements View.OnClickListener{
    View view;
    PlayingItem settingItem;
    TextView name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_music_item_setting, null);
        }
        name=(TextView)view.findViewById(R.id.text_setting_name);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(settingItem!=null){
            name.setText(settingItem.getSongName());
        }
    }

    public void setSettingItem( PlayingItem settingItem){
        this.settingItem=settingItem;
    }

    private void initData() {
        view.findViewById(R.id.fragment_music_item_setting).setOnClickListener(this);
        view.findViewById(R.id.linearLayout_music_item_setting).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_music_item_setting:
                getActivity().onBackPressed();
                break;

        }
    }
}
