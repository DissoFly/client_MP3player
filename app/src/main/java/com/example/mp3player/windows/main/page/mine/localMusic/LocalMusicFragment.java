package com.example.mp3player.windows.main.page.mine.localMusic;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.service.MusicPlayerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.mp3player.R.id.btn_local_music_back;
import static com.example.mp3player.R.id.btn_local_music_find_music;
import static com.example.mp3player.R.id.layout_local_music;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_MUSIC_ITEM_SETTING_FRAGMENT;

/**
 * Created by DissoCapB on 2017/1/17.
 */

public class LocalMusicFragment extends Fragment implements View.OnClickListener {
    View view;
    ListView listView;
    private List<PlayingItem> audioList = null; //本地音频列表
    MusicPlayerService messenger;
    boolean bound;
    int openFragInMain = 0;
    int settingSelect;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_page_mine_local_music, null);
            listView=(ListView)view.findViewById(R.id.list_local_music);
            getActivity().bindService(new Intent(getActivity(),MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
            initData();
            listView.setAdapter(listAdapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    onItemClicked(position);
//
//                }
//            });
        }

        return view;
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger=((MusicPlayerService.ServiceBinder) service).getService();
            bound=true;
        }
    };

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return audioList==null?0:audioList.size();
        }

        @Override
        public Object getItem(int i) {
            return audioList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View view=null;
            if (convertView==null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_local_music_list_item, null);
            }else{
                view=convertView;
            }
            ImageView imageView=(ImageView)view.findViewById(R.id.btn_local_music_setting);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //setting
                    settingSelect=i;
                    openFragInMain = OPEN_MUSIC_ITEM_SETTING_FRAGMENT;
                    OnBtnMusicItemSettingClickedListener.OnMusicItemSettingClicked();

                }
            });
            RelativeLayout item=(RelativeLayout)view.findViewById(R.id.local_music_item);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked(i);
                }
            });

            TextView musicName=(TextView)view.findViewById(R.id.text_local_music_name);
//            String s1[] =audioList.get(i).split("/");
            musicName.setText(audioList.get(i).getSongName());
            return view;
        }
    };

    void onItemClicked(int position) {
        messenger.setNewMusic(position,audioList);
    }

    private void initData() {
        view.findViewById(btn_local_music_back).setOnClickListener(this);
        view.findViewById(layout_local_music).setOnClickListener(this);
        view.findViewById(btn_local_music_find_music).setOnClickListener(this);
    }

    @Override
    public void onResume() {

        super.onResume();
        load();
        listView.removeAllViewsInLayout();
        listAdapter.notifyDataSetInvalidated();
        listView.setAdapter(listAdapter);
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

    public void load(){
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        audioList = new ArrayList<>();
        try{
            in=getActivity().openFileInput("localMusicData");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null)
                content.append(line);
            audioList = new Gson().fromJson(content.toString(), new TypeToken<List<PlayingItem>>() {
            }.getType());

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public  PlayingItem getSelectMusic(){
        return audioList.get(settingSelect);
    }
    public int getOpenFragmentInMain() {
        return openFragInMain;
    }

    public static interface OnBtnMusicItemSettingClickedListener {
        void OnMusicItemSettingClicked();
    }

    LocalMusicFragment.OnBtnMusicItemSettingClickedListener OnBtnMusicItemSettingClickedListener;

    public void setOnBtnMusicItemSettingClickedListener(LocalMusicFragment.OnBtnMusicItemSettingClickedListener onBtnMusicItemSettingClickedListener) {
        this.OnBtnMusicItemSettingClickedListener = onBtnMusicItemSettingClickedListener;
    }

}
