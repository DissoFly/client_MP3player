package com.example.mp3player.windows.main.page.mine.download;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.download.ProgressDownloader;
import com.example.mp3player.download.ProgressResponseBody;
import com.example.mp3player.entity.DownloadMusic;
import com.example.mp3player.entity.Downloading;
import com.example.mp3player.service.DataService;
import com.example.mp3player.service.HttpService;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by DissoCapB on 2017/2/26.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener, ProgressResponseBody.ProgressListener {
    View view;
    Button setMusicList;
    Button setMvList;
    Button setDownloadList;
    ListView listView;
    final int SELECT_MUSIC_LIST = 0;
    final int SELECT_MV_LIST = 1;
    final int SELECT_DOWNLOADING_LIST = 2;
    List<DownloadMusic> musicList;
    List<String> mvList;
    List<Downloading> downloadingList;
    Dao<DownloadMusic, Integer> downloadMusicDao;
    Dao<Downloading, Integer> downloadingDao;
    boolean isDownloading = false;
    int downloadingMusicId = 0;

    int select = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            select = SELECT_MUSIC_LIST;
            view = inflater.inflate(R.layout.fragment_main_page_mine_download, null);
            setMusicList = (Button) view.findViewById(R.id.btn_download_list_music);
            setMvList = (Button) view.findViewById(R.id.btn_download_list_mv);
            setDownloadList = (Button) view.findViewById(R.id.btn_download_list_downloading);
            listView = (ListView) view.findViewById(R.id.download_list);
            initData();
            listView.setAdapter(listAdapter);
            handler.postDelayed(runnable, 500);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //if (downloadingMusicId!=0){
                    System.out.println("before:" + isDownloading + "+" + downloadingMusicId);
                    continueOrPause(downloadingList.get(i).getMusicId());
                    System.out.println("continueOrPause");
                    System.out.println("after:" + isDownloading + "+" + downloadingMusicId);
                    //}
                }
            });
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        reflash();
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
                textView.setText("歌曲编号：" + dl.getMusicId() + ",已下载：" + dl.getBreakPoints() / 1024.0 / 1024.0 + "M/" + dl.getContentLength() / 1024.0 / 1024.0 + "M");
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
        view.findViewById(R.id.btn_download_list_mv).setOnClickListener(this);
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
            case R.id.btn_download_list_mv:
                setSelect(SELECT_MV_LIST);
                break;
            case R.id.test1:
                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入")
                        .setMessage("12")
                        .setPositiveButton("1", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadMusic(1);
                            }
                        })
                        .setNegativeButton("2", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadMusic(2);
                            }
                        })
                        .show();
                break;
            case R.id.test2:
                pauseDownload();
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
        try {
            DataService dataService = DataService.getInstance(getActivity());
            //downloadMusicDao = dataService.getDownloadMusicDao();
            downloadingDao = dataService.getDownloadingDao();
            //musicList =downloadMusicDao.queryForAll();
            downloadingList = downloadingDao.queryForAll();

            listView.removeAllViewsInLayout();
            listAdapter.notifyDataSetInvalidated();
            listView.setAdapter(listAdapter);
            System.out.println("reflash");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void continueOrPause(int MusicId) {
        if (isDownloading) {
            pauseDownload();
            if (downloadingMusicId != MusicId) {
                continueDowunload(MusicId);
            }
        } else {
            continueDowunload(MusicId);
        }
        reflash();
    }

    private void continueDowunload(int musicId) {
        try {
            Downloading dl = downloadingDao.queryBuilder().
                    where().
                    eq("musicId", musicId).query().get(0);

            downloader = new ProgressDownloader(HttpService.serverAddress + "api/online_song/" + musicId, new File(dl.getLocalPath()), DownloadFragment.this);
            downloader.download(dl.getBreakPoints());
            breakPoints = dl.getBreakPoints();
            contentLength = dl.getContentLength();
            isDownloading = true;
            downloadingMusicId = musicId;
            reflash();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void pauseDownload() {
        downloader.pause();

        Toast.makeText(getActivity(), "下载暂停", Toast.LENGTH_SHORT).show();
        // 存储此时的totalBytes，即断点位置。
        breakPoints = totalBytes;
        try {
            List<Downloading> dl = downloadingDao.queryBuilder().
                    where().
                    eq("musicId", downloadingMusicId).query();
            if (dl.size() == 0)
                return;
            dl.get(0).setBreakPoints(breakPoints);
            downloadingDao.update(dl.get(0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        isDownloading = false;
        //downloadingMusicId=0;
        contentLength = 0;
    }

    private void downloadMusic(int musicId) {
        String path = Environment.getExternalStorageDirectory() + "/MP3player/music/" + musicId + ".mp3";
        try {
            //差验证已完成下载
            List<Downloading> dls = downloadingDao.queryBuilder().
                    where().
                    eq("musicId", musicId).query();
            if (dls.size() != 0) {
                System.out.println("存在下载");
                return;
            }
            Downloading downloading = new Downloading();
            downloading.setLocalPath(path);
            downloading.setMusicId(musicId);
            downloading.setBreakPoints(0);
            downloading.setContentLength(0);
            downloading.setTotalBytes(0);
            downloadingDao.createOrUpdate(downloading);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        breakPoints = 0L;
        downloader = new ProgressDownloader(HttpService.serverAddress + "api/online_song/" + musicId, new File(path), DownloadFragment.this);
        downloader.download(0L);
        contentLength = 0;
        isDownloading = true;
        downloadingMusicId = musicId;
        reflash();
    }

    private void setSelect(int select) {
        this.select = select;
        switch (select) {
            case SELECT_MUSIC_LIST:

                break;
            case SELECT_MV_LIST:
                break;
            case SELECT_DOWNLOADING_LIST:
                break;
            default:
                break;
        }
        reflash();
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
                Downloading dl = downloadingDao.queryBuilder().
                        where().
                        eq("musicId", downloadingMusicId).query().get(0);
                dl.setContentLength(contentLength);
                downloadingDao.update(dl);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //downloadingDao.create(downloading);


            System.out.println("onPreExecute:" + contentLength + "," + (int) (contentLength / 1024));
            // progressBar.setMax((int) (contentLength / 1024));
        }
    }

    @Override
    public void update(long totalBytes, boolean done) {
        // 注意加上断点的长度
        isDownloading = true;
        this.totalBytes = totalBytes + breakPoints;
        //System.out.println("update:"+totalBytes+","+(int) (totalBytes + breakPoints) / 1024);

        if (done) {
            try {
                Downloading dl = downloadingDao.queryBuilder().
                        where().
                        eq("musicId", downloadingMusicId).query().get(0);
                downloadingDao.delete(dl);
                handlerReflash.post(runnableReflash);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("下载完成");
            isDownloading = false;
            downloadingMusicId = 0;
        }
    }

    Handler handlerReflash=new Handler();
    Runnable runnableReflash=new Runnable() {
        @Override
        public void run() {
            reflash();
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isDownloading) {
                System.out.println("update:" + totalBytes + "," + (int) (totalBytes) / 1024);
                int i = 0;
                for (Downloading dl : downloadingList) {
                    if (dl.getMusicId() == downloadingMusicId) {
                        downloadingList.get(i).setBreakPoints(totalBytes);
                        downloadingList.get(i).setContentLength(contentLength);
                        break;
                    }
                    i++;
                }
            }
            listView.removeAllViewsInLayout();
            listAdapter.notifyDataSetInvalidated();
            listView.setAdapter(listAdapter);

            handler.postDelayed(runnable, 500);
        }
    };
}
