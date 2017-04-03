package com.example.mp3player.windows.inputcells;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mp3player.R;

import static android.R.attr.radius;

/**
 * Created by DissoCapB on 2017/1/25.
 */

public class SongPictureFragment extends Fragment {


    AvatarView avatarView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_inputcell_song_picture,null);
        avatarView=(AvatarView)view.findViewById(R.id.img_song_picture);
        return view;
    }

    public void setImg(Bitmap bitmap){
        if(bitmap==null){
            Resources res=getResources();
            bitmap= BitmapFactory.decodeResource(res, R.mipmap.bg_music_src);
        }
        avatarView.setBitmap(bitmap);
    }

    public void setBlurImg(Bitmap sentBitmap){
        if(sentBitmap==null){
            Resources res=getResources();
            sentBitmap= BitmapFactory.decodeResource(res, R.mipmap.bg_choose);
        }
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        final RenderScript rs = RenderScript.create(getActivity());
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius /* e.g. 3.f */);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        setImg(bitmap);
    }
}
