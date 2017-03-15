package com.example.mp3player.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by DissoCapB on 2017/3/7.
 */
@DatabaseTable
public class PlayingItem {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private boolean isOnline;
    @DatabaseField(canBeNull = false)
    private boolean isDownload;
    @DatabaseField(canBeNull = false)
    private String songName;
    @DatabaseField
    private String artist;
    @DatabaseField
    private String album;
    @DatabaseField(canBeNull = false)
    private String filePath;
    @DatabaseField(canBeNull = false)
    private int OnlineSongId;

    public int getOnlineSongId() {
        return OnlineSongId;
    }

    public void setOnlineSongId(int onlineSongId) {
        OnlineSongId = onlineSongId;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
