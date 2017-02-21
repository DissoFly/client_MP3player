package com.example.mp3player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;

import com.example.mp3player.entity.SplitSong;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/1/19.
 */

public class MusicPlayerService extends Service {
    MediaPlayer player=new MediaPlayer();
    private List<String> audioList = null;
    private int listPosition=-1;
    private final IBinder binder=new ServiceBinder();

    boolean isOnlicePlay=false;


    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("------------MusicPlayerService Building--------------------");
        load();
        if (audioList.size()>0) {
            listPosition = new Random().nextInt(audioList.size());
            initMediaPlayer();
        }
        //播放完成事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                player.seekTo(0);
                next();
            }
        });

        //出错事件
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
        return binder;
    }

    public class ServiceBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }



    //设置新列表
    public void setNewMusic(int position,List<String> list){
        listPosition=position;
        audioList=list;
        save();
        start();

    }

    public boolean isPlaying(){
        return  player.isPlaying();
    }
    //返回列表
    public List<String> getAudioList(){
        List<String> list=new ArrayList<String>();
        for(int i=0;i<audioList.size();i++){
            String s1[] =audioList.get(i).split("/");
            list.add(s1[s1.length-1]);
        }
        return list;
    }
    //返回正在播放音乐所在列表位置
    public int getPlayingListPosition(){
        return listPosition;
    }
    //当前播放位置
    public int getCurrentPosition(){
            return player.getCurrentPosition();
    }
    //总长度
    public int getDuration(){
        return player.getDuration();
    }

    public Bitmap getImg(){
        Bitmap bitmap=null;
        if (listPosition>=0 ){
            try {
                Cursor currentCursor = getCursor(audioList.get(listPosition));
                int album_id = currentCursor.getInt(currentCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String albumArt = getAlbumArt(album_id);

                bitmap = BitmapFactory.decodeFile(albumArt);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    private Cursor getCursor(String filePath) {
        String path = null;
        Cursor c = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (c.moveToFirst()) {
            do {
                // 通过Cursor 获取路径，如果路径相同则break；
                path = c.getString(c
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                // 查找到相同的路径则返回，此时cursorPosition 便是指向路径所指向的Cursor 便可以返回了
                if (path.equals(filePath)) {
                    break;
                }
            } while (c.moveToNext());
        }
        return c;
    }
    private String getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = this.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /////////////////////////////////播放方法↓///////////////////////////////////////
    private void initMediaPlayer(){             //初始化
        try {
            File file=new File(audioList.get(listPosition));
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        player.reset();
        if (listPosition >= 0) {
            initMediaPlayer();
            play();
        }
    }
    public void start(int position){
        listPosition=position;
        player.reset();
        if (listPosition >= 0) {
            initMediaPlayer();
            play();
        }
    }
    public void stop(){
        player.reset();
        listPosition=-1;
    }

    public void playOrPause(){
        if(player.isPlaying())
            pause();
        else
            play();
    }

    public void play() {
        if (listPosition >= 0)
            player.start();
    }

    public void pause() {
        if (listPosition >= 0)
            player.pause();
    }
    public void next(){
        if (listPosition>=0){
            listPosition =
                    listPosition >= audioList.size() - 1 ?
                            0 : listPosition + 1;
            player.reset();
            initMediaPlayer();
            play();
        }
    }
    public void back(){
        if (listPosition>=0){
            listPosition =
                    listPosition <= 0 ?
                            audioList.size() - 1 : listPosition - 1;
            player.reset();
            initMediaPlayer();
            play();
        }
    }

    public void seekTo(int i){
        player.seekTo(i);
    }



    /////////////////////////////////播放方法↑///////////////////////////////////////

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
        listPosition=-1;
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
            for(int c=0;c<s1.length;c++) {
                audioList.add(s1[c]);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /////////////////////////////////正在播放音乐列表路径储存↑///////////////////////////////////////

    SplitSong splitSong;
    public void testConnect(){
        OkHttpClient client = HttpService.getClient();
        Request request = new Request.Builder()
                .url(HttpService.serverAddress + "api/online_play/1")
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                final String data = response.body().string();
                if (data.endsWith("}")) {
                    System.out.println("----------online_play success:" + data);
                    splitSong= new Gson().fromJson(data,SplitSong.class);
                } else {
                    System.out.println("----------online_play fail:" + data);
                }
            }
            @Override
            public void onFailure(Call arg0, IOException arg1) {
                System.out.println("----------online_play fail:" + arg1.toString());
            }
        });
    }

    public void testConnect2(){
        int songId=1;
        File file = new File(Environment.getExternalStorageDirectory() + "/MP3player/music/"+songId);
        if (!file.exists() && !file.isDirectory()) {
            System.out.println("//不存在");
            file.mkdir();
        } else {
            System.out.println("//目录存在");
        }
        testConnect3(1, 0);
    }


    public void testConnect3(final int songId, final int split){
        try {
            OkHttpClient client = HttpService.getClient();
            Request request = new Request.Builder()
                    .url(HttpService.serverAddress + "api/online_play/"+songId+"/"+split)
                    .method("GET", null)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call arg0, Response response) throws IOException {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try
                    {
                        is = response.body().byteStream();
                        File file = new File(Environment.getExternalStorageDirectory() + "/MP3player/music/" + songId, songId + "-" + split + ".mp3");
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        //如果下载文件成功，第一个参数为文件的绝对路径
                        System.out.println("-----------success download"+split);
                        if(split<splitSong.getQuantity())
                            testConnect3(songId,split+1);
                        else
                            merge(songId,splitSong.getQuantity());

                    } catch (IOException e) {
                        System.out.println("-----------fail download" + e);
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call arg0, IOException arg1) {
                    System.out.println("----------online_play fail:" + arg1.toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void merge(int songId,int quantity){
        List<String> srcPaths=new ArrayList<String>();
        System.out.println("----------- merge quantity"+quantity);
        for (int i=0;i<quantity;i++){
            srcPaths.add(Environment.getExternalStorageDirectory() + "/MP3player/music/" + songId+"/"+ songId + "-" + i + ".mp3");
        }
        //合并后的文件名
        String name = "1.mp3";
        //String destName = name.substring(0, name.lastIndexOf("-"));
        //destPath = destPath+destName;//合并后的文件路径

        File destFile = new File(Environment.getExternalStorageDirectory() + "/MP3player/music",name);//合并后的文件
        OutputStream out = null;
        BufferedOutputStream bos = null;
        try {
            out = new FileOutputStream(destFile);
            bos = new BufferedOutputStream(out);
            int i=0;
            for (String src : srcPaths) {
                System.out.println("-----------success merge"+i);
                i++;
                File srcFile = new File(src);
                InputStream in = new FileInputStream(srcFile);
                BufferedInputStream bis = new BufferedInputStream(in);
                byte[] bytes = new byte[1024*128];
                int len = -1;
                while((len = bis.read(bytes))!=-1){
                    bos.write(bytes, 0, len);
                }
                bis.close();
                in.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //关闭流
            try {
                if(bos!=null)bos.close();
                if(out!=null)out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
