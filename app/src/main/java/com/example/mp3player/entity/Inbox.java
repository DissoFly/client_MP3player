package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/3/24.
 */

public class Inbox extends DateRecord implements Serializable {
    private int inboxId;
    private int userId;
    private int geterId;
    private String text;

    public int getInboxId() {
        return inboxId;
    }

    public void setInboxId(int inboxId) {
        this.inboxId = inboxId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGeterId() {
        return geterId;
    }

    public void setGeterId(int geterId) {
        this.geterId = geterId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
