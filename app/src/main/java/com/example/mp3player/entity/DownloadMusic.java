package com.example.mp3player.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by DissoCapB on 2017/2/26.
 */

@DatabaseTable
public class DownloadMusic {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull=false)
    private int musicId;
    @DatabaseField(canBeNull=false)
    private String localPath;
    @DatabaseField
    private String songName;
    @DatabaseField
    private String album;
    @DatabaseField
    private String artist;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
