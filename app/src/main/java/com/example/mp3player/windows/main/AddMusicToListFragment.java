package com.example.mp3player.windows.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.MineMusicList;
import com.example.mp3player.entity.PlayingItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DissoCapB on 2017/3/9.
 */

public class AddMusicToListFragment extends Fragment implements View.OnClickListener {

    View view;
    ListView listView;
    List<MineMusicList> mineMusicLists = new ArrayList<>();
    PlayingItem settingItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_music_to_list, null);
        }
        initData();
        listView = (ListView) view.findViewById(R.id.list_add_to_music_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean isExist=false;
                for(PlayingItem playingItem:mineMusicLists.get(i).getMusicList()){
                    if (playingItem.getFilePath().equals(settingItem.getFilePath())){
                        isExist=true;
                        break;
                    }

                }
                if(!isExist) {
                    mineMusicLists.get(i).getMusicList().add(settingItem);
                    save();
                    Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "已存在", Toast.LENGTH_SHORT).show();
                }
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
        listView.removeAllViewsInLayout();
        musicListAdapter.notifyDataSetInvalidated();
        listView.setAdapter(musicListAdapter);
    }

    BaseAdapter musicListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mineMusicLists == null ? 0 : mineMusicLists.size();
        }

        @Override
        public Object getItem(int i) {
            return mineMusicLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = null;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_mine_music_list, null);
            } else {
                view = convertView;
            }
            TextView name = (TextView) view.findViewById(R.id.music_list_name);
            TextView size = (TextView) view.findViewById(R.id.music_list_size);
            name.setText(mineMusicLists.get(i).getListName());
            size.setText("共" + mineMusicLists.get(i).getMusicList().size() + "首");
            return view;
        }
    };


    private void initData() {
        view.findViewById(R.id.fragment_add_music_to_list).setOnClickListener(this);
        view.findViewById(R.id.linearLayout_add_music_to_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_add_music_to_list:
                getActivity().onBackPressed();
                break;

        }
    }
    public void setSettingItem( PlayingItem settingItem){
        this.settingItem=settingItem;
    }

    public void load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        mineMusicLists = new ArrayList<>();
        try {
            in = getActivity().openFileInput("mineMusicLists");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            mineMusicLists = new Gson().fromJson(content.toString(), new TypeToken<List<MineMusicList>>() {
            }.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {                            //写入文件
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = getActivity().openFileOutput("mineMusicLists", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(new Gson().toJson(mineMusicLists));
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
}
