package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/13.
 */

public class MusicList extends DateRecord implements Serializable {

    private int musicListId;
    private int userId;
    private boolean isPush;
    private String listName;
    private String listAbout;
    private String srcPath;
    private String musics;

    public int getMusicListId() {
        return musicListId;
    }

    public void setMusicListId(int musicListId) {
        this.musicListId = musicListId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isPush() {
        return isPush;
    }

    public void setPush(boolean push) {
        isPush = push;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListAbout() {
        return listAbout;
    }

    public void setListAbout(String listAbout) {
        this.listAbout = listAbout;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getMusics() {
        return musics;
    }

    public void setMusics(String musics) {
        this.musics = musics;
    }
}
