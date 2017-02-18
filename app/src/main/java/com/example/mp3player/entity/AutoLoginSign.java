package com.example.mp3player.entity;

import java.io.Serializable;

/**
 * Created by DissoCapB on 2017/2/18.
 */

public class AutoLoginSign implements Serializable {
    private String account;
    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
