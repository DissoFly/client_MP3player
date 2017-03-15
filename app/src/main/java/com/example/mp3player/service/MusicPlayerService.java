package com.example.mp3player.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.example.mp3player.entity.PlayingItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    MediaPlayer player = new MediaPlayer();
    private List<PlayingItem> audioList = null;
    private int listPosition = -1;
    private final IBinder binder = new ServiceBinder();

    boolean isPrepared = false;

    int bufferingProgress = 0;

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("------------MusicPlayerService Building--------------------");
        load();
        if (audioList.size() > 0) {
            listPosition = new Random().nextInt(audioList.size());
            for (int i = 0; i < audioList.size(); i++) {
                try {
                    player.setDataSource(audioList.get(listPosition).getFilePath());
                    break;
                } catch (IOException e) {
                    System.out.println("装载失败" + listPosition);
                    if (listPosition >= 0) {
                        listPosition =
                                listPosition >= audioList.size() - 1 ?
                                        0 : listPosition + 1;
                    }
                }
            }

        }


        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // 装载完毕回调
                isPrepared = true;
                player.start();
            }
        });
        //播放完成事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next();
            }
        });
        //监听事件，网络流媒体的缓冲监听
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                bufferingProgress = i;
            }
        });
        //监听事件，网络流媒体播放结束监听
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

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
    public void setNewMusic(int position, List<PlayingItem> list) {
        listPosition = position;
        audioList = list;
        save();
        start();

    }

    public void setOneAndPlay(PlayingItem playingItem) {
        listPosition =setOneMusic(playingItem);
        save();
        start();
    }

    public int setOneMusic(PlayingItem playingItem) {
        int i=0;
        if (audioList == null) {
            audioList = new ArrayList<>();
        }

        for (PlayingItem playingItem1 : audioList) {
            if (playingItem.getFilePath().equals(playingItem1.getFilePath())) {
                break;
            }
            System.out.println(playingItem.getOnlineSongId()+"++"+playingItem1.getOnlineSongId());
            if (playingItem.getOnlineSongId()==playingItem1.getOnlineSongId()&&playingItem.getOnlineSongId()>0) {
                audioList.get(i).setDownload(true);
                audioList.get(i).setFilePath(playingItem.getFilePath());
                break;
            }
            i++;
        }
        if (audioList.size()<=i)
            audioList.add(playingItem);
        return i;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    //返回列表
    public List<PlayingItem> getAudioList() {
        return audioList;
    }
    public boolean isOnlinePlay(){
        if (audioList.get(listPosition).isOnline()&&!audioList.get(listPosition).isDownload())
            return true;
        else
            return false;
    }
    //返回正在播放音乐所在列表位置
    public int getPlayingListPosition() {
        return listPosition;
    }

    //当前播放位置
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public int getBufferingProgress() {
        return bufferingProgress;
    }

    //总长度
    public int getDuration() {
        return player.getDuration();
    }

    public Bitmap getImg() {
        //        Bitmap bitmap=null;
        //        if (listPosition>=0 ){
        //            try {
        //                Cursor currentCursor = getCursor(audioList.get(listPosition));
        //                int album_id = currentCursor.getInt(currentCursor
        //                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        //                String albumArt = getAlbumArt(album_id);
        //
        //                bitmap = BitmapFactory.decodeFile(albumArt);
        //            }catch (Exception e){
        //                e.printStackTrace();
        //            }
        //        }
        return null;
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
        String[] projection = new String[]{"album_art"};
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
    private void initMediaPlayerAndPlay() {             //初始化
        try {
            isPrepared = false;
            player.reset();
            //File file=new File(audioList.get(listPosition));
            player.setDataSource(audioList.get(listPosition).getFilePath());
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (listPosition >= 0) {
            initMediaPlayerAndPlay();

        }
    }

    public void start(int position) {
        listPosition = position;
        if (listPosition >= 0) {
            initMediaPlayerAndPlay();
        }
    }

    public void stop() {
        listPosition = -1;
    }

    public void playOrPause() {
        if (!isPrepared) {
            initMediaPlayerAndPlay();
        } else {
            if (player.isPlaying()) {
                pause();
            } else {
                play();
            }
        }


    }

    public void play() {
        if (listPosition >= 0)
            player.start();

    }

    public void pause() {
        if (listPosition >= 0)
            player.pause();
    }

    public void next() {
        if (listPosition >= 0) {
            listPosition =
                    listPosition >= audioList.size() - 1 ?
                            0 : listPosition + 1;
            initMediaPlayerAndPlay();
        }
    }

    public void back() {
        if (listPosition >= 0) {
            listPosition =
                    listPosition <= 0 ?
                            audioList.size() - 1 : listPosition - 1;
            initMediaPlayerAndPlay();
        }
    }

    public void seekTo(int i) {
        player.seekTo(i);
    }


    /////////////////////////////////播放方法↑///////////////////////////////////////

    /////////////////////////////////正在播放音乐列表路径储存↓///////////////////////////////////////

    public void save() {                            //写入文件
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("playingMusicData", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            //for (int i = 0; i < audioList.size(); i++) {
            writer.write(new Gson().toJson(audioList));
            // writer.newLine();
            //}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        listPosition = -1;
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        audioList = new ArrayList<>();
        try {
            in = openFileInput("playingMusicData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            //String s1[] = content.toString().split("###");
            // for (int c = 0; c < s1.length; c++) {
            audioList = new Gson().fromJson(content.toString(), new TypeToken<List<PlayingItem>>() {
            }.getType());
            // }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////正在播放音乐列表路径储存↑///////////////////////////////////////


}
