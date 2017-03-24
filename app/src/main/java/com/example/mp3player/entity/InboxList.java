package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/24.
 */

public class InboxList extends DateRecord implements Serializable {
    private int inboxListId;
    private String text;
    private int userId;
    private int friendId;
    private String friendName;
    private int unReadNumber;

    public int getInboxListId() {
        return inboxListId;
    }

    public void setInboxListId(int inboxListId) {
        this.inboxListId = inboxListId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getUnReadNumber() {
        return unReadNumber;
    }

    public void setUnReadNumber(int unReadNumber) {
        this.unReadNumber = unReadNumber;
    }
}
