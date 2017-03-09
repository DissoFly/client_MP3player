package com.example.mp3player.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DissoCapB on 2017/2/17.
 */
@DatabaseTable
public class DateRecord implements Serializable {
    @DatabaseField
    Date createDate;
    @DatabaseField
    Date editDate;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public void onPreUpdate() {
        editDate = new Date();
    }

    public void onPrePersist() {
        createDate = new Date();
        editDate = new Date();
    }
}
