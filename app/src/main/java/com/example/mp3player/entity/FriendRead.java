package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/21.
 */

public class FriendRead extends DateRecord implements Serializable,Comparable{
    private int readId;
    private int userId;
    private int songId;
    private String userName;
    private String text;
    private boolean isComment;
    private boolean isCommentLike;
    private boolean isMusicLike;

    public int getSongId() {
        return songId;
    }
    public void setSongId(int songId) {
        this.songId = songId;
    }
    public int getReadId() {
        return readId;
    }
    public void setReadId(int readId) {
        this.readId = readId;
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
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public boolean isComment() {
        return isComment;
    }
    public void setComment(boolean isComment) {
        this.isComment = isComment;
    }
    public boolean isCommentLike() {
        return isCommentLike;
    }
    public void setCommentLike(boolean isCommentLike) {
        this.isCommentLike = isCommentLike;
    }
    public boolean isMusicLike() {
        return isMusicLike;
    }
    public void setMusicLike(boolean isMusicLike) {
        this.isMusicLike = isMusicLike;
    }
    @Override
    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        return this.getEditDate().compareTo(((DateRecord) arg0).getEditDate());
    }

}

