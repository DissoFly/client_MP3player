package com.example.mp3player.windows.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;

import java.util.ArrayList;
import java.util.List;

import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_ADD_MUSIC_TO_LIST_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_DELECT_FRAGMENT;

/**
 * Created by DissoCapB on 2017/3/8.
 */

public class MusicItemSettingFragment extends Fragment implements View.OnClickListener {
    View view;
    PlayingItem settingItem;
    TextView name;
    ListView listView;
    List<String> listItemName = new ArrayList<>();
    List<Integer> listItemNumber = new ArrayList<>();
    List<Integer> listItemSrc = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_music_item_setting, null);
        }
        listView = (ListView) view.findViewById(R.id.list_music_item_setting);
        name = (TextView) view.findViewById(R.id.text_setting_name);
        initData();
        setListItem();


        return view;
    }

    void listItemClick(int i) {
        switch (i) {
            case 0:
                //收藏到歌单
                openFragInMain = OPEN_ADD_MUSIC_TO_LIST_FRAGMENT;
                getActivity().onBackPressed();
                OnMusicItemSettingListener.OnMusicItemSetting();
                break;
            case 1:
                //删除
                openFragInMain = OPEN_DELECT_FRAGMENT;
                getActivity().onBackPressed();
                OnMusicItemSettingListener.OnMusicItemSetting();


                break;


        }
    }

    private void setListItem() {
        listItemName = new ArrayList<>();
        listItemNumber = new ArrayList<>();
        listItemSrc = new ArrayList<>();
        setOneListItem("收藏到歌单", OPEN_ADD_MUSIC_TO_LIST_FRAGMENT, R.mipmap.ic_launcher);
        setOneListItem("删除", OPEN_DELECT_FRAGMENT, R.mipmap.ic_launcher);
        setOneListItem("下载（没做）", 0, R.mipmap.ic_launcher);

        listView.setAdapter(localListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClick(i);
            }
        });
    }

    private void setOneListItem(String listName, int itemNumber, int itemSrc) {
        listItemName.add(listName);
        listItemNumber.add(itemNumber);
        listItemSrc.add(itemSrc);
    }

    BaseAdapter localListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listItemNumber == null ? 0 : listItemNumber.size();
        }

        @Override
        public Object getItem(int i) {
            return listItemNumber.get(i);
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
                view = inflater.inflate(R.layout.widget_img_text_list_item, null);
            } else {
                view = convertView;
            }
            ImageView itemImg = (ImageView) view.findViewById(R.id.list_img);
            TextView itemName = (TextView) view.findViewById(R.id.list_text);
            itemImg.setImageResource(listItemSrc.get(i));
            itemName.setText(listItemName.get(i));

            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (settingItem != null) {
            name.setText(settingItem.getSongName());
        }
    }

    public void setSettingItem(PlayingItem settingItem) {
        this.settingItem = settingItem;
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

    int openFragInMain = 0;

    public PlayingItem getSelectMusic() {
        return settingItem;
    }

    public int getOpenFragmentInMain() {
        return openFragInMain;
    }

    public static interface OnMusicItemSettingListener {
        void OnMusicItemSetting();
    }

    OnMusicItemSettingListener OnMusicItemSettingListener;

    public void setOnMusicItemSettingListener(OnMusicItemSettingListener onMusicItemSettingListener) {
        this.OnMusicItemSettingListener = onMusicItemSettingListener;
    }


}
