package com.example.mp3player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by DissoCapB on 2017/1/19.
 */

public class MusicPlayerService extends Service {
    private List<String> audioList = null;
    MediaPlayer player;
    private int musicFile=-1;
    private final IBinder binder=new ServiceBinder();
    String text="000";

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Binding MusicService1", Toast.LENGTH_SHORT).show();
        load();
        if (audioList.size()>0)
            musicFile= new Random().nextInt(audioList.size());
        return binder;
    }

    public class ServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }



    public String getConnect(){
        Toast.makeText(getApplicationContext(), "connect success!", Toast.LENGTH_SHORT).show();
        return text;
    }

    public void setConnect(String i){
        text=i;
    }



    public List<String> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<String> audioList) {
        this.audioList = audioList;
        save();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /////////////////////////////////正在播放音乐列表路径储存↓///////////////////////////////////////

    public void save(){							//写入文件
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("playingMusicData", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            for(int i=0;i<audioList.size();i++){

                writer.write(audioList.get(i)+"###");
                writer.newLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(writer!=null){
                    writer.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void load(){
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        audioList = new ArrayList<String>();
        try{
            in=openFileInput("playingMusicData");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null)
                content.append(line);
            String s1[] =content.toString().split("###");
            for(int c=0;c<s1.length;c++)
                audioList.add(s1[c]);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /////////////////////////////////正在播放音乐列表路径储存↑///////////////////////////////////////
}
