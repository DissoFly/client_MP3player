package com.example.mp3player.windows.main.page.mine.download;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
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
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.service.DataService;
import com.example.mp3player.service.DownloadService;
import com.example.mp3player.service.MusicPlayerService;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by DissoCapB on 2017/2/26.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener {
    View view;
    ListView listView;
    final int SELECT_MUSIC_LIST = 0;
    final int SELECT_DOWNLOADING_LIST = 2;
    List<DownloadMusic> downloadMusicList;
    List<Downloading> downloadingList;
    Dao<DownloadMusic, Integer> downloadMusicDao;

    Button btnMusic;
    Button btnDownload;


    int select = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            getActivity().bindService(new Intent(getActivity(), DownloadService.class), connection, Context.BIND_AUTO_CREATE);
            getActivity().bindService(new Intent(getActivity(), MusicPlayerService.class), connectionPlaying, Context.BIND_AUTO_CREATE);
            select = SELECT_MUSIC_LIST;
            view = inflater.inflate(R.layout.fragment_main_page_mine_download, null);
            btnMusic = (Button) view.findViewById(R.id.btn_download_list_music);
            btnDownload = (Button) view.findViewById(R.id.btn_download_list_downloading);
            listView = (ListView) view.findViewById(R.id.download_list);

            initData();
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), DownloadService.class), connection, Context.BIND_AUTO_CREATE);
        DataService dataService = DataService.getInstance(getActivity());
        try {
            downloadMusicDao = dataService.getDownloadMusicDao();
            setSelect(SELECT_MUSIC_LIST);
            reflash1();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    BaseAdapter listAdapter2 = new BaseAdapter() {
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
            } else {
                view = convertView;
            }
            TextView name = (TextView) view.findViewById(R.id.text_name);
            TextView progress = (TextView) view.findViewById(R.id.text_progress);
            Downloading dl = downloadingList.get(i);
            String titalBytes = new DecimalFormat("##.##").format(dl.getTotalBytes() / 1024.0 / 1024.0);
            String contentLength = new DecimalFormat("##.##").format(dl.getContentLength() / 1024.0 / 1024.0);
            name.setText(dl.getMusicName());
            progress.setText("已下载：" + titalBytes + "M/" + contentLength + "M");
            return view;
        }
    };

    BaseAdapter listAdapter1 = new BaseAdapter() {
        @Override
        public int getCount() {
            return downloadMusicList == null ? 0 : downloadMusicList.size();
        }

        @Override
        public Object getItem(int i) {
            return downloadMusicList == null ? null : downloadMusicList.get(i);
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
                view = inflater.inflate(R.layout.widget_download_finish, null);
            } else {
                view = convertView;
            }
            TextView name = (TextView) view.findViewById(R.id.list_name);
            TextView others = (TextView) view.findViewById(R.id.list_others);
            DownloadMusic dMusic = downloadMusicList.get(i);
            name.setText(dMusic.getSongName());
            others.setText(dMusic.getArtist() + " - " + dMusic.getAlbum());
            return view;
        }
    };


    private void initData() {
        view.findViewById(R.id.fragment_main_page_mine_download).setOnClickListener(this);
        view.findViewById(R.id.btn_download_back).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_music).setOnClickListener(this);
        view.findViewById(R.id.btn_download_list_downloading).setOnClickListener(this);
        //        view.findViewById(R.id.test1).setOnClickListener(this);
        //        view.findViewById(R.id.test2).setOnClickListener(this);
        //        view.findViewById(R.id.test3).setOnClickListener(this);
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
            case R.id.btn_download_list_downloading:
                setSelect(SELECT_DOWNLOADING_LIST);
                break;
            //            case R.id.test1:
            //                new AlertDialog.Builder(getActivity())
            //                        .setTitle("请输入")
            //                        .setMessage("12")
            //                        .setPositiveButton("1", new DialogInterface.OnClickListener() {
            //                            @Override
            //                            public void onClick(DialogInterface dialogInterface, int i) {
            //                                messenger.downloadMusic(1,"aaaaa");
            //                            }
            //                        })
            //                        .setNegativeButton("2", new DialogInterface.OnClickListener() {
            //                            @Override
            //                            public void onClick(DialogInterface dialogInterface, int i) {
            //                                messenger.downloadMusic(2,"bbbbbbb");
            //                            }
            //                        })
            //                        .show();
            //                break;
            //            case R.id.test2:
            //                messenger.pauseDownload();
            //                break;
            //            case R.id.test3:
            //                try {
            //                    downloadMusicList=downloadMusicDao.queryForAll();
            //                    for(DownloadMusic downloadMusic:downloadMusicList){
            //                        downloadMusicDao.deleteById(downloadMusic.getId());
            //                    }
            //                } catch (SQLException e) {
            //                    e.printStackTrace();
            //                }
            //
            //                break;
            default:
                break;
        }
    }

    private void reflash1() {
        try {
            downloadMusicList = downloadMusicDao.queryForAll();
            listView.removeAllViewsInLayout();
            listAdapter1.notifyDataSetInvalidated();
            listView.setAdapter(listAdapter1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reflash2() {
        downloadingList = messenger.getDownloadingList();
        listView.removeAllViewsInLayout();
        listAdapter2.notifyDataSetInvalidated();
        listView.setAdapter(listAdapter2);
    }


    private void setSelect(int select) {
        this.select = select;
        switch (select) {
            case SELECT_MUSIC_LIST:
                btnMusic.setTextColor(Color.parseColor("#d33a31"));
                btnMusic.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                btnDownload.setTextColor(Color.parseColor("#000000"));
                btnDownload.setBackground(null);
                reflash1();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        PlayingItem playingItem = new PlayingItem();
                        playingItem.setSongName(downloadMusicList.get(i).getSongName());
                        playingItem.setArtist(downloadMusicList.get(i).getArtist());
                        playingItem.setAlbum(downloadMusicList.get(i).getAlbum());
                        playingItem.setOnline(true);
                        playingItem.setDownload(true);
                        playingItem.setFilePath(downloadMusicList.get(i).getLocalPath());
                        playingItem.setOnlineSongId(downloadMusicList.get(i).getMusicId());
                        messengerPlaying.setOneAndPlay(playingItem);
                    }
                });

                break;
            case SELECT_DOWNLOADING_LIST:
                btnMusic.setTextColor(Color.parseColor("#000000"));
                btnMusic.setBackground(null);
                btnDownload.setTextColor(Color.parseColor("#d33a31"));
                btnDownload.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                reflash2();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        messenger.continueOrPause(downloadingList.get(i).getMusicId());
                    }
                });

                break;
            default:
                break;
        }

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (select == SELECT_MUSIC_LIST) {
                try {
                    downloadMusicList = downloadMusicDao.queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                listAdapter1.notifyDataSetChanged();
            } else {
                reflash2();
            }
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
            bound = true;
        }
    };

    MusicPlayerService messengerPlaying;
    boolean boundPlaying;
    private ServiceConnection connectionPlaying = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messengerPlaying = null;
            boundPlaying = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messengerPlaying = ((MusicPlayerService.ServiceBinder) service).getService();
            boundPlaying = true;
        }
    };
}
