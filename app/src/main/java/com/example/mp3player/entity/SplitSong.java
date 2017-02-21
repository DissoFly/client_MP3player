package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/2/21.
 */

public class SplitSong extends DateRecord implements Serializable {
    int splitId;
    int songId;
    int quantity;

    public int getSplitId() {
        return splitId;
    }
    public void setSplitId(int splitId) {
        this.splitId = splitId;
    }
    public int getSongId() {
        return songId;
    }
    public void setSongId(int songId) {
        this.songId = songId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
