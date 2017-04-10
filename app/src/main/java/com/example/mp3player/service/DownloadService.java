package com.example.mp3player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.mp3player.download.ProgressDownloader;
import com.example.mp3player.download.ProgressResponseBody;
import com.example.mp3player.entity.DownloadMusic;
import com.example.mp3player.entity.Downloading;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.entity.PublicSong;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/2/28.
 */

public class DownloadService extends Service implements ProgressResponseBody.ProgressListener {

    boolean isDownloading = false;
    private ProgressDownloader downloader;
    private long breakPoints;
    private long totalBytes;
    private long contentLength;
    Dao<Downloading, Integer> downloadingDao;
    Dao<DownloadMusic, Integer> downloadMusicDao;
    List<Downloading> downloadingList;
    int downloadingMusicId = 0;
    private final IBinder binder = new ServicesBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        try {
            DataService dataService = DataService.getInstance(DownloadService.this);
            downloadingDao = dataService.getDownloadingDao();
            downloadingList = downloadingDao.queryForAll();
            downloadMusicDao = dataService.getDownloadMusicDao();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return binder;
    }

    public class ServicesBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public List<Downloading> getDownloadingList() {
        return downloadingList;
    }

    public void continueOrPause(int MusicId) {
        if (isDownloading) {
            pauseDownload();
            if (downloadingMusicId != MusicId) {
                continueDowunload(MusicId);
            }
        } else {
            continueDowunload(MusicId);
        }
        //reflash();
    }

    public void continueDowunload(int musicId) {
        try {

            Downloading dl = downloadingDao.queryBuilder().
                    where().
                    eq("musicId", musicId).query().get(0);
            downloader = new ProgressDownloader(HttpService.serverAddress + "api/online_song/" + musicId, new File(dl.getLocalPath()), this);
            downloader.download(dl.getBreakPoints());
            breakPoints = dl.getBreakPoints();
            contentLength = dl.getContentLength();
            isDownloading = true;
            downloadingMusicId = musicId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void downloadMusic(int musicId, String musicName) {
        String path = Environment.getExternalStorageDirectory() + "/MP3player/music/" + musicName + ".mp3";
        if (isDownloading) {
            pauseDownload();
        }
        try {
            //验证正在下载
            List<Downloading> dls = downloadingDao.queryBuilder().
                    where().
                    eq("musicId", musicId).query();
            if (dls.size() != 0) {
                Toast.makeText(this, "存在下载", Toast.LENGTH_SHORT).show();
                return;
            }
            //验证已完成下载
            File file = new File(path);
            if (file.exists()) {
                Toast.makeText(this, "存在完成下载", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!file.getParentFile().exists()){
                if(!file.getParentFile().mkdirs()){
                    Toast.makeText(this, "创建目标文件所在目录失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
            Downloading downloading = new Downloading();
            downloading.setLocalPath(path);
            downloading.setMusicId(musicId);
            downloading.setBreakPoints(0);
            downloading.setContentLength(0);
            downloading.setTotalBytes(0);
            downloadingDao.create(downloading);
            downloadingList = downloadingDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        breakPoints = 0L;
        downloader = new ProgressDownloader(HttpService.serverAddress + "api/online_song/" + musicId, new File(path), DownloadService.this);
        downloader.download(0L);
        contentLength = 0;
        isDownloading = true;
        downloadingMusicId = musicId;
    }


    public void pauseDownload() {
        downloader.pause();
        // 存储此时的totalBytes，即断点位置。
        breakPoints = totalBytes;
        try {
            List<Downloading> dl = downloadingDao.queryBuilder().
                    where().
                    eq("musicId", downloadingMusicId).query();
            if (dl.size() == 0)
                return;
            dl.get(0).setBreakPoints(breakPoints);
            dl.get(0).setTotalBytes(totalBytes);
            downloadingDao.update(dl.get(0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        isDownloading = false;
        //downloadingMusicId=0;
        contentLength = 0;
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
                downloadingList = downloadingDao.queryForAll();
                musicListConnect(dl.getMusicId(), dl.getLocalPath());

            } catch (SQLException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
            System.out.println("下载完成");
            isDownloading = false;
            downloadingMusicId = 0;
        }
        if (isDownloading) {
            //System.out.println("update:" + totalBytes + "," + (int) (totalBytes) / 1024);
            int i = 0;
            for (Downloading dl : downloadingList) {
                if (dl.getMusicId() == downloadingMusicId) {
                    downloadingList.get(i).setBreakPoints(totalBytes);
                    downloadingList.get(i).setContentLength(contentLength);
                    downloadingList.get(i).setTotalBytes(this.totalBytes);
                    break;
                }
                i++;
            }
        }
    }

    void musicListConnect(final int songId, final String path) {
        Request request = HttpService.requestBuilderWithPath("song_message/" + songId).get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final PublicSong data = new Gson().fromJson(response.body().string(), PublicSong.class);
                    DownloadMusic downloadMusic = new DownloadMusic();
                    downloadMusic.setSongName(data.getSongName());
                    downloadMusic.setArtist(data.getArtist());
                    downloadMusic.setAlbum(data.getAlbum());
                    downloadMusic.setLocalPath(path);
                    downloadMusic.setMusicId(songId);
                    downloadMusicDao.create(downloadMusic);
                    saveLocal(downloadMusic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


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
            System.out.println("onPreExecute:" + contentLength + "," + (int) (contentLength / 1024));
        }
    }


    private List<PlayingItem> load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        List<PlayingItem> audioList = new ArrayList<>();
        try {
            in = openFileInput("localMusicData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            audioList = new Gson().fromJson(content.toString(), new TypeToken<List<PlayingItem>>() {
            }.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioList;
    }

    public void saveLocal(DownloadMusic downloadMusic) {

        List<PlayingItem> audioList = load();
        PlayingItem playingItem = new PlayingItem();

        playingItem.setSongName(downloadMusic.getSongName());
        playingItem.setArtist(downloadMusic.getArtist());
        playingItem.setAlbum(downloadMusic.getAlbum());
        playingItem.setOnline(true);
        playingItem.setDownload(true);
        playingItem.setFilePath(downloadMusic.getLocalPath());
        playingItem.setOnlineSongId(downloadMusic.getMusicId());
        audioList.add(0, playingItem);


        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("localMusicData", Context.MODE_PRIVATE);
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

}
