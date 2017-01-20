package com.example.mp3player.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mp3player.R;

import static com.example.mp3player.R.id.btn_footer_list;
import static com.example.mp3player.R.id.btn_footer_next;
import static com.example.mp3player.R.id.btn_footer_play;

/**
 * Created by DissoCapB on 2017/1/19.
 */

public class FooterPlayerFragment extends Fragment implements View.OnClickListener{
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.widget_main_footer, null);
            initData();
        }
        return view;
    }

    private void initData() {
        view.findViewById(btn_footer_play).setOnClickListener(this);
        view.findViewById(btn_footer_next).setOnClickListener(this);
        view.findViewById(btn_footer_list).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case btn_footer_play:

                break;
            case btn_footer_next:

                break;
            case btn_footer_list:

                break;
            default:
                break;
        }
    }
}
