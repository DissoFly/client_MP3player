package com.example.mp3player.windows.inputcells;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mp3player.R;

/**
 * Created by DissoCapB on 2017/3/13.
 */

public class MusicListsFragment extends Fragment {
    ImgView imgView;
    TextView name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view=inflater.inflate(R.layout.widget_music_list_item, container);
        imgView=(ImgView)view.findViewById(R.id.img_view);
        name=(TextView)view.findViewById(R.id.text_list_name);
        return view;
    }


    public void setMessages(String srcPath,String listName) {
        System.out.println(srcPath+"---"+listName);
//        imgView.loadById(srcPath);
//        name.setText(listName);
    }


}
