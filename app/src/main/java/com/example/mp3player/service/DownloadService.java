package com.example.mp3player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.mp3player.download.ProgressDownloader;
import com.example.mp3player.download.ProgressResponseBody;
import com.example.mp3player.entity.Downloading;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

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

    public boolean isDownloading(){
        return isDownloading;
    }
public  List<Downloading> getDownloadingList(){
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
            //reflash();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadMusic(int musicId) {
        String path = Environment.getExternalStorageDirectory() + "/MP3player/music/" + musicId + ".mp3";
        if (isDownloading){
            pauseDownload();
        }
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

       // Toast.makeText(getActivity(), "下载暂停", Toast.LENGTH_SHORT).show();
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
                downloadingList=downloadingDao.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
}
