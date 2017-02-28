package com.example.mp3player.main.page.mine.download;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.DownloadMusic;
import com.example.mp3player.entity.Downloading;
import com.example.mp3player.service.DataService;
import com.example.mp3player.service.HttpService;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by DissoCapB on 2017/2/26.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener,ProgressResponseBody.ProgressListener{
    View view;
    Button setMusicList;
    Button setMvList;
    Button setDownloadList;
    ListView listView;
    final int SELECT_MUSIC_LIST=0;
    final int SELECT_MV_LIST=1;
    final int SELECT_DOWNLOADING_LIST=2;
    List<DownloadMusic> musicList;
    List<String> mvList;
    List<Downloading> downloadingList;
    Dao<DownloadMusic, Integer> downloadMusicDao;
    Dao<Downloading, Integer> downloadingDao;


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




    @Override
    public void onResume() {
        super.onResume();
        try {
            DataService dataService = DataService.getInstance(getActivity());
            //downloadMusicDao = dataService.getDownloadMusicDao();
            downloadingDao=dataService.getDownloadingDao();
            //musicList =downloadMusicDao.queryForAll();
            downloadingList =downloadingDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        view.findViewById(R.id.test1).setOnClickListener(this);
        view.findViewById(R.id.test2).setOnClickListener(this);
        view.findViewById(R.id.test3).setOnClickListener(this);
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
            case R.id.test1:
                int i;
                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入")
                        .setMessage("12")
                        .setPositiveButton("1", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String path=Environment.getExternalStorageDirectory() + "/MP3player/music/"+1;
                                breakPoints = 0L;
                                downloader = new ProgressDownloader(HttpService.serverAddress + "api/online_song/1",new File(path), DownloadFragment.this);
                                downloader.download(0L);
                                try {
                                    Downloading downloading =new Downloading();
                                    downloading.setLocalPath(path);
                                    downloading.setMusicId(1);
                                    downloading.setBreakPoints(0);
                                    downloading.setContentLength(0);
                                    downloading.setTotalBytes(0);
                                    downloadingDao.create(downloading);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("2", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
//                breakPoints = 0L;
//                File file = new File(Environment.getExternalStorageDirectory() + "/MP3player/music/"+1);
//                downloader = new ProgressDownloader(HttpService.serverAddress + "api/online_song/1", file, this);
//                downloader.download(0L);
                break;
            case R.id.test2:
                downloader.pause();
                Toast.makeText(getActivity(), "下载暂停", Toast.LENGTH_SHORT).show();
                // 存储此时的totalBytes，即断点位置。
                breakPoints = totalBytes;
                break;
            case R.id.test3:
                downloader.download(breakPoints);
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


    private ProgressDownloader downloader;
    private long breakPoints;
    private long totalBytes;
    private long contentLength;
    @Override
    public void onPreExecute(long contentLength) {
        if (this.contentLength == 0L) {
            this.contentLength = contentLength;


            try {
                //GenericRawResults<Downloading> qb = downloadingDao.queryBuilder();
                //qb.where().ge("musicId", 1);
                GenericRawResults<String[]> downloadings =downloadingDao.queryRaw("select * from Downloading group by id");
                System.out.println(downloadings.getResults().get(0).toString());
                       // downloading.setContentLength(contentLength);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //downloadingDao.create(downloading);


            System.out.println("onPreExecute:"+contentLength+","+(int) (contentLength / 1024));
           // progressBar.setMax((int) (contentLength / 1024));
        }
    }

    @Override
    public void update(long totalBytes, boolean done) {
        // 注意加上断点的长度
        this.totalBytes = totalBytes + breakPoints;
        System.out.println("update:"+totalBytes+","+(int) (totalBytes + breakPoints) / 1024);
        //progressBar.setProgress((int) (totalBytes + breakPoints) / 1024);
        if (done) {
            // 切换到主线程
            System.out.println("下载完成");
        }
    }

}
