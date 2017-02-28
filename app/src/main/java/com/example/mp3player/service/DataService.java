package com.example.mp3player.service;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mp3player.entity.DownloadMusic;
import com.example.mp3player.entity.Downloading;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by DissoCapB on 2017/2/26.
 */

public class DataService extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "blob.db";
    public static final int DATABASE_VERSION = 1;
    private static DataService instance;

    public DataService(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataService getInstance(Context c) {
        if (instance == null)
            instance = new DataService(c);


        return instance;
    }
    public Dao<DownloadMusic, Integer> getDownloadMusicDao() throws SQLException {
        return getDao(DownloadMusic.class);

    }
    public Dao<Downloading, Integer> getDownloadingDao() throws SQLException {
        return getDao(Downloading.class);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DownloadMusic.class);
            TableUtils.createTable(connectionSource, Downloading.class);
            Log.e(DataService.class.getName(), "创建数据库成功");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(DataService.class.getName(), "创建数据库失败", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, DownloadMusic.class, true);
            TableUtils.dropTable(connectionSource, Downloading.class, true);
            onCreate(sqLiteDatabase, connectionSource);
            Log.e(DataService.class.getName(), "更新数据库成功");
        } catch (SQLException e) {
            Log.e(DataService.class.getName(), "更新数据库失败", e);
        }
    }
}
