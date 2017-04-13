package com.example.mp3player.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by DissoCapB on 2017/2/27.
 */

@DatabaseTable
public class Downloading {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String musicName;
    @DatabaseField(canBeNull = false)
    private int musicId;
    @DatabaseField(canBeNull = false)
    private String localPath;
    @DatabaseField(canBeNull = false)
    private long breakPoints;  //断点长度
    @DatabaseField(canBeNull = false)
    private long totalBytes;    //开始下载长度
    @DatabaseField(canBeNull = false)
    private long contentLength; //最长长度

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

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

    public long getBreakPoints() {
        return breakPoints;
    }

    public void setBreakPoints(long breakPoints) {
        this.breakPoints = breakPoints;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}
