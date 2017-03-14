package com.example.mp3player.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DissoCapB on 2017/3/8.
 */
public class MineMusicList extends DateRecord{
    int id;
    String listName;
    String srcPath;
    String listAbout;
    List<PlayingItem> musicList;
    int userId;

    public String getListAbout() {
        return listAbout;
    }

    public void setListAbout(String listAbout) {
        this.listAbout = listAbout;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public List<PlayingItem> getMusicList() {
        if (musicList==null){
            musicList=new ArrayList<>();
        }
        return musicList;
    }

    public void setMusicList(List<PlayingItem> musicList) {
        this.musicList = musicList;
    }
    public void addItem(PlayingItem playingItem){
        if (musicList==null){
            musicList=new ArrayList<>();
        }
        musicList.add(playingItem);
    }
}
