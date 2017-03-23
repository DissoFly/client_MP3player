package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/23.
 */

public class Friend extends DateRecord implements Serializable {

    private int friendId;
    private int userId;
    private int friendUserId;
    private String friendName;
    private boolean isFollow;
    private boolean isBlacklist;

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isBlacklist() {
        return isBlacklist;
    }

    public void setBlacklist(boolean blacklist) {
        isBlacklist = blacklist;
    }
}
