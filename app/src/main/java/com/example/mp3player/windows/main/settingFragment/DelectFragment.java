package com.example.mp3player.windows.main.settingFragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.windows.main.page.mine.localMusic.LocalMusicFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DissoCapB on 2017/3/11.
 */

public class DelectFragment extends Fragment implements View.OnClickListener {
    View view;
    private List<PlayingItem> audioList = null;
    CheckBox checkBox;
    PlayingItem settingItem;
    boolean select=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_delect, null);
        }
        checkBox = (CheckBox) view.findViewById(R.id.checkBox_delect);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                select=b;
            }
        });
        initData();
        return view;
    }

    private void initData() {
        view.findViewById(R.id.fragment_delect).setOnClickListener(this);
        view.findViewById(R.id.in_delect).setOnClickListener(this);
        view.findViewById(R.id.btn_delect_confirm).setOnClickListener(this);
        view.findViewById(R.id.btn_delect_cancel).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_delect:
                getActivity().onBackPressed();
                break;
            case R.id.btn_delect_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.btn_delect_confirm:
                load();
                if (select) {
                    delectFile();
                }
                int j = 0;
                for (PlayingItem playingItem : audioList) {
                    if (playingItem.getFilePath().equals(settingItem.getFilePath())) {
                        audioList.remove(j);
                        break;
                    }
                    j++;
                }
                save();
                from.reload();
                getActivity().onBackPressed();
                break;

        }
    }

    public void setSettingItem(PlayingItem settingItem) {
        this.settingItem = settingItem;
    }

    private void delectFile() {
        for (PlayingItem playingItem : audioList) {
            if (playingItem.getFilePath().equals(settingItem.getFilePath())) {
                File file = new File(playingItem.getFilePath());
                if (file.exists()) {
                    file.delete();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    System.out.println("delect fail");
                }
                break;
            }
        }
    }


    public void load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        audioList = new ArrayList<>();
        try {
            in = getActivity().openFileInput("localMusicData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            audioList = new Gson().fromJson(content.toString(), new TypeToken<List<PlayingItem>>() {
            }.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {                            //写入文件
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = getActivity().openFileOutput("localMusicData", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(new Gson().toJson(audioList));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    LocalMusicFragment from;
    public void setFrom(LocalMusicFragment from) {
        this.from = from;
    }
}
