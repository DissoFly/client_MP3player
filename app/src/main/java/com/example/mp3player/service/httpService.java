package com.example.mp3player.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    static String networkAddress=first();

    public static String serverAddress() {
        return "http://"+networkAddress+":8080/musicCenter/";
    }

    public static void serverAddressChanges() {
        load();
    }


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
        return new Request.Builder().url(serverAddress() + "/api/" + path);
    }

    public static void load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        networkAddress = "192.168.253.3";
        try {
            in =new FileInputStream("/data/data/com.example.mp3player/files/networkAddressData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            networkAddress = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String first(){
        String s;
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        s = "192.168.253.3:8080";
        try {
            in =new FileInputStream("/data/data/com.example.mp3player/files/networkAddressData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            s = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
