package com.example.mp3player.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DissoCapB on 2017/2/17.
 */

public class DateRecord implements Serializable {
    Date createDate, editDate;
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
}
