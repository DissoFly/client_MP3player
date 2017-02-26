package com.example.mp3player.main.page.mine.download;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.mp3player.R;

import java.util.List;

/**
 * Created by DissoCapB on 2017/2/26.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener{
    View view;
    Button setMusicList;
    Button setMvList;
    Button setDownloadList;
    ListView listView;
    final int SELECT_MUSIC_LIST=0;
    final int SELECT_MV_LIST=1;
    final int SELECT_DOWNLOADING_LIST=2;
    List<String> musicList;
    List<String> mvList;
    List<String> downloadingList;



    int select=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            select=SELECT_MUSIC_LIST;
            view = inflater.inflate(R.layout.fragment_main_page_mine_download, null);
            setMusicList=(Button)view.findViewById(R.id.btn_download_list_music);
            setMvList=(Button)view.findViewById(R.id.btn_download_list_mv);
            setDownloadList=(Button)view.findViewById(R.id.btn_download_list_downloading);
            listView=(ListView)view.findViewById(R.id.download_list);
            initData();
            listView.setAdapter(listAdapter);

        }
        return view;
    }

    BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    };
    private void initData() {
        view.findViewById(R.id.fragment_main_page_mine_download).setOnClickListener(this);
        view.findViewById(R.id.btn_download_back).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_music).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_mv).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_downloading).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_download_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_download_list_music:
                setSelect(SELECT_MUSIC_LIST);
                break;
            case R.id.btn_download_list_mv:
                setSelect(SELECT_MV_LIST);
                break;
            case R.id.btn_download_list_downloading:
                setSelect(SELECT_DOWNLOADING_LIST);
                break;
            default:
                break;
        }
    }

    private void setSelect(int select){
        this.select=select;
        switch (select){
            case SELECT_MUSIC_LIST:

                break;
            case SELECT_MV_LIST:
                break;
            case SELECT_DOWNLOADING_LIST:
                break;
            default:
                break;
        }
        listView.removeAllViewsInLayout();
        listAdapter.notifyDataSetInvalidated();
        listView.setAdapter(listAdapter);
    }
}
