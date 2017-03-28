package com.example.mp3player.windows;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.service.DownloadService;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.service.MusicPlayerService;
import com.example.mp3player.windows.inputcells.SongPictureFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by DissoCapB on 2017/1/21.
 */

public class PlayingActivity extends Activity implements View.OnClickListener {

    private List<PlayingItem> audioList = null;
    private int listPosition = -1;

    int REFLASH_TIME = 50;

    TextView playingName;
    TextView playingCurrentPosition;
    TextView playingDuration;
    SeekBar musicBar;
    FrameLayout songImg;
    ObjectAnimator rotateAnimation;
    private float currentValue = 0f;
    boolean isMusicBarTouch = false;
    int oneReloadPosition = -1;
    SongPictureFragment songPictureFragment = new SongPictureFragment();

    TextView localOrOnline;
    Button btnDownload;
    Button btnComment;
    Button btnLike;

    ImageView btnPlayChanges;
    ImageView btnPlayBack;
    ImageView btnPlayNext;

    boolean isLrcShow;
    RelativeLayout layoutLrc;
    TextView lrcCenter;
    TextView lrcTop;
    TextView lrcButton;


    List<String> lyricList = null;
    List<String> lyricTimeList = null;
    boolean isLyric = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        playingName = (TextView) findViewById(R.id.text_play_name);
        playingCurrentPosition = (TextView) findViewById(R.id.text_playing_time);
        playingDuration = (TextView) findViewById(R.id.text_max_time);
        musicBar = (SeekBar) findViewById(R.id.play_music_bar);
        songImg = (FrameLayout) findViewById(R.id.frag_song_img);
        localOrOnline = (TextView) findViewById(R.id.text_playing_local_or_online);
        btnDownload = (Button) findViewById(R.id.btn_playing_download);
        btnComment = (Button) findViewById(R.id.btn_playing_comment);
        btnLike = (Button) findViewById(R.id.btn_playing_like);
        layoutLrc = (RelativeLayout) findViewById(R.id.layout_lrc);
        lrcCenter = (TextView) findViewById(R.id.text_lrc_center);
        lrcTop = (TextView) findViewById(R.id.text_lrc_top);
        lrcButton = (TextView) findViewById(R.id.text_lrc_bottom);
        lrcCenter.setText("正在加载歌词");
        layoutLrc.setVisibility(View.INVISIBLE);
        findViewById(R.id.frag_song_img).setVisibility(View.VISIBLE);
        animation();
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_song_img, songPictureFragment).commit();

        initData();

        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isMusicBarTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isMusicBarTouch = false;
                if (listPosition >= 0) {
                    messenger.seekTo(seekBar.getProgress());
                }
            }
        });
    }


    private void initData() {
        findViewById(R.id.btn_playing_back).setOnClickListener(this);
        findViewById(R.id.btn_play_cycle).setOnClickListener(this);
        findViewById(R.id.btn_play_back).setOnClickListener(this);
        findViewById(R.id.btn_play_changes).setOnClickListener(this);
        findViewById(R.id.btn_play_next).setOnClickListener(this);
        findViewById(R.id.btn_play_list).setOnClickListener(this);
        findViewById(R.id.btn_playing_download).setOnClickListener(this);
        findViewById(R.id.btn_playing_comment).setOnClickListener(this);
        findViewById(R.id.btn_playing_like).setOnClickListener(this);
        findViewById(R.id.layout_lrc).setOnClickListener(this);
        findViewById(R.id.frag_song_img).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_playing_back:
                rotateAnimation.start();
                finish();
                break;
            case R.id.btn_play_cycle:
                messenger.nextType();
                break;
            case R.id.btn_play_back:
                messenger.back();
                break;
            case R.id.btn_play_changes:
                messenger.playOrPause();
                break;
            case R.id.btn_play_next:
                messenger.next();
                break;
            case R.id.btn_playing_comment:
                CommentFragment commentFragment = new CommentFragment();
                commentFragment.setSongId(audioList.get(listPosition).getOnlineSongId());
                getFragmentManager().beginTransaction()
                        .replace(R.id.layout_out, commentFragment).addToBackStack(null).commit();
                break;
            case R.id.btn_playing_like:
                setLike();
                break;
            case R.id.btn_play_list:
                getFragmentManager().beginTransaction()
                        .replace(R.id.layout_out, new FooterPlayingListFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_playing_download:
                messengerDownload.downloadMusic(audioList.get(listPosition).getOnlineSongId(), audioList.get(listPosition).getSongName());
                break;
            case R.id.layout_lrc:
                if (isLrcShow) {
                    layoutLrc.setVisibility(View.VISIBLE);
                    findViewById(R.id.frag_song_img).setVisibility(View.INVISIBLE);
                } else {
                    layoutLrc.setVisibility(View.INVISIBLE);
                    findViewById(R.id.frag_song_img).setVisibility(View.VISIBLE);
                }
                isLrcShow = !isLrcShow;
                break;
            case R.id.frag_song_img:
                if (isLrcShow) {
                    layoutLrc.setVisibility(View.VISIBLE);
                    findViewById(R.id.frag_song_img).setVisibility(View.INVISIBLE);
                } else {
                    layoutLrc.setVisibility(View.INVISIBLE);
                    findViewById(R.id.frag_song_img).setVisibility(View.VISIBLE);
                }
                isLrcShow = !isLrcShow;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);

    }

    private void getLike() {
        if (listPosition >= 0) {
            if (audioList.get(listPosition).isOnline()) {
                int userId = -1;
                if (loginMessenger.getUser() != null) {
                    userId = loginMessenger.getUser().getUserId();
                }
                getMusicLikeConnect(userId, audioList.get(listPosition).getOnlineSongId());
            }
        }
    }

    private void setLike() {
        if (listPosition >= 0) {
            if (audioList.get(listPosition).isOnline()) {
                int userId = -1;
                if (loginMessenger.getUser() != null) {
                    userId = loginMessenger.getUser().getUserId();
                }
                setMusiclikeConnect(userId, audioList.get(listPosition).getOnlineSongId());
            }
        }
    }

    private void getMusicLikeConnect(int userId, int onlineSongId) {
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .add("musicId", onlineSongId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("like/getMusicLikeNumber").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();
                    if (data.endsWith("FALSE")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnLike.setText("赞" + "(" + data.replace("FALSE", "") + ")");
                            }
                        });
                    } else if (data.endsWith("TRUE")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnLike.setText("已赞" + "(" + data.replace("TRUE", "") + ")");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnLike.setText("连接错误");
                            }
                        });
                        System.out.println(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMusiclikeConnect(int userId, int onlineSongId) {
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .add("musicId", onlineSongId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("like/setMusicLike").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();
                    if (data.equals("SUCCESS_ADD")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlayingActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                getLike();
                            }
                        });
                    } else if (data.equals("SUCCESS_DELECT")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlayingActivity.this, "取消点赞", Toast.LENGTH_SHORT).show();
                                getLike();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlayingActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                        System.out.println(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getLrc() {
        isLyric = false;
        lrcCenter.setText("正在加载歌词");
        lrcTop.setText("");
        lrcButton.setText("");
        if (listPosition >= 0)
            if (audioList.get(listPosition).isOnline()) {
                int songId = audioList.get(listPosition).getOnlineSongId();
                String path = Environment.getExternalStorageDirectory() + "/MP3player/lrc/" + songId + ".lrc";
                File file = new File(path);
                if (file.exists()) {
                    setLrc(file);
                } else {
                    getLrcConnect(songId);
                }
            }
    }

    void setLrc(File file) {

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "GB2312");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String s = "";
            lyricList = new ArrayList<String>();
            lyricTimeList = new ArrayList<String>();

            while ((s = bufferedReader.readLine()) != null) {

                if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1) || (s.indexOf("[al:") != -1)) {
                    s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
                    lyricTimeList.add("-1");
                } else if (s.indexOf("[") != -1) {
                    String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);

                    String timeStr = (ss.replace("[", "").replace("]", ""));
                    timeStr = timeStr.replace(":", ".");
                    timeStr = timeStr.replace(".", "@");
                    String timeData[] = timeStr.split("@");
                    int minute, second, millisecond;
                    try {
                        minute = Integer.parseInt(timeData[0]);
                        second = Integer.parseInt(timeData[1]);
                        millisecond = Integer.parseInt(timeData[2]);
                        lyricTimeList.add(String.valueOf((minute * 60 + second) * 1000 + millisecond * 10));
                        s = s.replace(ss, "");
                    } catch (NumberFormatException e) {
                        lyricTimeList.add(lyricTimeList.get(lyricTimeList.size() - 1));
                    }

                } else {
                    lyricTimeList.add("-1");
                }
                lyricList.add(s);
            }

            isLyric = true;
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isLyric = false;
        } catch (IOException e) {
            e.printStackTrace();
            lrcCenter.setText("错误，没有读取到歌词");
            isLyric = false;
        }
    }


    void getLrcConnect(final int songId) {
        Request request = HttpService.requestBuilderWithPath("lrc/isHaveLrc/" + songId).get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lrcCenter.setText("连接错误，没有找到歌词");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                String result = "";
                if (data.startsWith("HAVE_LRC:")) {
                    int lrcId = Integer.parseInt(data.replace("HAVE_LRC:", ""));
                    downloadLrc(lrcId, songId);
                } else if (data.startsWith("HAVE_NOT_LRC")) {
                    result = "这首歌还没有歌词";
                } else {
                    System.out.println(data);
                    result = "连接错误，没有找到歌词";
                }
                if (!result.equals("")) {
                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lrcCenter.setText(finalResult);
                        }
                    });
                }
            }
        });
    }

    private void downloadLrc(int lrcId, final int songId) {
        Request request = HttpService.requestBuilderWithPath("lrc/getLrcByLrcId/" + lrcId).get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lrcCenter.setText("下载歌词失败，请检查你的网络");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String path = Environment.getExternalStorageDirectory() + "/MP3player/lrc/" + songId + ".lrc";
                File file = new File(path);
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    setLrc(file);
                    //加载歌词
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lrcCenter.setText("下载歌词失败，未知错误1");
                        }
                    });
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lrcCenter.setText("下载歌词失败，未知错误2");
                            }
                        });
                    }
                }
            }
        });
    }

    int reflashSign = -1;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            audioList = messenger.getAudioList();
            listPosition = messenger.getPlayingListPosition();
            //切换歌时刷新
            if (reflashSign != listPosition) {
                getLrc();
                if (loginBound) {
                    getLike();
                    reflashSign = listPosition;
                }
            }

            if (listPosition == -1) {
                playingName.setText("音乐，让生活更美好");
                playingCurrentPosition.setText("00:00");
                playingDuration.setText("00:00");
                localOrOnline.setText("无");
                btnDownload.setEnabled(false);
                btnDownload.setText("无下载");
            } else {
                playingName.setText(audioList.get(listPosition).getSongName());
                lyricUpdata();
                if (!audioList.get(listPosition).isOnline()) {
                    localOrOnline.setText("本地音乐");
                    btnDownload.setEnabled(false);
                    btnComment.setEnabled(false);
                    btnLike.setVisibility(View.GONE);
                    btnLike.setEnabled(false);
                    btnDownload.setText("本地");
                } else if (audioList.get(listPosition).isOnline() && audioList.get(listPosition).isDownload()) {
                    localOrOnline.setText("网络下载");
                    btnDownload.setEnabled(false);
                    btnComment.setEnabled(true);
                    btnLike.setVisibility(View.VISIBLE);
                    btnLike.setEnabled(true);
                    btnDownload.setText("已下载");
                } else {
                    localOrOnline.setText("网络在线");
                    btnDownload.setEnabled(true);
                    btnComment.setEnabled(true);
                    btnLike.setVisibility(View.VISIBLE);
                    btnLike.setEnabled(true);
                    btnDownload.setText("下载");
                }
                if (messenger.isPlaying())
                    play();
                if (!rotateAnimation.isPaused()) {
                    if (!messenger.isPlaying()) {
                        rotateAnimation.pause();
                    }
                } else {
                    if (messenger.isPlaying()) {
                        rotateAnimation.resume();
                    }
                }
            }

            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };

    private void lyricUpdata() {
        if (isLyric) {
            int[] iTime = new int[lyricTimeList.size()];
            for (int a = 0; a < lyricTimeList.size(); a++) {
                iTime[a] = Integer.parseInt(lyricTimeList.get(a));
            }
            int getLyric = 0;
            for (boolean a = true; a; ) {
                if (getLyric < iTime.length) {
                    if (iTime[getLyric] >= messenger.getCurrentPosition()) {
                        getLyric--;
                        a = false;
                    } else
                        getLyric++;
                } else {
                    getLyric = iTime.length - 1;
                    a = false;
                }
            }
            StringBuilder sb = new StringBuilder();
            StringBuilder sf = new StringBuilder();
            List<String> sb1 = new ArrayList<String>();
            boolean b = false;
            for (int a = getLyric - 1; (a >= 0) && (a > getLyric - 7); a--) {
                sb1.add(lyricList.get(a));
                b = true;
            }
            if (b)
                for (int a = 0; a < sb1.size(); a++)
                    sb.append(sb1.get(sb1.size() - a - 1) + "\n");
            for (int a = getLyric + 1; (a < lyricList.size()) && (a < getLyric + 6); a++)
                sf.append(lyricList.get(a) + "\n");
            lrcCenter.setText(lyricList.get(getLyric));
            lrcTop.setText(sb.length() >= 2 ? sb.substring(0, sb.length() - 2) : sb);
            lrcButton.setText(sf.length() >= 2 ? sf.substring(0, sf.length() - 2) : sf);
        }

    }

    private void play() {
        int max = messenger.getDuration();
        int current = messenger.getCurrentPosition();
        playingCurrentPosition.setText(showTime(current));
        playingDuration.setText(showTime(max));
        musicBar.setMax(max);
        if (messenger.isOnlinePlay())
            musicBar.setSecondaryProgress(messenger.getBufferingProgress() * max / 100);
        else
            musicBar.setSecondaryProgress(max);
        if (!isMusicBarTouch)
            musicBar.setProgress(current);
        if (oneReloadPosition != listPosition) {
            songPictureFragment.setImg(messenger.getImg());
            oneReloadPosition = listPosition;
        }

    }

    public String showTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format("%02d:%02d", minute, second);
    }

    public void animation() {
        rotateAnimation = ObjectAnimator.ofFloat(songImg, "rotation", currentValue - 360, currentValue);
        rotateAnimation.setDuration(30000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimation.start();
        rotateAnimation.pause();
        rotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentValue = (Float) rotateAnimation.getAnimatedValue();
            }
        });
    }

    DownloadService messengerDownload;
    boolean boundDownload;
    private ServiceConnection connectionDownload = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {

            messengerDownload = null;
            boundDownload = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messengerDownload = ((DownloadService.ServicesBinder) service).getService();
            boundDownload = true;
        }
    };

    MusicPlayerService messenger;
    boolean bound;
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = ((MusicPlayerService.ServiceBinder) service).getService();
            handler.postDelayed(runnable, REFLASH_TIME);
            messenger.setMusicDownload();
            bound = true;
            bindService(new Intent(PlayingActivity.this, DownloadService.class), connectionDownload, Context.BIND_AUTO_CREATE);
            bindService(new Intent(PlayingActivity.this, LoginService.class), loginConnection, Context.BIND_AUTO_CREATE);
            getLrc();
        }
    };


    LoginService loginMessenger;
    boolean loginBound;

    private ServiceConnection loginConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            loginMessenger = null;
            loginBound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            loginMessenger = ((LoginService.ServicesBinder) service).getService();
            loginBound = true;
            reflashSign = listPosition;
            getLike();
        }
    };

}


