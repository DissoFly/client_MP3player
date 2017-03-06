package com.example.mp3player.windows.inputcells;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mp3player.R;

/**
 * Created by DissoCapB on 2017/1/25.
 */

public class SongPictureFragment extends Fragment {
    ImageView songImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_inputcell_song_picture,null);
        songImg=(ImageView)view.findViewById(R.id.img_song_picture);
        return view;
    }

    public void setImg(Bitmap bitmap){
        songImg.setImageBitmap(bitmap);
    }
}
