package com.example.mp3player.service;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by DissoCapB on 2017/2/9.
 */

public class HttpService {
    static OkHttpClient client;

    public static String serverAddress = "http://192.168.253.3:8080/musicCenter/api";

    static {


        CookieJar cookieJar = new CookieJar() {
            Map<HttpUrl, List<Cookie>> cookiemap = new HashMap<HttpUrl, List<Cookie>>();

            @Override
            public List<Cookie> loadForRequest(HttpUrl key) {
                List<Cookie> cookies = cookiemap.get(key);
                if (cookies == null) {
                    return new ArrayList<Cookie>();
                } else
                    return cookies;
            }

            @Override
            public void saveFromResponse(HttpUrl key, List<Cookie> value) {
                cookiemap.put(key, value);
            }
        };

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();

        client = new OkHttpClient.Builder().cookieJar(cookieJar).cookieJar(new JavaNetCookieJar(cookieManager)).build();
    }

    public static OkHttpClient getClient() {
        return client;
    }

    public static Request.Builder requestBuilderWithPath(String path) {
        return new Request.Builder().url(serverAddress + "/" + path);
    }


}
