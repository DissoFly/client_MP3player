package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/17.
 */

public class Comment extends DateRecord implements Serializable{

    private int commentId;
    private int songId;
    private String text;
    private int floor;
    private int userId;
    private String userName;
    private String likeIds;
    private boolean isUnlook;
    private boolean isUserLike;

    public boolean isUserLike() {
        return isUserLike;
    }

    public void setUserLike(boolean userLike) {
        isUserLike = userLike;
    }

    public boolean isUnlook() {
        return isUnlook;
    }

    public void setUnlook(boolean unlook) {
        isUnlook = unlook;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLikeIds() {
        return likeIds;
    }

    public void setLikeIds(String likeIds) {
        this.likeIds = likeIds;
    }
}
