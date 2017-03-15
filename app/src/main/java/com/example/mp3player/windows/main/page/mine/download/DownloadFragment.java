package com.example.mp3player.windows.main.page.mine.download;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.DownloadMusic;
import com.example.mp3player.entity.Downloading;
import com.example.mp3player.service.DownloadService;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.util.List;

/**
 * Created by DissoCapB on 2017/2/26.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener {
    View view;
    Button setMusicList;
    Button setDownloadList;
    ListView listView;
    final int SELECT_MUSIC_LIST = 0;
    final int SELECT_MV_LIST = 1;
    final int SELECT_DOWNLOADING_LIST = 2;
    List<DownloadMusic> musicList;
    List<Downloading> downloadingList;
    Dao<DownloadMusic, Integer> downloadMusicDao;
    Dao<Downloading, Integer> downloadingDao;

    int select = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            getActivity().bindService(new Intent(getActivity(), DownloadService.class), connection, Context.BIND_AUTO_CREATE);
            select = SELECT_MUSIC_LIST;
            view = inflater.inflate(R.layout.fragment_main_page_mine_download, null);
            setMusicList = (Button) view.findViewById(R.id.btn_download_list_music);
            setDownloadList = (Button) view.findViewById(R.id.btn_download_list_downloading);
            listView = (ListView) view.findViewById(R.id.download_list);
            initData();
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    messenger.continueOrPause(downloadingList.get(i).getMusicId());
                }
            });
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), DownloadService.class), connection, Context.BIND_AUTO_CREATE);

    }

    BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return downloadingList == null ? 0 : downloadingList.size();
        }

        @Override
        public Object getItem(int i) {
            return downloadingList == null ? null : downloadingList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_downloading_list_item, null);
                TextView textView = (TextView) view.findViewById(R.id.list_text);
                Downloading dl = downloadingList.get(i);
                textView.setText("歌曲编号：" + dl.getMusicId() + ",已下载：" + dl.getTotalBytes() / 1024.0 / 1024.0 + "M/" + dl.getContentLength() / 1024.0 / 1024.0 + "M");
            } else {
                view = convertView;
            }
            return view;
        }
    };

    private void initData() {
        view.findViewById(R.id.fragment_main_page_mine_download).setOnClickListener(this);
        view.findViewById(R.id.btn_download_back).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_music).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_downloading).setOnClickListener(this);
        view.findViewById(R.id.test1).setOnClickListener(this);
        view.findViewById(R.id.test2).setOnClickListener(this);
        view.findViewById(R.id.test3).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_download_list_music:
                setSelect(SELECT_MUSIC_LIST);
                break;
            case R.id.test1:
                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入")
                        .setMessage("12")
                        .setPositiveButton("1", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                messenger.downloadMusic(1);
                            }
                        })
                        .setNegativeButton("2", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                messenger.downloadMusic(2);
                            }
                        })
                        .show();
                break;
            case R.id.test2:
                messenger.pauseDownload();
                break;
            case R.id.test3:
                for (Downloading dl : downloadingList)
                    System.out.println(new Gson().toJson(dl));
                break;
            default:
                break;
        }
    }

    private void reflash() {
        downloadingList = messenger.getDownloadingList();
        listView.removeAllViewsInLayout();
        listAdapter.notifyDataSetInvalidated();
        listView.setAdapter(listAdapter);
    }


    private void setSelect(int select) {
        this.select = select;
        switch (select) {
            case SELECT_MUSIC_LIST:
                break;
            case SELECT_DOWNLOADING_LIST:
                break;
            default:
                break;
        }
        reflash();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            downloadingList = messenger.getDownloadingList();
            listView.removeAllViewsInLayout();
            listAdapter.notifyDataSetInvalidated();
            listView.setAdapter(listAdapter);
            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };

    DownloadService messenger;
    boolean bound;
    int REFLASH_TIME = 500;
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {

            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = ((DownloadService.ServicesBinder) service).getService();
            handler.postDelayed(runnable, REFLASH_TIME);
            reflash();
            bound = true;
        }
    };
}
