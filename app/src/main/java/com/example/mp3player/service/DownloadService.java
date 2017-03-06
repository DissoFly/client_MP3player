package com.example.mp3player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.mp3player.windows.main.page.mine.download.ProgressResponseBody;

/**
 * Created by DissoCapB on 2017/2/28.
 */

public class DownloadService extends Service implements ProgressResponseBody.ProgressListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPreExecute(long contentLength) {

    }

    @Override
    public void update(long totalBytes, boolean done) {

    }
}
