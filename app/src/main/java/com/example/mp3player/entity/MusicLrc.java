package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/27.
 */

public class MusicLrc extends DateRecord implements Serializable {
    int lrcId;
    int songId;
    int userId;

    public int getLrcId() {
        return lrcId;
    }

    public void setLrcId(int lrcId) {
        this.lrcId = lrcId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
